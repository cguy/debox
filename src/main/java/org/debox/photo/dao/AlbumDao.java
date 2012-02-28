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
import org.apache.shiro.util.JdbcUtils;
import org.debox.photo.model.Album;
import org.debox.photo.util.SourcePathComparator;
import org.debox.photo.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class AlbumDao extends JdbcMysqlRealm {
    
    private static final Logger logger = LoggerFactory.getLogger(AlbumDao.class);
    
    protected static String SQL_CREATE_ALBUM = "INSERT INTO albums VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE name = ?, visibility = ?, photos_count = ?, downloadable = ?";
    
    protected static String SQL_DELETE_ALBUM = "DELETE FROM albums WHERE id = ?";
    protected static String SQL_GET_ALBUMS = "SELECT id, name, date, photos_count, downloadable, source_path, target_path, parent_id, visibility FROM albums";
    
    protected static String SQL_GET_ROOT_ALBUMS = "SELECT id, name, date, photos_count, downloadable, source_path, target_path, parent_id, visibility FROM albums WHERE parent_id is null";
    protected static String SQL_GET_ROOT_VISIBLE_ALBUMS = ""
            + "SELECT"
            + "    id, name, date, photos_count, downloadable, source_path, target_path, parent_id, visibility "
            + "FROM"
            + "    albums LEFT JOIN albums_tokens ON id = album_id "
            + "WHERE"
            + "    parent_id is null "
            + "    AND ("
            + "        token_id = ?"
            + "        OR visibility = 'public'"
            + "    )";
    
    protected static String SQL_GET_ALBUMS_BY_PARENT_ID = "SELECT id, name, date, photos_count, downloadable, source_path, target_path, parent_id, visibility FROM albums WHERE parent_id = ?";
    protected static String SQL_GET_VISIBLE_ALBUMS_BY_PARENT_ID = ""
            + "SELECT"
            + "    id, name, date,  photos_count, downloadable, source_path, target_path, parent_id, visibility "
            + "FROM"
            + "    albums LEFT JOIN albums_tokens ON id = album_id "
            + "WHERE"
            + "    parent_id = ? "
            + "    AND ("
            + "        token_id = ?"
            + "        OR visibility = 'public'"
            + "    )";
    
    protected static String SQL_GET_ALBUM_BY_ID = "SELECT id, name, date, photos_count, downloadable, source_path, target_path, parent_id, visibility FROM albums WHERE id = ?";
    protected static String SQL_GET_VISIBLE_ALBUM_BY_ID = "SELECT id, name, date, photos_count, downloadable, source_path, target_path, parent_id, visibility FROM albums LEFT JOIN albums_tokens ON id = album_id WHERE id = ? AND ("
            + "        token_id = ?"
            + "        OR visibility = 'public'"
            + "    )";
    
    protected static String SQL_GET_ALBUM_BY_NAME = ""
            + "SELECT"
            + "    id, name, date, photos_count, downloadable, source_path, target_path, parent_id, visibility "
            + "FROM"
            + "    albums LEFT JOIN albums_tokens ON id = album_id "
            + "WHERE"
            + "    name = ?";
    
    protected static String SQL_GET_VISIBLE_ALBUM_BY_NAME = ""
            + "SELECT"
            + "    id, name, date, photos_count, downloadable, source_path, target_path, parent_id, visibility "
            + "FROM"
            + "    albums LEFT JOIN albums_tokens ON id = album_id "
            + "WHERE"
            + "    name = ? "
            + "    AND ("
            + "        token_id = ?"
            + "        OR visibility = 'public'"
            + "    )";
    
    
    protected static String SQL_GET_ALBUM_BY_SOURCE_PATH = "SELECT id, name, date, photos_count, downloadable, source_path, target_path, parent_id, visibility FROM albums WHERE source_path = ?";

    public void save(List<Album> albums) throws SQLException {
        Connection connection = getDataSource().getConnection();
        connection.setAutoCommit(false);
        PreparedStatement statement = null;
        
        Collections.sort(albums, new SourcePathComparator());
        
        try {
            statement = connection.prepareStatement(SQL_CREATE_ALBUM);
            for (Album album : albums) {
                statement.setString(1, album.getId());
                statement.setString(2, album.getName());
                if (album.getDate() != null) {
                    statement.setDate(3, new java.sql.Date(album.getDate().getTime()));
                } else {
                    statement.setDate(3, null);
                }
                statement.setInt(4, album.getPhotosCount());
                statement.setBoolean(5, album.isDownloadable());
                statement.setString(6, album.getSourcePath());
                statement.setString(7, album.getTargetPath());
                statement.setString(8, album.getParentId());
                statement.setString(9, album.getVisibility().name().toLowerCase());
                statement.setString(10, album.getName());
                statement.setString(11, album.getVisibility().name().toLowerCase());
                statement.setInt(12, album.getPhotosCount());
                statement.setBoolean(13, album.isDownloadable());
                statement.addBatch();
            }

            statement.executeBatch();
            connection.commit();
        } finally {
            if (connection != null) {
                connection.rollback();
            }
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
        }
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
            statement.setInt(4, album.getPhotosCount());
            statement.setBoolean(5, album.isDownloadable());
            statement.setString(6, album.getSourcePath());
            statement.setString(7, album.getTargetPath());
            statement.setString(8, album.getParentId());
            statement.setString(9, album.getVisibility().name().toLowerCase());
            statement.setString(10, album.getName());
            statement.setString(11, album.getVisibility().name().toLowerCase());
            statement.setInt(12, album.getPhotosCount());
            statement.setBoolean(13, album.isDownloadable());
            statement.executeUpdate();

        } finally {
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
        }
    }
    
    public void delete(Album album) throws SQLException {
        Connection connection = getDataSource().getConnection();
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL_DELETE_ALBUM);
            statement.setString(1, album.getId());
            statement.executeUpdate();

        } finally {
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
        }
    }
    
    public Album getVisibleAlbum(String token, String albumId) throws SQLException {
        Connection connection = getDataSource().getConnection();
        PreparedStatement statement = connection.prepareStatement(SQL_GET_VISIBLE_ALBUM_BY_ID);
        statement.setString(1, albumId);
        statement.setString(2, token);
        Album result = executeSingleQueryStatement(statement, token);
        return result;
    }
    
    public Album getAlbum(String albumId) throws SQLException {
        Connection connection = getDataSource().getConnection();
        PreparedStatement statement = connection.prepareStatement(SQL_GET_ALBUM_BY_ID);
        statement.setString(1, albumId);
        Album result = executeSingleQueryStatement(statement, null);
        return result;
    }
    
    public Album getAlbumByName(String token, String albumName) throws SQLException {
        Connection connection = getDataSource().getConnection();
        PreparedStatement statement = connection.prepareStatement(SQL_GET_VISIBLE_ALBUM_BY_NAME);
        statement.setString(1, albumName);
        statement.setString(2, token);
        Album result = executeSingleQueryStatement(statement, token);
        return result;
    }
    
    public Album getAlbumByName(String albumName) throws SQLException {
        Connection connection = getDataSource().getConnection();
        PreparedStatement statement = connection.prepareStatement(SQL_GET_ALBUM_BY_NAME);
        statement.setString(1, albumName);
        Album result = executeSingleQueryStatement(statement, null);
        return result;
    }
    
    public List<Album> getAlbums() throws SQLException {
        Connection connection = getDataSource().getConnection();
        PreparedStatement statement = connection.prepareStatement(SQL_GET_ALBUMS);
        List<Album> result = executeListQueryStatement(statement, null);
        Collections.sort(result);
        return result;
    }

    public List<Album> getVisibleAlbums(String token, String parentId, boolean grantedAccess) throws SQLException {
        Connection connection = getDataSource().getConnection();
        PreparedStatement statement = null;
        if (parentId == null && grantedAccess) {
            statement = connection.prepareStatement(SQL_GET_ROOT_ALBUMS);

        } else if (parentId == null) {
            statement = connection.prepareStatement(SQL_GET_ROOT_VISIBLE_ALBUMS);
            statement.setString(1, token);

        } else if (grantedAccess) {
            statement = connection.prepareStatement(SQL_GET_ALBUMS_BY_PARENT_ID);
            statement.setString(1, parentId);

        } else {
            statement = connection.prepareStatement(SQL_GET_VISIBLE_ALBUMS_BY_PARENT_ID);
            statement.setString(1, parentId);
            statement.setString(2, token);
        }
        List<Album> result = this.executeListQueryStatement(statement, token);
        Collections.sort(result);
        return result;
    }

    public Album getAlbumBySourcePath(String sourcePath) throws SQLException {
        Connection connection = getDataSource().getConnection();
        PreparedStatement statement = connection.prepareStatement(SQL_GET_ALBUM_BY_SOURCE_PATH);
        statement.setString(1, sourcePath);
        Album result = executeSingleQueryStatement(statement, null);
        return result;
    }
    
    protected Album convertAlbum(ResultSet resultSet, String token) throws SQLException {
        Album result = new Album();
        result.setId(resultSet.getString(1));
        result.setName(resultSet.getString(2));
        result.setDate(resultSet.getDate(3));
        result.setPhotosCount(resultSet.getInt(4));
        result.setDownloadable(resultSet.getBoolean(5));
        result.setSourcePath(resultSet.getString(6));
        result.setTargetPath(resultSet.getString(7));
        result.setParentId(resultSet.getString(8));
        result.setVisibility(Album.Visibility.valueOf(resultSet.getString(9).toUpperCase()));

        String url = "album/" + result.getId() + "/cover";
        if (token != null) {
            url += "?token=" + token;
        }
        result.setCoverUrl(url);

        return result;
    }

    protected List<Album> executeListQueryStatement(PreparedStatement statement, String token) throws SQLException {
        List<Album> result = new ArrayList<>();
        ResultSet resultSet = null;
        try {
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Album album = convertAlbum(resultSet, token);
                result.add(album);
            }
        } finally {
            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeConnection(statement.getConnection());
            JdbcUtils.closeStatement(statement);
        }
        return result;
    }

    protected Album executeSingleQueryStatement(PreparedStatement statement, String token) throws SQLException {
        Album result = null;
        ResultSet resultSet = null;
        try {
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                result = convertAlbum(resultSet, token);
            }
        } finally {
            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeConnection(statement.getConnection());
            JdbcUtils.closeStatement(statement);
        }
        return result;
    }

}
