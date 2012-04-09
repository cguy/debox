/*
 * #%L
 * debox-photos
 * %%
 * Copyright (C) 2012 Debox
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.debox.photo.util.img;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Date;
import java.util.concurrent.*;
import org.apache.commons.lang3.tuple.Pair;
import org.debox.photo.dao.PhotoDao;
import org.debox.photo.model.Configuration;
import org.debox.photo.model.Photo;
import org.debox.photo.model.ThumbnailSize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class ImageHandler {

    private static final Logger logger = LoggerFactory.getLogger(ImageHandler.class);
    protected static ImageHandler instance = new ImageHandler();
    
    protected PhotoDao photoDao = new PhotoDao();
    protected ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    protected ExecutorService threadPool = getExecutorService();
    protected ConcurrentHashMap<String, Future> inProgressPaths = new ConcurrentHashMap<>();

    protected ImageHandler() {
        // Schedule a cleaner for futures collection, every 10 seconds
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                for (String path : inProgressPaths.keySet()) {
                    Future future = inProgressPaths.get(path);
                    if (future.isDone()) {
                        inProgressPaths.remove(path);
                    }
                }
            }
        }, 1, 1, TimeUnit.MINUTES);
    }

    public static ImageHandler getInstance() {
        return instance;
    }

    protected ExecutorService getExecutorService() {
        return Executors.newFixedThreadPool(Math.max(Runtime.getRuntime().availableProcessors() - 2, 1));
    }

    public void abort() {
        threadPool.shutdownNow();
        threadPool = getExecutorService();
    }

    public void generateThumbnail(Configuration configuration, Photo photo, ThumbnailSize size) {
        String sourcePath = configuration.get(Configuration.Key.SOURCE_PATH);
        String targetPath = configuration.get(Configuration.Key.TARGET_PATH);
        
        String path = ImageUtils.getTargetPath(targetPath, photo, size);
        if (inProgressPaths.containsKey(path) || Files.exists(Paths.get(path))) {
            return;
        }

        ThumbnailGenerator processor = new ThumbnailGenerator(sourcePath, photo, targetPath, size);
        Future<Pair<String, FileInputStream>> future = threadPool.submit(processor);
        inProgressPaths.put(path, future);
        try {
            photoDao.savePhotoGenerationTime(photo.getId(), size, new Date().getTime());
        } catch (SQLException ex) {
            logger.error("Unable to save time generation for photo: " + path, ex);
        }
    }

    public FileInputStream getStream(Configuration configuration, Photo photo, ThumbnailSize size) throws Exception {
        String sourcePath = configuration.get(Configuration.Key.SOURCE_PATH);
        String targetPath = configuration.get(Configuration.Key.TARGET_PATH);
        String path = ImageUtils.getTargetPath(targetPath, photo, size);
        FileInputStream fis;

        try {
            fis = new FileInputStream(path);

        } catch (IOException ex) {
            logger.warn("Unable to load stream for path " + path);

            Future<Pair<String, FileInputStream>> future = inProgressPaths.get(path);
            if (future == null) {
                ThumbnailGenerator processor = new ThumbnailGenerator(sourcePath, photo, targetPath, size);
                future = threadPool.submit(processor);
                inProgressPaths.put(path, future);
            }
            fis = future.get().getValue();
            try {
                photoDao.savePhotoGenerationTime(photo.getId(), size, new Date().getTime());
            } catch (SQLException sqle) {
                logger.error("Unable to save time generation for photo: " + path, sqle);
            }
            inProgressPaths.remove(path);
        }
        return fis;
    }
    
    
    
}
