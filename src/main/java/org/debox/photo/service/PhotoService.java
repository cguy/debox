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
package org.debox.photo.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.debox.imaging.ImageUtils;
import org.debox.photo.dao.AlbumDao;
import org.debox.photo.model.Album;
import org.debox.photo.model.Photo;
import org.debox.photo.model.configuration.ThumbnailSize;
import org.debox.photo.util.SessionUtils;
import org.debux.webmotion.server.render.Render;
import org.debux.webmotion.server.render.RenderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class PhotoService extends DeboxService {

    private static final Logger logger = LoggerFactory.getLogger(PhotoService.class);
    
    protected static AlbumDao albumDao = new AlbumDao();
    
    public Render editPhoto(String id, String title) throws SQLException {
        // This test can't be put in mapping file
        if (!SessionUtils.isLogged(SecurityUtils.getSubject())) {
            return renderError(HttpURLConnection.HTTP_FORBIDDEN, "You must be logged-in.");
        }
        Photo photo = photoDao.getPhoto(id);
        if (photo == null) {
            return renderError(HttpURLConnection.HTTP_NOT_FOUND, "There is not any photo with id: " + id);
        }
        if (StringUtils.isBlank(title)) {
            title = photo.getFilename();
        }
        photo.setTitle(title);
        photoDao.save(photo);
        return renderStatus(HttpURLConnection.HTTP_NO_CONTENT);
        
    }
    
    public Render deletePhoto(String id) throws SQLException {
        Photo photo = photoDao.getPhoto(id);
        if (photo == null) {
            return renderError(HttpURLConnection.HTTP_NOT_FOUND, "There is not any photo with id: " + id);
        }
        
        String originalPath = ImageUtils.getSourcePath(photo);
        try {
            for (ThumbnailSize size : ThumbnailSize.values()) {
                String targetPath = ImageUtils.getThumbnailPath(photo, size);
                Files.deleteIfExists(Paths.get(targetPath));
            }
            Files.deleteIfExists(Paths.get(originalPath));
            photoDao.delete(photo);
            
            Album album = albumDao.getAlbum(photo.getAlbumId());
            while (album != null) {
                album.setPhotosCount(album.getPhotosCount() - 1);
                albumDao.save(album);
                
                album = albumDao.getAlbum(album.getParentId());
            }
            
        } catch (SQLException | IOException ex) {
            logger.error("Unable to delete photo (original file: " + originalPath + ")", ex);
            return renderError(HttpURLConnection.HTTP_INTERNAL_ERROR, "An error has occured during deletion");
        }
        return renderStatus(HttpURLConnection.HTTP_NO_CONTENT);
    }
    
    public Render getThumbnail(String token, String photoId) throws IOException, SQLException {
        photoId = StringUtils.substringBeforeLast(photoId, ".jpg");
        
        Photo photo;
        if (SessionUtils.isAdministrator(SecurityUtils.getSubject())) {
            photo = photoDao.getPhoto(photoId);
        } else {
            photo = photoDao.getVisiblePhoto(token, photoId);
        }
        if (photo == null) {
            return renderError(HttpURLConnection.HTTP_NOT_FOUND);
        }
        FileInputStream fis = null;
        try {
            fis = ImageUtils.getStream(photo, ThumbnailSize.SQUARE);
            RenderStatus status = handleLastModifiedHeader(photo, ThumbnailSize.SQUARE);
            if (status.getCode() == HttpURLConnection.HTTP_NOT_MODIFIED) {
                return status;
            }
            
        } catch (Exception ex) {
            logger.error("Unable to get stream", ex);
        }
        return renderStream(fis, "image/jpeg");
    }

    public Render getPhotoStream(String token, String photoId) throws IOException, SQLException {
        photoId = StringUtils.substringBeforeLast(photoId, ".jpg");       
        
        Photo photo;
        if (SessionUtils.isAdministrator(SecurityUtils.getSubject())) {
            photo = photoDao.getPhoto(photoId);
        } else {
            photo = photoDao.getVisiblePhoto(token, photoId);
        }

        if (photo == null) {
            return renderStatus(HttpURLConnection.HTTP_NOT_FOUND);
        }
        FileInputStream fis = null;
        try {
            fis = ImageUtils.getStream(photo, ThumbnailSize.LARGE);
            RenderStatus status = handleLastModifiedHeader(photo, ThumbnailSize.LARGE);
            if (status.getCode() == HttpURLConnection.HTTP_NOT_MODIFIED) {
                return status;
            }
            
        } catch (Exception ex) {
            logger.error("Unable to get stream", ex);
        }
        return renderStream(fis, "image/jpeg");
    }

}
