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
package org.debox.photo.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.file.Paths;
import java.sql.SQLException;
import org.apache.shiro.SecurityUtils;
import org.debox.photo.dao.PhotoDao;
import org.debox.photo.job.ImageProcessor;
import org.debox.photo.model.Photo;
import org.debox.photo.model.ThumbnailSize;
import org.debox.photo.server.renderer.FileDownloadRenderer;
import org.debux.webmotion.server.render.Render;
import org.im4java.core.IM4JavaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class PhotoController extends DeboxController {
    
    private static final Logger logger = LoggerFactory.getLogger(PhotoController.class);
    
    protected static PhotoDao photoDao = new PhotoDao();
    
    public Render getThumbnail(String token, String photoId) throws IOException, SQLException {
        Photo photo;
        if (SecurityUtils.getSubject().isAuthenticated()) {
            photo = photoDao.getPhoto(photoId);
        } else {
            photo = photoDao.getVisiblePhoto(token, photoId);
        }
        
        if (photo == null) {
            return renderStatus(HttpURLConnection.HTTP_NOT_FOUND);
        }
        String path = photo.getTargetPath() + File.separatorChar + ThumbnailSize.SQUARE.getPrefix() + photoId + ".jpg";
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(path);
        } catch (IOException e) {
            logger.warn(path + " image doesn't exist, generation in progress.");
            ImageProcessor processor = new ImageProcessor(photo.getSourcePath(), photo.getTargetPath(), photo.getId());
            try {
                fis = processor.generateThumbnail(ThumbnailSize.SQUARE);
            } catch (IOException | IM4JavaException | InterruptedException ex) {
                logger.error("Unable to generate thumbnail", ex);
            }
        }
        return renderStream(fis, "image/jpeg");
    }

    public Render getPhotoStream(String token, String photoId) throws IOException, SQLException {
        Photo photo;
        if (SecurityUtils.getSubject().isAuthenticated()) {
            photo = photoDao.getPhoto(photoId);
        } else {
            photo = photoDao.getVisiblePhoto(token, photoId);
        }
        
        if (photo == null) {
            return renderStatus(HttpURLConnection.HTTP_NOT_FOUND);
        }
        String path = photo.getTargetPath() + File.separatorChar + ThumbnailSize.LARGE.getPrefix() + photoId + ".jpg";
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(path);
        } catch (IOException e) {
            logger.warn(path + " image doesn't exist, generation in progress.");
            ImageProcessor processor = new ImageProcessor(photo.getSourcePath(), photo.getTargetPath(), photo.getId());
            try {
                fis = processor.generateThumbnail(ThumbnailSize.LARGE);
            } catch (IOException | IM4JavaException | InterruptedException ex) {
                logger.error("Unable to generate thumbnail", ex);
            }
        }
        return renderStream(fis, "image/jpeg");
    }
    
    /*
     * TODO [cguy:2012-02-12|15:41] Handle security access for next version.
     */
    public Render download(String photoId, boolean resized) throws SQLException, IOException {
        Photo photo = photoDao.getPhoto(photoId);
        if (photo == null) {
            return renderStatus(HttpURLConnection.HTTP_NOT_FOUND);
        }
        
        if (resized) {
            String path = photo.getTargetPath() + File.separatorChar + ThumbnailSize.LARGE.getPrefix() + photoId + ".jpg";
            return new FileDownloadRenderer(Paths.get(path), ThumbnailSize.LARGE.getPrefix() + photo.getName(), "image/jpeg");
            
        } else {
            return new FileDownloadRenderer(Paths.get(photo.getSourcePath()), photo.getName(), "image/jpeg");
        }
    }

}
