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

import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.debox.photo.dao.AlbumDao;
import org.debox.photo.job.RegenerateThumbnailsJob;
import org.debox.photo.model.Album;
import org.debox.photo.model.Configuration;
import org.debox.photo.model.Photo;
import org.debox.photo.model.ThumbnailSize;
import org.debox.photo.server.ApplicationContext;
import org.debox.photo.server.renderer.ZipDownloadRenderer;
import org.debox.photo.util.img.ImageHandler;
import org.debux.webmotion.server.render.Render;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class AlbumController extends DeboxController {

    private static final Logger logger = LoggerFactory.getLogger(AlbumController.class);
    
    protected static AlbumDao albumDao = new AlbumDao();
    protected RegenerateThumbnailsJob regenerateThumbnailsJob;
    protected ExecutorService threadPool = Executors.newSingleThreadExecutor();

    public Render getAlbums(String token) throws SQLException {
        boolean isAuthenticated = SecurityUtils.getSubject().isAuthenticated();
        List<Album> albums = albumDao.getVisibleAlbums(token, null, isAuthenticated);
        return renderJSON("albums", albums);
    }

    public Render getAlbum(String token, String id) throws IOException, SQLException {
        boolean isAuthenticated = SecurityUtils.getSubject().isAuthenticated();
        Album album;
        if (isAuthenticated) {
            album = albumDao.getAlbum(id);
        } else {
            album = albumDao.getVisibleAlbum(token, id);
        }
        if (album == null) {
            return renderStatus(HttpURLConnection.HTTP_NOT_FOUND);
        }

        List<Photo> photos;
        List<Album> albums;
        Album parent;
        
        if (isAuthenticated) {
            photos = photoDao.getPhotos(album.getId());
            parent = albumDao.getAlbum(album.getParentId());
        } else {
            photos = photoDao.getVisiblePhotos(token, album.getId());
            parent = albumDao.getVisibleAlbum(token, album.getParentId());
        }
        albums = albumDao.getVisibleAlbums(token, album.getId(), isAuthenticated);

        return renderJSON("album", album, "photos", photos, "albums", albums, "parent", parent);
    }

    public Render getAlbumById(String albumId) throws SQLException {
        if (!SecurityUtils.getSubject().isAuthenticated()) {
            return renderStatus(HttpURLConnection.HTTP_FORBIDDEN);
        }
        
        Album album = albumDao.getAlbum(albumId);
        if (album == null) {
            return renderStatus(HttpURLConnection.HTTP_NOT_FOUND);
        }
        return renderJSON(album);
    }

    public Render editAlbum(String id, String name, String visibility, boolean downloadable) throws SQLException {
        if (!SecurityUtils.getSubject().isAuthenticated()) {
            return renderStatus(HttpURLConnection.HTTP_FORBIDDEN);
        }
        
        Album album = albumDao.getAlbum(id);
        if (album == null) {
            return renderStatus(HttpURLConnection.HTTP_NOT_FOUND);
        }

        album.setName(name);
        album.setVisibility(Album.Visibility.valueOf(visibility.toUpperCase()));
        album.setDownloadable(downloadable);

        albumDao.save(album);

        return renderJSON(album);
    }

    public Render setAlbumCover(String albumId, String objectId) throws SQLException, IOException {
        if (!SecurityUtils.getSubject().isAuthenticated()) {
            return renderStatus(HttpURLConnection.HTTP_FORBIDDEN);
        }
        
        if (StringUtils.isEmpty(objectId) && photoDao.getPhoto(objectId) != null) {
            return renderError(HttpURLConnection.HTTP_INTERNAL_ERROR, "The photoId parameter must correspond with a valid photo.");
        }
        
        String id;
        if (objectId.startsWith("a.")) {
            Photo photo = albumDao.getAlbumCover(objectId.substring(2));
            id = photo.getId();
        } else {
            id = objectId;
        }
        
        photoDao.savePhotoGenerationTime("a." + albumId, ThumbnailSize.SQUARE, new Date().getTime());
        albumDao.setAlbumCover(albumId, id);
        return renderStatus(HttpURLConnection.HTTP_OK);
    }

    public Render getAlbumCover(String token, String albumId) throws SQLException, IOException {
        Photo photo;
        if (SecurityUtils.getSubject().isAuthenticated()) {
            photo = albumDao.getAlbumCover(albumId);
        } else {
            photo = albumDao.getVisibleAlbumCover(token, albumId);
        }

        if (photo == null) {
            String missingImagePath = getContext().getServletContext().getRealPath("img/folder.png");
            return renderStream(new FileInputStream(missingImagePath), "image/png");
        }
        
        Album album = albumDao.getAlbum(albumId);
        if (album == null) {
            return renderError(HttpURLConnection.HTTP_NOT_FOUND, "");
        }
        
        Configuration configuration = ApplicationContext.getInstance().getConfiguration();
        FileInputStream fis = null;
        try {
            fis = ImageHandler.getInstance().getStream(configuration, photo, ThumbnailSize.SQUARE);
            
        } catch (Exception ex) {
            logger.error("Unable to get stream", ex);
        }
        
        handleLastModifiedHeader(album);
        return renderStream(fis, "image/jpeg");
    }

    public Render download(String token, String albumId, boolean resized) throws SQLException {
        Album album;
        if (SecurityUtils.getSubject().isAuthenticated()) {
            album = albumDao.getAlbum(albumId);
        } else {
            album = albumDao.getVisibleAlbum(token, albumId);
        }
        
        if (album == null) {
            return renderStatus(HttpURLConnection.HTTP_NOT_FOUND);

        } else if (!album.isDownloadable() && !SecurityUtils.getSubject().isAuthenticated()) {
            return renderStatus(HttpURLConnection.HTTP_FORBIDDEN);
        }

        Configuration configuration = ApplicationContext.getInstance().getConfiguration();
        if (resized) {
            List<Photo> photos = photoDao.getPhotos(album.getId());
            Map<String, String> names = new HashMap<>(photos.size());
            for (Photo photo : photos) {
                names.put(ThumbnailSize.LARGE.getPrefix() + photo.getName(), ThumbnailSize.LARGE.getPrefix() + photo.getName());
            }
            return new ZipDownloadRenderer(configuration.get(Configuration.Key.TARGET_PATH) + album.getRelativePath(), album.getName(), names);
        }
        return new ZipDownloadRenderer(configuration.get(Configuration.Key.SOURCE_PATH) + album.getRelativePath(), album.getName());
    }

    public Render regenerateThumbnails(String albumId) throws SQLException {
        if (!SecurityUtils.getSubject().isAuthenticated()) {
            return renderStatus(HttpURLConnection.HTTP_FORBIDDEN);
        }
        Configuration configuration = ApplicationContext.getInstance().getConfiguration();

        Album album = albumDao.getAlbum(albumId);
        if (album == null) {
            return renderStatus(HttpURLConnection.HTTP_NOT_FOUND);
        }

        String strSource = configuration.get(Configuration.Key.SOURCE_PATH) + album.getRelativePath();
        String strTarget = configuration.get(Configuration.Key.TARGET_PATH) + album.getRelativePath();

        if (StringUtils.isEmpty(strSource) || StringUtils.isEmpty(strTarget)) {
            return renderError(HttpURLConnection.HTTP_CONFLICT, "Work paths are not defined.");
        }

        Path source = Paths.get(strSource);
        Path target = Paths.get(strTarget);

        if (regenerateThumbnailsJob != null && !regenerateThumbnailsJob.isTerminated()) {
            logger.warn("Cannot launch process, it is already running");
        } else {
            if (regenerateThumbnailsJob == null) {
                regenerateThumbnailsJob = new RegenerateThumbnailsJob(source, target);

            } else if (!regenerateThumbnailsJob.getSource().equals(source) || !regenerateThumbnailsJob.getTarget().equals(target)) {
                logger.warn("Aborting sync between {} and {}", regenerateThumbnailsJob.getSource(), regenerateThumbnailsJob.getTarget());
                regenerateThumbnailsJob.abort();
                regenerateThumbnailsJob.setSource(source);
                regenerateThumbnailsJob.setTarget(target);
            }

            threadPool.execute(regenerateThumbnailsJob);
        }

        return renderStatus(HttpURLConnection.HTTP_OK);
    }
    
}
