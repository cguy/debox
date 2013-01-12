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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import org.apache.commons.io.FilenameUtils;
import org.apache.shiro.SecurityUtils;
import org.debox.imaging.ImageUtils;
import org.debox.photo.dao.AlbumDao;
import org.debox.photo.model.Video;
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
    
}
