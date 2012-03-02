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
package org.debox.photo.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.*;
import org.apache.commons.lang3.tuple.Pair;
import org.debox.photo.job.ImageProcessor;
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
        String path = ImageUtils.getTargetPath(configuration, photo, size);
        if (inProgressPaths.containsKey(path) || Files.exists(Paths.get(path))) {
            return;
        }

        ImageProcessor processor = new ImageProcessor(configuration, photo, size);
        Future<Pair<String, FileInputStream>> future = threadPool.submit(processor);
        inProgressPaths.put(path, future);
    }

    public FileInputStream getStream(Configuration configuration, Photo photo, ThumbnailSize size) throws Exception {
        String path = ImageUtils.getTargetPath(configuration, photo, size);
        FileInputStream fis;

        try {
            fis = new FileInputStream(path);

        } catch (IOException ex) {
            logger.warn("Unable to load stream for path " + path, ex);

            Future<Pair<String, FileInputStream>> future = inProgressPaths.get(path);
            if (future == null) {
                ImageProcessor processor = new ImageProcessor(configuration, photo, size);
                future = threadPool.submit(processor);
                inProgressPaths.put(path, future);
            }
            fis = future.get().getValue();
            inProgressPaths.remove(path);
        }
        return fis;
    }
    
    
    
}
