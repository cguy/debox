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

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.shiro.util.JdbcUtils;
import org.debox.photo.model.Photo;
import org.debox.photo.model.configuration.ThumbnailSize;
import org.debox.photo.util.DatabaseUtils;
import org.debox.photo.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class PhotoDao {

    private static final Logger logger = LoggerFactory.getLogger(PhotoDao.class);
    
    protected static String SQL_CREATE_PHOTO = "INSERT INTO photos VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE album_id = ?, relative_path = ?, title = ?";
    protected static String SQL_INCREMENT_PHOTO_COUNT = "UPDATE albums SET photos_count = photos_count + 1 WHERE id = ?";
    
    protected static String SQL_DELETE_PHOTO = "DELETE FROM photos WHERE id = ?";
    protected static String SQL_GET_ALL = "SELECT id, filename, title, date, relative_path, album_id FROM photos";
    protected static String SQL_GET_PHOTOS_BY_ALBUM_ID = "SELECT id, filename, title, date, relative_path, album_id FROM photos WHERE album_id = ? ORDER BY date";
    protected static String SQL_GET_VISIBLE_PHOTOS_BY_ALBUM_ID = "SELECT p.id, p.filename, p.title, p.date, p.relative_path, p.album_id FROM photos p INNER JOIN albums a ON p.album_id = a.id LEFT JOIN albums_tokens t ON p.album_id = t.album_id WHERE p.album_id = ? AND (t.token_id = ? OR public = 1) ORDER BY date";
    
    protected static String SQL_GET_PHOTO_BY_ID = "SELECT id, filename, title, date, relative_path, album_id FROM photos WHERE id = ?";
    protected static String SQL_GET_VISIBLE_PHOTO_BY_ID = ""
            + "(SELECT p.id, p.filename, p.title, p.date, p.relative_path, p.album_id FROM photos p INNER JOIN albums a ON p.album_id = a.id LEFT JOIN albums_tokens t ON p.album_id = t.album_id WHERE p.id = ? AND (t.token_id = ? OR public = 1))"
            + " UNION DISTINCT "
            + "(SELECT p.id, p.filename, p.title, p.date, p.relative_path, p.album_id FROM photos p INNER JOIN albums a ON p.album_id = a.id LEFT JOIN accounts_accesses aa ON p.album_id = aa.album_id WHERE p.id = ?)";
    protected static String SQL_GET_PHOTO_BY_SOURCE_PATH = "SELECT id, filename, title, date, relative_path, album_id FROM photos WHERE source_path = ?";

    protected static String SQL_INSERT_PHOTO_GENERATION = "INSERT INTO photos_generation VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE time = ?";
    protected static String SQL_GET_PHOTO_GENERATION = "SELECT time FROM photos_generation WHERE id = ? AND size = ?";
    
    public void savePhotoGenerationTime(String id, ThumbnailSize size, long time) throws SQLException {
        Connection connection = DatabaseUtils.getConnection();
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL_INSERT_PHOTO_GENERATION);
            statement.setString(1, id);
            statement.setString(2, size.name());
            statement.setTimestamp(3, new Timestamp(time));
            statement.setTimestamp(4, new Timestamp(time));
            statement.executeUpdate();

        } finally {
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
        }
    }
    
    public long getGenerationTime(String id, ThumbnailSize size) throws SQLException {
        long result = -1;

        Connection connection = DatabaseUtils.getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL_GET_PHOTO_GENERATION);
            statement.setString(1, id);
            statement.setString(2, size.name());
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getTimestamp(1).getTime();
            }
        } finally {
            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
        }

        return result;
    }
    
    public List<Photo> getAll() throws SQLException {
        Connection connection = DatabaseUtils.getConnection();
        PreparedStatement statement = connection.prepareStatement(SQL_GET_ALL);
        List<Photo> result = executeListQueryStatement(statement, null);
        return result;
    }

    public Photo getPhotoBySourcePath(String sourcePath) throws SQLException {
        Photo result = null;

        Connection connection = DatabaseUtils.getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL_GET_PHOTO_BY_SOURCE_PATH);
            statement.setString(1, sourcePath);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                result = convertPhoto(resultSet, null);
            }
        } finally {
            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
        }

        return result;
    }

    protected static Photo convertPhoto(ResultSet resultSet, String token) throws SQLException {
        Photo result = new Photo();
        result.setId(resultSet.getString(1));
        result.setFilename(resultSet.getString(2));
        result.setTitle(resultSet.getString(3));
        result.setDate(resultSet.getTimestamp(4));
        result.setRelativePath(resultSet.getString(5));
        result.setAlbumId(resultSet.getString(6));
        
        // deploy/ is present because of a bug in WebMotion 2.2
        String thumbnail = "deploy/thumbnail/" + result.getId() + ".jpg";
        String url = "deploy/photo/" + result.getId() + ".jpg";
        
        if (token != null) {
            thumbnail += "?token=" + token;
            url += "?token=" + token;
        }
        
        result.setThumbnailUrl(thumbnail);
        result.setUrl(url);

        return result;
    }

    public void save(List<Photo> photos) throws SQLException {
        Connection connection = DatabaseUtils.getConnection();
        connection.setAutoCommit(false);
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL_CREATE_PHOTO);
            for (Photo photo : photos) {
                String id = photo.getId();
                if (id == null) {
                    id = StringUtils.randomUUID();
                }
                statement.setString(1, id);
                statement.setString(2, photo.getFilename());
                statement.setString(3, photo.getTitle());
                statement.setTimestamp(4, new Timestamp(photo.getDate().getTime()));
                statement.setString(5, photo.getRelativePath());
                statement.setString(6, photo.getAlbumId());
                statement.setString(7, photo.getAlbumId());
                statement.setString(8, photo.getRelativePath());
                statement.setString(9, photo.getTitle());
                statement.addBatch();
            }
            statement.executeBatch();
            connection.commit();

        } finally {
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
        }
    }

    public void save(Photo photo) throws SQLException {
        String id = photo.getId();
        if (id == null) {
            id = StringUtils.randomUUID();
        }
        
        QueryRunner queryRunner = new QueryRunner();
        Connection connection = DatabaseUtils.getConnection();
        connection.setAutoCommit(false);
        
        try {
            // Save the photo
            queryRunner.update(connection, SQL_CREATE_PHOTO,
                    id, photo.getFilename(),
                    photo.getTitle(),
                    new Timestamp(photo.getDate().getTime()),
                    photo.getRelativePath(),
                    photo.getAlbumId(),
                    photo.getAlbumId(),
                    photo.getRelativePath(),
                    photo.getTitle());
            
            // Increase photos count for the album containing this photo
            queryRunner.update(connection, SQL_INCREMENT_PHOTO_COUNT, photo.getAlbumId());
            
            DbUtils.commitAndCloseQuietly(connection);
        } catch (SQLException ex) {
            DbUtils.rollbackAndCloseQuietly(connection);
            throw ex;
        }
    }
    
    public void delete(Photo photo) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DatabaseUtils.getDataSource());
        queryRunner.update(SQL_DELETE_PHOTO, photo.getId());
    }
    
    public void delete(List<Photo> photos) throws SQLException {
        String[] ids = new String[photos.size()];
        int i = 0;
        for (Photo photo : photos) {
            ids[i] = photo.getId();
            i++;
        }
        QueryRunner queryRunner = new QueryRunner(DatabaseUtils.getDataSource());
        queryRunner.batch(SQL_DELETE_PHOTO, new Object[][]{ids});
    }

    public List<Photo> getPhotos(String albumId) throws SQLException {
        return getPhotos(albumId, null);
    }
    
    public List<Photo> getPhotos(String albumId, String token) throws SQLException {
        Connection connection = DatabaseUtils.getConnection();
        PreparedStatement statement = connection.prepareStatement(SQL_GET_PHOTOS_BY_ALBUM_ID);
        statement.setString(1, albumId);
        List<Photo> result = executeListQueryStatement(statement, token);
        return result;
    }
    
    public Photo getPhoto(String photoId) throws SQLException {
        Connection connection = DatabaseUtils.getConnection();
        PreparedStatement statement = connection.prepareStatement(SQL_GET_PHOTO_BY_ID);
        statement.setString(1, photoId);
        Photo result = executeSingleQueryStatement(statement, null);
        return result;
    }
    
    public Photo getVisiblePhoto(String token, String photoId) throws SQLException {
        Connection connection = DatabaseUtils.getConnection();
        PreparedStatement statement = connection.prepareStatement(SQL_GET_VISIBLE_PHOTO_BY_ID);
        statement.setString(1, photoId);
        statement.setString(2, token);
        statement.setString(3, photoId);
        Photo result = executeSingleQueryStatement(statement, token);
        return result;
    }
    
    protected static Photo executeSingleQueryStatement(PreparedStatement statement, String token) throws SQLException {
        Photo result = null;
        ResultSet resultSet = null;
        try {
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                result = convertPhoto(resultSet, token);
            }
        } finally {
            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeConnection(statement.getConnection());
            JdbcUtils.closeStatement(statement);
        }
        return result;
    }
    
    protected static List<Photo> executeListQueryStatement(PreparedStatement statement, String token) throws SQLException {
        List<Photo> result = new ArrayList<>();
        ResultSet resultSet = null;
        try {
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Photo photo = convertPhoto(resultSet, token);
                result.add(photo);
            }
        } finally {
            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeConnection(statement.getConnection());
            JdbcUtils.closeStatement(statement);
        }
        return result;
    }

}
