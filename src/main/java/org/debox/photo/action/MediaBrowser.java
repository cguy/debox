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

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.List;
import org.debox.photo.dao.MediaDao;
import org.debox.photo.model.Album;
import org.debox.photo.model.Photo;
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
        return renderJSON("album", album, "photos", photos, "albums", albums);
    }

    public Render index(String layout) throws IOException {
        return renderView("index.jsp");
    }

    public Render getThumbnail(String photoId) throws FileNotFoundException, SQLException {
        Photo photo = mediaDao.getPhoto(photoId);
        if (photo == null) {
            return renderStatus(HttpURLConnection.HTTP_NOT_FOUND);
        }
        File file = new File(photo.getTargetPath() + File.separatorChar + ImageUtils.THUMBNAIL_PREFIX + photoId + ".jpg");
        return renderStream(new FileInputStream(file), "image/jpeg");
    }

    public Render getPhotoStream(String photoId) throws FileNotFoundException, IOException, SQLException {
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

    public Render getAlbumCover(String albumId) throws SQLException, FileNotFoundException {
        Photo photo = mediaDao.getFirstPhotoByAlbumId(albumId);
        if (photo == null) {
            String missingImagePath = getContext().getServletContext().getRealPath("img/folder.png");
            return renderStream(new FileInputStream(missingImagePath), "image/png");
        }
        String filename = photo.getTargetPath() + File.separatorChar + ImageUtils.THUMBNAIL_PREFIX + photo.getId() + ".jpg";
        return renderStream(new FileInputStream(filename), "image/jpeg");
    }
}
