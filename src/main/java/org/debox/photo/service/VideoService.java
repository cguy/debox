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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import org.apache.commons.io.FilenameUtils;
import org.apache.shiro.SecurityUtils;
import org.debox.imaging.ImageUtils;
import org.debox.photo.dao.AlbumDao;
import org.debox.photo.model.Album;
import org.debox.photo.model.Photo;
import org.debox.photo.model.Video;
import org.debox.photo.model.configuration.ThumbnailSize;
import org.debox.photo.util.FileUtils;
import org.debox.photo.util.SessionUtils;
import org.debux.webmotion.server.render.Render;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class VideoService extends MediaService {

    private static final Logger log = LoggerFactory.getLogger(VideoService.class);
    
    protected static AlbumDao albumDao = new AlbumDao();
    
    public Render getVideoStream(String token, String filename) throws IOException, SQLException {
        String videoId = FilenameUtils.removeExtension(filename);
        
        Video video;
        if (SessionUtils.isAdministrator(SecurityUtils.getSubject())) {
            video = videoDao.getVideo(videoId);
        } else {
            video = videoDao.getVisibleVideo(token, videoId);
        }
        if (video == null) {
            log.warn("Not any video found with id: {} (given filename: {})", videoId, filename);
            return renderNotFound();
        }
        
        String path = null;
        String extension = FilenameUtils.getExtension(filename);
        switch (extension) {
            case FileUtils.DEFAULT_EXT_VIDEO_OGG:
                path = ImageUtils.getOggSourcePath(video);
                break;
            case FileUtils.DEFAULT_EXT_VIDEO_H264:
                path = ImageUtils.getOggSourcePath(video);
                break;
            case FileUtils.DEFAULT_EXT_VIDEO_WEB_M:
                path = ImageUtils.getOggSourcePath(video);
                break;
        }
        
        if (path == null) {
            return renderNotFound();
        }
        Path videoPath = Paths.get(path);
        return renderStream(new FileInputStream(videoPath.toFile()), FileUtils.getMimeType(extension));
    }
    
    public Render delete(String id) throws SQLException {
        Video media = videoDao.getVideo(id);
        if (media == null) {
            return renderError(HttpURLConnection.HTTP_NOT_FOUND, "There is not any video with id: " + id);
        } else if (!media.getOwnerId().equals(SessionUtils.getUserId()) && !SessionUtils.isAdministrator()) {
            return renderError(HttpURLConnection.HTTP_FORBIDDEN, "You are not allowed to delete this video");
        }
        
        try {
            // Delete cover thumbnails
            for (ThumbnailSize size : ThumbnailSize.values()) {
                String targetPath = ImageUtils.getThumbnailPath(media, size);
                Files.deleteIfExists(Paths.get(targetPath));
            }
            
            Files.deleteIfExists(Paths.get(ImageUtils.getH264SourcePath(media)));
            Files.deleteIfExists(Paths.get(ImageUtils.getH264ThumbnailPath(media)));
            Files.deleteIfExists(Paths.get(ImageUtils.getWebMSourcePath(media)));
            Files.deleteIfExists(Paths.get(ImageUtils.getWebMThumbnailPath(media)));
            Files.deleteIfExists(Paths.get(ImageUtils.getOggSourcePath(media)));
            Files.deleteIfExists(Paths.get(ImageUtils.getOggThumbnailPath(media)));
            
            videoDao.delete(media);
            
            Album album = albumDao.getAlbum(media.getAlbumId());
            while (album != null) {
                album.setVideosCount(album.getVideosCount()- 1);
                albumDao.save(album);
                
                album = albumDao.getAlbum(album.getParentId());
            }
            
        } catch (SQLException | IOException ex) {
            log.error("Unable to delete video", ex);
            return renderError(HttpURLConnection.HTTP_INTERNAL_ERROR, "An error has occured during deletion");
        }
        return renderStatus(HttpURLConnection.HTTP_NO_CONTENT);
    }
    
}
