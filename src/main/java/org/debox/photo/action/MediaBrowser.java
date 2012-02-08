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
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import org.debox.photo.dao.MediaDao;
import org.debox.photo.model.Album;
import org.debox.photo.model.Photo;
import org.debox.photo.server.renderer.FileDownloadRenderer;
import org.debox.photo.server.renderer.ZipDownloadRenderer;
import org.debox.photo.util.ImageUtils;
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.render.Render;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class MediaBrowser extends WebMotionController {

    private static final Logger logger = LoggerFactory.getLogger(MediaBrowser.class);
    protected static MediaDao mediaDao = new MediaDao();

    public Render getAlbums() throws SQLException {
        return renderJSON(
                "albums", mediaDao.getAlbums());
    }

    public Render getAlbum(String albumName) throws IOException, SQLException {
        albumName = URLDecoder.decode(albumName, "UTF-8");
        Album album = mediaDao.getAlbumByName(albumName);
        if (album == null) {
            return renderStatus(HttpURLConnection.HTTP_NOT_FOUND);
        }

        List<Photo> photos = mediaDao.getPhotos(album.getId());
        List<Album> albums = mediaDao.getAlbums(album.getId());
        return renderJSON("album", album, "photos", photos, "albums", albums, "parent", mediaDao.getAlbum(album.getParentId()));
    }
    
    public Render getAlbumById(String albumId) throws SQLException {
        Album album = mediaDao.getAlbum(albumId);
        if (album == null) {
            return renderStatus(HttpURLConnection.HTTP_NOT_FOUND);
        }
        return renderJSON(album);
    }
    
    public Render editAlbum(String id, String name, String visibility) throws SQLException {
        Album album = mediaDao.getAlbum(id);
        if (album == null) {
            return renderStatus(HttpURLConnection.HTTP_NOT_FOUND);
        }
        
        album.setName(name);
        album.setVisibility(Album.Visibility.valueOf(visibility.toUpperCase()));
        
        mediaDao.save(album);
        
        return renderJSON(album);
    }

    public Render getThumbnail(String photoId) throws IOException, SQLException {
        Photo photo = mediaDao.getPhoto(photoId);
        if (photo == null) {
            return renderStatus(HttpURLConnection.HTTP_NOT_FOUND);
        }
        File file = new File(photo.getTargetPath() + File.separatorChar + ImageUtils.THUMBNAIL_PREFIX + photoId + ".jpg");
        return renderStream(new FileInputStream(file), "image/jpeg");
    }

    public Render getPhotoStream(String photoId) throws IOException, SQLException {
        Photo photo = mediaDao.getPhoto(photoId);
        if (photo == null) {
            return renderStatus(HttpURLConnection.HTTP_NOT_FOUND);
        }

        File file = new File(photo.getTargetPath() + File.separatorChar + ImageUtils.LARGE_PREFIX + photoId + ".jpg");
        return renderStream(new FileInputStream(file), "image/jpeg");
    }

    public Render displayPhoto(String photoId) throws IOException, SQLException {
        photoId = URLDecoder.decode(photoId, "UTF-8");

        Photo photo = mediaDao.getPhoto(photoId);
        if (photo == null) {
            return renderStatus(HttpURLConnection.HTTP_NOT_FOUND);
        }

        Album album = mediaDao.getAlbum(photo.getAlbumId());
        logger.info(photo.getAlbumId());
        return renderJSON("album", album, "photo", photo);
    }

    public Render getAlbumCover(String albumId) throws SQLException, IOException {
        Photo photo = mediaDao.getFirstPhotoByAlbumId(albumId);
        if (photo == null) {
            String missingImagePath = getContext().getServletContext().getRealPath("img/folder.png");
            return renderStream(new FileInputStream(missingImagePath), "image/png");
        }
        String filename = photo.getTargetPath() + File.separatorChar + ImageUtils.THUMBNAIL_PREFIX + photo.getId() + ".jpg";
        return renderStream(new FileInputStream(filename), "image/jpeg");
    }

    public Render getOriginalPhotoStream(String photoId) throws SQLException, IOException {
        Photo photo = mediaDao.getPhoto(photoId);
        if (photo == null) {
            return renderStatus(HttpURLConnection.HTTP_NOT_FOUND);
        }
        return new FileDownloadRenderer(Paths.get(photo.getSourcePath()), photo.getName(), "image/jpeg");
    }
    
    public Render getResizedPhotoStream(String photoId) throws SQLException, IOException {
        Photo photo = mediaDao.getPhoto(photoId);
        if (photo == null) {
            return renderStatus(HttpURLConnection.HTTP_NOT_FOUND);
        }
        String path = photo.getTargetPath() + File.separatorChar + ImageUtils.LARGE_PREFIX + photoId + ".jpg";
        return new FileDownloadRenderer(Paths.get(path), ImageUtils.LARGE_PREFIX + photo.getName(), "image/jpeg");
    }
    
    public Render downloadAlbum(String albumId, boolean thumbnails) throws SQLException {
        Album album = mediaDao.getAlbum(albumId);
        if (album == null) {
            return renderStatus(HttpURLConnection.HTTP_NOT_FOUND);
        }
        
        if (thumbnails) {
            return new ZipDownloadRenderer(album.getTargetPath(), album.getName(), new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.startsWith(ImageUtils.LARGE_PREFIX);
                }
            });
        }
        return new ZipDownloadRenderer(album.getSourcePath(), album.getName());
    }
}
