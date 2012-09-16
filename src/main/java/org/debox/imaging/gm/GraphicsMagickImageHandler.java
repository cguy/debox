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
package org.debox.imaging.gm;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Date;
import java.util.concurrent.*;
import org.apache.commons.lang3.tuple.Pair;
import org.debox.imaging.ImageHandler;
import org.debox.imaging.ImageUtils;
import org.debox.photo.dao.PhotoDao;
import org.debox.photo.model.Configuration;
import org.debox.photo.model.Photo;
import org.debox.photo.model.configuration.ThumbnailSize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class GraphicsMagickImageHandler implements ImageHandler {

    private static final Logger logger = LoggerFactory.getLogger(GraphicsMagickImageHandler.class);
    protected static GraphicsMagickImageHandler instance = new GraphicsMagickImageHandler();
    
    public static GraphicsMagickImageHandler getInstance() {
        return instance;
    }

    protected ExecutorService getExecutorService() {
        return Executors.newFixedThreadPool(Math.max(Runtime.getRuntime().availableProcessors() - 2, 1));
    }

//    public void generateThumbnail(Configuration configuration, Photo photo, ThumbnailSize size) {
//        String sourcePath = configuration.get(Configuration.Key.SOURCE_PATH);
//        String targetPath = configuration.get(Configuration.Key.TARGET_PATH);
//        
//        String path = ImageUtils.getTargetPath(targetPath, photo, size);
//        if (inProgressPaths.containsKey(path) || Files.exists(Paths.get(path))) {
//            return;
//        }
//
//        ThumbnailGenerator processor = new ThumbnailGenerator(sourcePath, photo, targetPath, size);
//        Future<Pair<String, FileInputStream>> future = threadPool.submit(processor);
//        inProgressPaths.put(path, future);
//        try {
//            photoDao.savePhotoGenerationTime(photo.getId(), size, new Date().getTime());
//        } catch (SQLException ex) {
//            logger.error("Unable to save time generation for photo: " + path, ex);
//        }
//    }

    @Override
    public void thumbnail(Path source, Path target, ThumbnailSize size) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void thumbnail(Path source, Path target, ThumbnailSize size, boolean async) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
    
}
