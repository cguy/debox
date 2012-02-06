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
package org.debox.photo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.apache.shiro.util.JdbcUtils;
import org.debox.photo.dao.mysql.JdbcMysqlRealm;
import org.debox.photo.model.Album;
import org.debox.photo.model.Photo;
import org.debox.photo.model.Visibility;
import org.debox.photo.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class MediaDao extends JdbcMysqlRealm {

    private static final Logger logger = LoggerFactory.getLogger(MediaDao.class);
    protected static String SQL_CREATE_ALBUM = "INSERT INTO albums VALUES (?, ?, ?, ?, ?, ?, ?)";
    protected static String SQL_GET_ALBUMS = "SELECT id, name, date, source_path, target_path, parent_id, visibility FROM albums";
    protected static String SQL_GET_ROOT_ALBUMS = "SELECT id, name, date, source_path, target_path, parent_id, visibility FROM albums WHERE parent_id is null";
    protected static String SQL_GET_ALBUMS_BY_PARENT_ID = "SELECT id, name, date, source_path, target_path, parent_id, visibility FROM albums WHERE parent_id = ?";
    protected static String SQL_GET_ALBUM_BY_ID = "SELECT id, name, date, source_path, target_path, parent_id, visibility FROM albums WHERE id = ?";
    protected static String SQL_GET_ALBUM_BY_NAME = "SELECT id, name, date, source_path, target_path, parent_id, visibility FROM albums WHERE name = ?";
    protected static String SQL_GET_ALBUM_BY_SOURCE_PATH = "SELECT id, name, date, source_path, target_path, parent_id, visibility FROM albums WHERE source_path = ?";
    protected static String SQL_CREATE_PHOTO = "INSERT INTO photos VALUES (?, ?, ?, ?, ?)";
    
    protected static String SQL_GET_PHOTOS_BY_ALBUM_ID = "SELECT id, name, source_path, target_path, album_id FROM photos WHERE album_id = ?";
    protected static String SQL_GET_PHOTO_BY_ID = "SELECT id, name, source_path, target_path, album_id FROM photos WHERE id = ?";
    protected static String SQL_GET_PHOTO_BY_SOURCE_PATH = "SELECT id, name, source_path, target_path, album_id FROM photos WHERE source_path = ?";
    protected static String SQL_GET_FIRST_PHOTO_BY_ALBUM_ID = SQL_GET_PHOTOS_BY_ALBUM_ID + " LIMIT 1";
    protected static String SQL_GET_PHOTOS_COUNT = "SELECT count(id) FROM photos";

    public List<Album> getAllAlbums() throws SQLException {
        List<Album> result = new ArrayList<>();

        Connection connection = getDataSource().getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL_GET_ALBUMS);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Album album = convertAlbum(resultSet);
                result.add(album);
            }
        } finally {
            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
        }

        Collections.sort(result);
        return result;
    }

    public List<Album> getAlbums() throws SQLException {
        return getAlbums(null);
    }

    public List<Album> getAlbums(String parentId) throws SQLException {
        List<Album> result = new ArrayList<>();

        Connection connection = getDataSource().getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            if (parentId == null) {
                statement = connection.prepareStatement(SQL_GET_ROOT_ALBUMS);
            } else {
                statement = connection.prepareStatement(SQL_GET_ALBUMS_BY_PARENT_ID);
                statement.setString(1, parentId);
            }
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Album album = convertAlbum(resultSet);
                result.add(album);
            }
        } catch (SQLException ex) {
            logger.error(ex.getMessage(), ex);
            throw ex;
        } finally {
            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
        }

        Collections.sort(result);
        return result;
    }

    public Album getAlbumBySourcePath(String sourcePath) throws SQLException {
        Album result = null;

        Connection connection = getDataSource().getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL_GET_ALBUM_BY_SOURCE_PATH);
            statement.setString(1, sourcePath);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                result = convertAlbum(resultSet);
            }
        } finally {
            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
        }

        return result;
    }

    public Photo getPhotoBySourcePath(String sourcePath) throws SQLException {
        Photo result = null;

        Connection connection = getDataSource().getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL_GET_PHOTO_BY_SOURCE_PATH);
            statement.setString(1, sourcePath);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                result = convertPhoto(resultSet);
            }
        } finally {
            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
        }

        return result;
    }

    protected Album convertAlbum(ResultSet resultSet) throws SQLException {
        Album result = new Album();
        result.setId(resultSet.getString(1));
        result.setName(resultSet.getString(2));
        result.setDate(resultSet.getDate(3));
        result.setSourcePath(resultSet.getString(4));
        result.setTargetPath(resultSet.getString(5));
        result.setParentId(resultSet.getString(6));
        result.setVisibility(Visibility.valueOf(resultSet.getString(7).toUpperCase()));

        return result;
    }

    protected Photo convertPhoto(ResultSet resultSet) throws SQLException {
        Photo result = new Photo();
        result.setId(resultSet.getString(1));
        result.setName(resultSet.getString(2));
        result.setSourcePath(resultSet.getString(3));
        result.setTargetPath(resultSet.getString(4));
        result.setAlbumId(resultSet.getString(5));

        return result;
    }

    public void save(Album album) throws SQLException {
        Connection connection = getDataSource().getConnection();
        String id = album.getId();
        if (id == null) {
            id = StringUtils.randomUUID();
        }

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL_CREATE_ALBUM);
            statement.setString(1, id);
            statement.setString(2, album.getName());
            if (album.getDate() != null) {
                statement.setDate(3, new java.sql.Date(album.getDate().getTime()));
            } else {
                statement.setDate(3, null);
            }
            statement.setString(4, album.getSourcePath());
            statement.setString(5, album.getTargetPath());
            statement.setString(6, album.getParentId());
            statement.setString(7, album.getVisibility().name().toLowerCase());
            statement.executeUpdate();

        } finally {
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
        }
    }

    public void save(Photo photo) throws SQLException {
        Connection connection = getDataSource().getConnection();
        String id = photo.getId();
        if (id == null) {
            id = StringUtils.randomUUID();
        }

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL_CREATE_PHOTO);
            statement.setString(1, id);
            statement.setString(2, photo.getName());
            statement.setString(3, photo.getSourcePath());
            statement.setString(4, photo.getTargetPath());
            statement.setString(5, photo.getAlbumId());
            statement.executeUpdate();

        } finally {
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
        }
    }

    public long getPhotosCount() throws SQLException {
        long result = -1;

        Connection connection = getDataSource().getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL_GET_PHOTOS_COUNT);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getLong(1);
            }
        } finally {
            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
        }

        return result;
    }

    public Album getAlbum(String albumId) throws SQLException {
        Album result = null;

        Connection connection = getDataSource().getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL_GET_ALBUM_BY_ID);
            statement.setString(1, albumId);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                result = convertAlbum(resultSet);
            }
        } finally {
            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
        }

        return result;
    }

    public Photo getFirstPhotoByAlbumId(String albumId) throws SQLException {
        Photo result = null;

        Connection connection = getDataSource().getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL_GET_FIRST_PHOTO_BY_ALBUM_ID);
            statement.setString(1, albumId);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                result = convertPhoto(resultSet);
            }
        } finally {
            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
        }

        return result;
    }

    public Album getAlbumByName(String albumName) throws SQLException {
        Album result = null;

        Connection connection = getDataSource().getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL_GET_ALBUM_BY_NAME);
            statement.setString(1, albumName);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                result = convertAlbum(resultSet);
            }
        } finally {
            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
        }

        return result;
    }

    public List<Photo> getPhotos(String albumId) throws SQLException {
        List<Photo> result = new ArrayList<>();

        Connection connection = getDataSource().getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL_GET_PHOTOS_BY_ALBUM_ID);
            statement.setString(1, albumId);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Photo photo = convertPhoto(resultSet);
                result.add(photo);
            }
        } finally {
            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
        }

        Collections.sort(result);
        return result;
    }

    public Photo getPhoto(String photoId) throws SQLException {
        Photo result = null;

        Connection connection = getDataSource().getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL_GET_PHOTO_BY_ID);
            statement.setString(1, photoId);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                result = convertPhoto(resultSet);
            }
        } finally {
            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
        }

        return result;
    }
}
