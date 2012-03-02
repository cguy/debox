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
import org.debox.photo.model.Configuration;
import org.debox.photo.model.Photo;
import org.debox.photo.model.ThumbnailSize;
import org.debox.photo.server.ApplicationContext;
import org.debox.photo.server.renderer.FileDownloadRenderer;
import org.debox.photo.util.ImageHandler;
import org.debux.webmotion.server.render.Render;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class PhotoController extends DeboxController {

    private static final Logger logger = LoggerFactory.getLogger(PhotoController.class);

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
        Configuration configuration = ApplicationContext.getInstance().getConfiguration();
        FileInputStream fis = null;
        try {
            fis = ImageHandler.getInstance().getStream(configuration, photo, ThumbnailSize.SQUARE);
            handleLastModifiedHeader(photo, ThumbnailSize.SQUARE);
            
        } catch (Exception ex) {
            logger.error("Unable to get stream", ex);
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
        Configuration configuration = ApplicationContext.getInstance().getConfiguration();
        FileInputStream fis = null;
        try {
            fis = ImageHandler.getInstance().getStream(configuration, photo, ThumbnailSize.LARGE);
            handleLastModifiedHeader(photo, ThumbnailSize.LARGE);
            
        } catch (Exception ex) {
            logger.error("Unable to get stream", ex);
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

        Configuration configuration = ApplicationContext.getInstance().getConfiguration();
        if (resized) {
            String path = configuration.get(Configuration.Key.TARGET_PATH) + photo.getRelativePath() + File.separatorChar + ThumbnailSize.LARGE.getPrefix() + photoId + ".jpg";
            return new FileDownloadRenderer(Paths.get(path), ThumbnailSize.LARGE.getPrefix() + photo.getName(), "image/jpeg");

        } else {
            return new FileDownloadRenderer(Paths.get(configuration.get(Configuration.Key.SOURCE_PATH) + photo.getRelativePath()), photo.getName(), "image/jpeg");
        }
    }
}
