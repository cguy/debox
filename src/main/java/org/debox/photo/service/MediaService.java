package org.debox.photo.service;

/*
 * #%L
 * debox-photos
 * %%
 * Copyright (C) 2012 - 2013 Debox
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

import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.sql.SQLException;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.debox.imaging.ImageUtils;
import org.debox.photo.dao.AlbumDao;
import org.debox.photo.dao.VideoDao;
import org.debox.photo.model.Album;
import org.debox.photo.model.Media;
import org.debox.photo.model.Photo;
import org.debox.photo.model.Video;
import org.debox.photo.model.configuration.ThumbnailSize;
import org.debox.photo.model.user.DeboxPermission;
import static org.debox.photo.service.DeboxService.photoDao;
import org.debox.photo.util.SessionUtils;
import org.debux.webmotion.server.render.Render;
import org.debux.webmotion.server.render.RenderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class MediaService extends DeboxService {
    
    private static final Logger log = LoggerFactory.getLogger(MediaService.class);
    
    protected static AlbumDao albumDao = new AlbumDao();
    protected static VideoDao videoDao = new VideoDao();
    
    public Render getPictureStream(String token, String mediaId, String size) throws IOException, SQLException {
        ThumbnailSize thumbnailSize = ThumbnailSize.byName(size);
        if (thumbnailSize == null) {
            thumbnailSize = ThumbnailSize.LARGE;
        }
        
        mediaId = StringUtils.substringBeforeLast(mediaId, ".jpg");
        Media media = getMediaById(mediaId, token);
        if (media == null) {
            return renderError(HttpURLConnection.HTTP_NOT_FOUND);
        }
        
        FileInputStream fis = null;
        try {
            fis = ImageUtils.getStream(media, thumbnailSize);
            RenderStatus status = handleLastModifiedHeader(media, thumbnailSize);
            if (status.getCode() == HttpURLConnection.HTTP_NOT_MODIFIED) {
                return status;
            }
            
        } catch (Exception ex) {
            log.error("Unable to get stream", ex);
        }
        return renderStream(fis, "image/jpeg");
    }
    
    protected Media getMediaById(String mediaId, String token) throws SQLException {
        Media media = photoDao.getPhoto(mediaId);
        if (media == null) {
            media = videoDao.getVideo(mediaId);
        }
        if (media == null) {
            return null;
        }
        
        Album album = albumDao.getAlbum(media.getAlbumId());
        boolean isAutorized = album.isPublicAlbum() || SecurityUtils.getSubject().isPermitted(new DeboxPermission("album", "read", media.getAlbumId()));
        if (!isAutorized) {
            return null;
        }
        return media;
    }
    
    public Render editMedia(String mediaId, String title) throws SQLException {
        // This test can't be put in mapping file
        if (!SessionUtils.isLogged()) {
            return renderError(HttpURLConnection.HTTP_FORBIDDEN, "You must be logged-in.");
        }
        
        Media media = getMediaById(mediaId, null);
        if (media == null) {
            return renderError(HttpURLConnection.HTTP_NOT_FOUND, "There is not any media with id: " + mediaId);
        }
        if (StringUtils.isBlank(title)) {
            title = media.getFilename();
        }
        media.setTitle(title);
        if (media instanceof Video) {
            videoDao.save((Video)media);
        } else {
            photoDao.save((Photo)media);
        }
        return renderStatus(HttpURLConnection.HTTP_NO_CONTENT);
    }
    
}
