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
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.util.JdbcUtils;
import org.debox.photo.model.Album;
import org.debox.photo.model.Photo;
import org.debox.photo.model.User;
import org.debox.photo.util.DatabaseUtils;
import org.debox.photo.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class AlbumDao {
    
    private static final Logger logger = LoggerFactory.getLogger(AlbumDao.class);
    
    protected static final PhotoDao PHOTO_DAO = new PhotoDao();
    
    protected static String SQL_CREATE_ALBUM = "INSERT INTO albums VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NULL) ON DUPLICATE KEY UPDATE name = ?, description = ?, public = ?, photos_count = ?, downloadable = ?, begin_date = ?, end_date = ?";
    
    protected static String SQL_DELETE_ALBUM = "DELETE FROM albums WHERE id = ?";

    protected static final String SQL_COMPUTED_PHOTOS_COUNT_PART = ""
            + "(SELECT SUM(c) FROM albums a1, "
            + "(SELECT a2.relative_path, a2.photos_count c FROM albums a2 "
            +    "LEFT JOIN albums_tokens at ON a2.id = at.album_id WHERE at.token_id = ? OR a2.public = 1 GROUP BY a2.id) rc"
            + " WHERE rc.relative_path LIKE CONCAT(a.relative_path, '%') AND a1.id = a.id GROUP BY a.id) total_photos_count";

    protected static final String SQL_COMPUTED_PHOTOS_COUNT_PART_LOGGED = ""
            + "(SELECT SUM(c) FROM albums a1, "
            + "(SELECT a2.relative_path, a2.photos_count c FROM albums a2 "
            +    "LEFT JOIN accounts_accesses aa ON a2.id = aa.album_id "
            + "WHERE aa.user_id = ? GROUP BY a2.id) rc"
            + " WHERE rc.relative_path LIKE CONCAT(a.relative_path, '%') AND a1.id = a.id GROUP BY a.id) total_photos_count";

    protected static final String SQL_COMPUTED_PHOTOS_COUNT_PART_ADMIN = ""
            + "(SELECT SUM(c) FROM albums a1, "
            + "(SELECT a2.relative_path, a2.photos_count c FROM albums a2 GROUP BY a2.id) rc"
            + " WHERE rc.relative_path LIKE CONCAT(a.relative_path, '%') AND a1.id = a.id GROUP BY a.id) total_photos_count";

    protected static String SQL_GET_ALBUMS = "SELECT id, name, description, begin_date, end_date, photos_count, downloadable, relative_path, parent_id, public, (select count(id) from albums where parent_id = a.id) subAlbumsCount FROM albums a ORDER BY begin_date";
    
    protected static String SQL_GET_ROOT_ALBUMS_FOR_ADMIN = "SELECT id, name, description, begin_date, end_date, " + SQL_COMPUTED_PHOTOS_COUNT_PART_ADMIN +  ", downloadable, relative_path, parent_id, public, (select count(id) from albums where parent_id = a.id) subAlbumsCount FROM albums a WHERE parent_id is null ORDER BY begin_date";
    
    protected static String SQL_GET_ROOT_VISIBLE_ALBUMS = ""
            + "SELECT DISTINCT"
            + "    id, name, description, begin_date, end_date, " + SQL_COMPUTED_PHOTOS_COUNT_PART +  ", downloadable, relative_path, parent_id, public, (select count(id) from albums where parent_id = a.id) subAlbumsCount "
            + "FROM"
            + "    albums a LEFT JOIN albums_tokens ON id = album_id "
            + "WHERE"
            + "    parent_id is null "
            + "    AND ("
            + "        token_id = ?"
            + "        OR public = 1"
            + "    )"
            + "ORDER BY begin_date";
    
    protected static String SQL_GET_ROOT_VISIBLE_ALBUMS_FOR_LOGGED = ""
            + "SELECT"
            + "    id, name, description, begin_date, end_date, " + SQL_COMPUTED_PHOTOS_COUNT_PART_LOGGED +  ", downloadable, relative_path, parent_id, public, (select count(id) from albums where parent_id = a.id) subAlbumsCount "
            + "FROM"
            + "    albums a LEFT JOIN accounts_accesses aa ON a.id = aa.album_id "
            + "WHERE aa.user_id = ? "
            + "ORDER BY begin_date";
    
    protected static String SQL_GET_ALBUMS_BY_PARENT_ID = "SELECT id, name, description, begin_date, end_date, " + SQL_COMPUTED_PHOTOS_COUNT_PART + ", downloadable, relative_path, parent_id, public, (select count(id) from albums where parent_id = a.id) subAlbumsCount FROM albums a WHERE parent_id = ? ORDER BY begin_date";
    
    protected static String SQL_GET_ALBUMS_BY_PARENT_ID_FOR_ADMINISTRATOR = "SELECT id, name, description, begin_date, end_date, " + SQL_COMPUTED_PHOTOS_COUNT_PART_ADMIN + ", downloadable, relative_path, parent_id, public, (select count(id) from albums where parent_id = a.id) subAlbumsCount FROM albums a WHERE parent_id = ?  ORDER BY begin_date";
    
    protected static String SQL_GET_VISIBLE_ALBUMS_BY_PARENT_ID = ""
            + "SELECT DISTINCT"
            + "    id, name, description, begin_date, end_date, " + SQL_COMPUTED_PHOTOS_COUNT_PART + ", downloadable, relative_path, parent_id, public, (select count(id) from albums where parent_id = a.id) subAlbumsCount "
            + "FROM"
            + "    albums a LEFT JOIN albums_tokens ON id = album_id "
            + "WHERE"
            + "    parent_id = ? "
            + "    AND ("
            + "        token_id = ?"
            + "        OR public = 1"
            + "    )"
            + "ORDER BY begin_date";
    
    protected static String SQL_GET_VISIBLE_ALBUMS_BY_PARENT_ID_FOR_LOGGED = ""
            + "SELECT DISTINCT"
            + "    id, name, description, begin_date, end_date, " + SQL_COMPUTED_PHOTOS_COUNT_PART_LOGGED + ", downloadable, relative_path, parent_id, public, (select count(id) from albums where parent_id = a.id) subAlbumsCount "
            + "FROM"
            + "    albums a LEFT JOIN accounts_accesses aa ON a.id = aa.album_id "
            + "WHERE"
            + "    parent_id = ? "
            + "    OR public = 1 "
            + "ORDER BY begin_date";
    
    protected static String SQL_GET_ALBUM_BY_ID = "SELECT id, name, description, begin_date, end_date, photos_count, downloadable, relative_path, parent_id, public, (select count(id) from albums where parent_id = a.id) subAlbumsCount FROM albums a WHERE id = ?";
    protected static String SQL_GET_VISIBLE_ALBUM_BY_ID = "SELECT id, name, description, begin_date, end_date, " + SQL_COMPUTED_PHOTOS_COUNT_PART + ", downloadable, relative_path, parent_id, public, (select count(id) from albums where parent_id = a.id) subAlbumsCount FROM albums a LEFT JOIN albums_tokens ON id = album_id WHERE id = ? AND ("
            + "        token_id = ? OR public = 1"
            + "    )";
    protected static String SQL_GET_VISIBLE_ALBUM_BY_ID_LOGGED = "SELECT id, name, description, begin_date, end_date, " + SQL_COMPUTED_PHOTOS_COUNT_PART_LOGGED + ", downloadable, relative_path, parent_id, public, (select count(id) from albums where parent_id = a.id) subAlbumsCount FROM albums a LEFT JOIN accounts_accesses aa ON a.id = aa.album_id "
            + "WHERE id = ? OR public = 1";
    
    protected static String SQL_GET_ALBUM_BY_RELATIVE_PATH = "SELECT id, name, description, begin_date, end_date, photos_count, downloadable, relative_path, parent_id, public, (select count(id) from albums where parent_id = a.id) subAlbumsCount FROM albums a WHERE relative_path = ?";
    
    protected static String SQL_GET_CHILDREN_ID = "SELECT id from albums WHERE parent_id = ?";
    
    protected static String SQL_GET_RANDOM_PHOTO = "SELECT id FROM photos WHERE album_id = ? ORDER BY RAND( ) LIMIT 1";

    protected static String SQL_GET_RANDOM_SUB_ALBUM = "SELECT id FROM albums WHERE parent_id = ? ORDER BY RAND( ) LIMIT 1";

    protected static String SQL_UPDATE_ALBUM_COVER = "UPDATE albums SET cover = ? WHERE id = ?";
    
    protected static String SQL_GET_ALBUM_COVER = "SELECT p.id, p.name, p.date, p.relative_path, p.album_id FROM photos p LEFT JOIN albums a ON a.cover = p.id WHERE a.id = ?";
    
    protected static String SQL_GET_VISIBLE_ALBUM_COVER = ""
            + "(SELECT p.id, p.name, p.date, p.relative_path, p.album_id "
            + "FROM photos p "
            + "LEFT JOIN albums a ON a.cover = p.id "
            + "LEFT JOIN albums_tokens at ON at.album_id = a.id "
            + "WHERE a.id = ? AND (at.token_id = ? OR a.public = 1)) UNION DISTINCT "
            + "(SELECT p.id, p.name, p.date, p.relative_path, p.album_id "
            + "FROM photos p "
            + "LEFT JOIN albums a ON a.cover = p.id "
            + "LEFT JOIN accounts_accesses aa ON aa.album_id = a.id "
            + "WHERE a.id = ?)";
    
    public void save(List<Album> albums) throws SQLException {
        Connection connection = DatabaseUtils.getConnection();
        connection.setAutoCommit(false);
        PreparedStatement statement = null;
        
        try {
            statement = connection.prepareStatement(SQL_CREATE_ALBUM);
            for (Album album : albums) {
                statement = prepareAlbumSaveStatement(statement, album);
                statement.addBatch();
            }
            statement.executeBatch();
            connection.commit();
        } finally {
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
        }
    }
    
    public void save(Album album) throws SQLException {
        Connection connection = DatabaseUtils.getConnection();
        String id = album.getId();
        if (id == null) {
            album.setId(StringUtils.randomUUID());
        }
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL_CREATE_ALBUM);
            statement = prepareAlbumSaveStatement(statement, album);
            statement.executeUpdate();

        } finally {
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
        }
    }
    
    protected PreparedStatement prepareAlbumSaveStatement(PreparedStatement statement, Album album) throws SQLException {
        statement.setString(1, album.getId());
        statement.setString(2, album.getName());
        statement.setString(3, album.getDescription());
        Timestamp beginTimestamp = null;
        Timestamp endTimestamp = null;
        if (album.getBeginDate() != null) {
            beginTimestamp = new Timestamp(album.getBeginDate().getTime());
        }
        if (album.getEndDate() != null) {
            endTimestamp = new Timestamp(album.getEndDate().getTime());
        }
        statement.setTimestamp(4, beginTimestamp);
        statement.setTimestamp(5, endTimestamp);
        statement.setInt(6, album.getPhotosCount());
        statement.setBoolean(7, album.isDownloadable());
        statement.setString(8, album.getRelativePath());
        statement.setString(9, album.getParentId());
        statement.setBoolean(10, album.isPublic());
        statement.setString(11, album.getName());
        statement.setString(12, album.getDescription());
        statement.setBoolean(13, album.isPublic());
        statement.setInt(14, album.getPhotosCount());
        statement.setBoolean(15, album.isDownloadable());
        statement.setTimestamp(16, beginTimestamp);
        statement.setTimestamp(17, endTimestamp);

        return statement;
    }
    
    public void delete(Album album) throws SQLException {
        Connection connection = DatabaseUtils.getConnection();
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
    
    public Album getVisibleAlbumForLoggedUser(String userId, String albumId) throws SQLException {
        Connection connection = DatabaseUtils.getConnection();
        PreparedStatement statement = connection.prepareStatement(SQL_GET_VISIBLE_ALBUM_BY_ID_LOGGED);
        statement.setString(1, userId);
        statement.setString(2, albumId);
        Album result = executeSingleQueryStatement(statement, null);
        return result;
    }
    
    public Album getVisibleAlbum(String token, String albumId, boolean grantedAccess) throws SQLException {
        Connection connection = DatabaseUtils.getConnection();
        PreparedStatement statement = connection.prepareStatement(SQL_GET_VISIBLE_ALBUM_BY_ID);
        statement.setString(1, token);
        statement.setString(2, albumId);
        statement.setString(3, token);
        Album result = executeSingleQueryStatement(statement, token);
        return result;
    }
    
    public Album getAlbum(String albumId) throws SQLException {
        Connection connection = DatabaseUtils.getConnection();
        PreparedStatement statement = connection.prepareStatement(SQL_GET_ALBUM_BY_ID);
        statement.setString(1, albumId);
        Album result = executeSingleQueryStatement(statement, null);
        return result;
    }
    
    public Album getAlbumByPath(String path) throws SQLException {
        Connection connection = DatabaseUtils.getConnection();
        PreparedStatement statement = connection.prepareStatement(SQL_GET_ALBUM_BY_RELATIVE_PATH);
        statement.setString(1, path);
        Album result = executeSingleQueryStatement(statement, null);
        return result;
    }
   
    public List<Album> getAllAlbums() throws SQLException {
        Connection connection = DatabaseUtils.getConnection();
        PreparedStatement statement = connection.prepareStatement(SQL_GET_ALBUMS);
        List<Album> result = executeListQueryStatement(statement, null);
        return result;
    }
    
    public List<Album> getAlbums(String parentId) throws SQLException {
        Connection connection = DatabaseUtils.getConnection();
        PreparedStatement statement;
        if (parentId == null) {
            statement = connection.prepareStatement(SQL_GET_ROOT_ALBUMS_FOR_ADMIN);
        } else {
            statement = connection.prepareStatement(SQL_GET_ALBUMS_BY_PARENT_ID_FOR_ADMINISTRATOR);
            statement.setString(1, parentId);
        }

        List<Album> result = this.executeListQueryStatement(statement, null);
        return result;
    }

    public List<Album> getVisibleAlbums(String token, String parentId) throws SQLException {
        Connection connection = DatabaseUtils.getConnection();
        PreparedStatement statement;
        if (parentId == null) {
            statement = connection.prepareStatement(SQL_GET_ROOT_VISIBLE_ALBUMS);
            statement.setString(1, token);
            statement.setString(2, token);

        } else {
            statement = connection.prepareStatement(SQL_GET_VISIBLE_ALBUMS_BY_PARENT_ID);
            statement.setString(1, token);
            statement.setString(2, parentId);
            statement.setString(3, token);
        }
        List<Album> result = this.executeListQueryStatement(statement, token);
        return result;
    }

    public List<Album> getVisibleAlbumsForLoggedUser(String parentId) throws SQLException {
        Connection connection = DatabaseUtils.getConnection();
        PreparedStatement statement;
        String id = ((User) SecurityUtils.getSubject().getPrincipal()).getId();
        if (parentId == null) {
            statement = connection.prepareStatement(SQL_GET_ROOT_VISIBLE_ALBUMS_FOR_LOGGED);
            statement.setString(1, id);
            statement.setString(2, id);

        } else {
            statement = connection.prepareStatement(SQL_GET_VISIBLE_ALBUMS_BY_PARENT_ID_FOR_LOGGED);
            statement.setString(1, id);
            statement.setString(2, parentId);
        }
        List<Album> result = this.executeListQueryStatement(statement, null);
        return result;
    }

    public String setAlbumCover(String albumId, String photoId) throws SQLException {
        //if no photo id is given, then get a random photo
        if (StringUtils.isEmpty(photoId)) {
            photoId = getRandomAlbumPhoto(albumId);
        }
        //if the album has no photo, only subalbums, then get a random subalbum and get its cover
        if (StringUtils.isEmpty(photoId)) {
            String subAlbumId = getRandomSubAlbumId(albumId);
            Photo cover = getAlbumCover(subAlbumId);
            photoId = cover.getId();
        }
        
        Connection connection = DatabaseUtils.getConnection();
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL_UPDATE_ALBUM_COVER);
            statement.setString(1, photoId);
            statement.setString(2, albumId);
            statement.executeUpdate();

        } finally {
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
        }
        return photoId;
    }

    /**
     * Gets a random subalbum of an album
     * @param albumId
     * @return the id of a andom subalbum
     * @throws SQLException
     */
    protected String getRandomSubAlbumId(String albumId) throws SQLException {
        Connection connection = DatabaseUtils.getConnection();
        PreparedStatement statement = null;
        String subAlbumId = null;
        try {
            statement = connection.prepareStatement(SQL_GET_RANDOM_SUB_ALBUM);
            statement.setString(1, albumId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.first()) {
                subAlbumId = resultSet.getString(1);
            }
            JdbcUtils.closeResultSet(resultSet);
            
        } finally {
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
        }
        return subAlbumId;
    }

    /**
     * Get a random photo of an album
     * @param albumId
     * @return the id of a random photo
     * @throws SQLException
     */
    protected String getRandomAlbumPhoto(String albumId) throws SQLException {
        Connection connection = DatabaseUtils.getConnection();
        PreparedStatement statement = null;
        String photoId = null;
        try {
            statement = connection.prepareStatement(SQL_GET_RANDOM_PHOTO);
            statement.setString(1, albumId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.first()) {
                photoId = resultSet.getString(1);
            }
            JdbcUtils.closeResultSet(resultSet);
        } finally {
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
        }
        return photoId;
    }

    public Photo getAlbumCover(String albumId) throws SQLException {
        Connection connection = DatabaseUtils.getConnection();
        PreparedStatement statement = connection.prepareStatement(SQL_GET_ALBUM_COVER);
        statement.setString(1, albumId);
        Photo result = PhotoDao.executeSingleQueryStatement(statement, null);
        if (result == null) {
            Album album = getAlbum(albumId);
            if (album.getPhotosCount() == 0) {
                return null;
            } else {
                setAlbumCover(albumId, null);
                return getAlbumCover(albumId);
            }
        }
        return result;
    }
    
    public Photo getVisibleAlbumCover(String token, String albumId) throws SQLException {
        logger.debug("test ?");
        Connection connection = DatabaseUtils.getConnection();
        PreparedStatement statement = connection.prepareStatement(SQL_GET_VISIBLE_ALBUM_COVER);
        statement.setString(1, albumId);
        statement.setString(2, token);
        statement.setString(3, albumId);
        Photo result = PhotoDao.executeSingleQueryStatement(statement, token);
        return result;
    }
    
    protected Album convertAlbum(ResultSet resultSet, String token) throws SQLException {
        Album result = new Album();
        result.setId(resultSet.getString(1));
        result.setName(resultSet.getString(2));
        result.setDescription(resultSet.getString(3));
        result.setBeginDate(resultSet.getTimestamp(4));
        result.setEndDate(resultSet.getTimestamp(5));
        result.setPhotosCount(resultSet.getInt(6));
        result.setDownloadable(resultSet.getBoolean(7));
        result.setRelativePath(resultSet.getString(8));
        result.setParentId(resultSet.getString(9));
        result.setPublic(resultSet.getBoolean(10));
        result.setSubAlbumsCount(resultSet.getInt(11));
        
        // deploy/ is present because of a bug in WebMotion 2.2
        String url = "deploy/album/" + result.getId() + "-cover.jpg";
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
