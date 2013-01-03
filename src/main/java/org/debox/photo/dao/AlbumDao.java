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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.BeanProcessor;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.RowProcessor;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.util.JdbcUtils;
import org.debox.photo.model.Album;
import org.debox.photo.model.Photo;
import org.debox.photo.model.user.User;
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
    
    protected static String SQL_CREATE_ALBUM = "INSERT INTO albums VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, ?) ON DUPLICATE KEY UPDATE name = ?, description = ?, public = ?, photos_count = ?, downloadable = ?, begin_date = ?, end_date = ?";
    
    protected static String SQL_DELETE_ALBUM = "DELETE FROM albums WHERE id = ?";

    protected static String SQL_GET_ALBUMS = "SELECT id, name, description, begin_date, end_date, photos_count, downloadable, relative_path, parent_id, public, (select count(id) from albums where parent_id = a.id) subAlbumsCount, owner_id FROM albums a ORDER BY begin_date";
    
    protected static String SQL_GET_ROOT_ALBUMS_FOR_ADMIN = "SELECT id, name, description, begin_date, end_date, photos_count, downloadable, relative_path, parent_id, public, (select count(id) from albums where parent_id = a.id) subAlbumsCount, owner_id FROM albums a WHERE parent_id is null ORDER BY begin_date";
    
    protected static String SQL_GET_ROOT_VISIBLE_ALBUMS = ""
            + "SELECT DISTINCT"
            + "    id, name, description, begin_date, end_date, photos_count, downloadable, relative_path, parent_id, public, (select count(id) from albums where parent_id = a.id) subAlbumsCount, owner_id "
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
            + "    id, name, description, begin_date, end_date, photos_count, downloadable, relative_path, parent_id, public, (select count(id) from albums where parent_id = a.id) subAlbumsCount, owner_id "
            + "FROM"
            + "    albums a LEFT JOIN accounts_accesses aa ON a.id = aa.album_id "
            + "WHERE aa.user_id = ? AND a.parent_id IS NULL "
            + "ORDER BY begin_date";
    
    protected static String SQL_GET_ALBUMS_BY_PARENT_ID = "SELECT id, name, description, begin_date, end_date, photos_count, downloadable, relative_path, parent_id, public, (select count(id) from albums where parent_id = a.id) subAlbumsCount, owner_id FROM albums a WHERE parent_id = ? ORDER BY begin_date";
    
    protected static String SQL_GET_ALBUMS_BY_PARENT_ID_FOR_ADMINISTRATOR = "SELECT id, name, description, begin_date, end_date, photos_count, downloadable, relative_path, parent_id, public, (select count(id) from albums where parent_id = a.id) subAlbumsCount, owner_id FROM albums a WHERE parent_id = ?  ORDER BY begin_date";
    
    protected static String SQL_GET_VISIBLE_ALBUMS_BY_PARENT_ID = ""
            + "SELECT DISTINCT"
            + "    id, name, description, begin_date, end_date, photos_count, downloadable, relative_path, parent_id, public, (select count(id) from albums where parent_id = a.id) subAlbumsCount, owner_id "
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
            + "    id, name, description, begin_date, end_date, photos_count, downloadable, relative_path, parent_id, public, (select count(id) from albums where parent_id = a.id) subAlbumsCount, owner_id "
            + "FROM"
            + "    albums a LEFT JOIN accounts_accesses aa ON a.id = aa.album_id "
            + "WHERE"
            + "    parent_id = ? AND (aa.user_id = ? "
            + "    OR public = 1) "
            + "ORDER BY begin_date";
    
    protected static String SQL_GET_PHOTOS_COUNT_BY_PARENT_ID_FOR_LOGGED = ""
            + "SELECT count(*) "
            + "FROM"
            + "    albums a LEFT JOIN accounts_accesses aa ON a.id = aa.album_id "
            + "    INNER JOIN photos p ON a.id = p.album_id "
            + "WHERE"
            + "    a.id = ? AND (aa.user_id = ? "
            + "    OR public = 1) "
            + "ORDER BY begin_date";
    
    protected static String SQL_GET_VISIBLE_PHOTOS_COUNT_BY_ALBUM_ID = ""
            + "SELECT count(*) "
            + "FROM"
            + "    albums a LEFT JOIN albums_tokens ON id = album_id "
            + "    INNER JOIN photos p ON a.id = p.album_id "
            + "WHERE"
            + "    a.id = ? "
            + "    AND ("
            + "        token_id = ?"
            + "        OR public = 1"
            + "    )"
            + "ORDER BY begin_date";
    
    protected static String SQL_GET_ALBUM_BY_ID = "SELECT id, name, description, begin_date, end_date, photos_count, downloadable, relative_path, parent_id, public, (select count(id) from albums where parent_id = a.id) subAlbumsCount, owner_id FROM albums a WHERE id = ?";
    protected static String SQL_GET_VISIBLE_ALBUM_BY_ID = "SELECT id, name, description, begin_date, end_date, photos_count, downloadable, relative_path, parent_id, public, (select count(id) from albums where parent_id = a.id) subAlbumsCount, owner_id FROM albums a LEFT JOIN albums_tokens ON id = album_id WHERE id = ? AND ("
            + "        token_id = ? OR public = 1"
            + "    )";
    protected static String SQL_GET_VISIBLE_ALBUM_BY_ID_LOGGED = "SELECT id, name, description, begin_date, end_date, photos_count, downloadable, relative_path, parent_id, public, (select count(id) from albums where parent_id = a.id) subAlbumsCount, owner_id FROM albums a LEFT JOIN accounts_accesses aa ON a.id = aa.album_id "
            + "WHERE id = ? AND aa.user_id = ?";
    
    protected static String SQL_GET_ALBUM_BY_RELATIVE_PATH = "SELECT id, name, description, begin_date, end_date, photos_count, downloadable, relative_path, parent_id, public, (select count(id) from albums where parent_id = a.id) subAlbumsCount, owner_id FROM albums a WHERE relative_path = ?";
    
    protected static String SQL_GET_CHILDREN_ID = "SELECT id from albums WHERE parent_id = ?";
    
    protected static String SQL_GET_RANDOM_PHOTO = "SELECT id FROM photos WHERE album_id = ? ORDER BY RAND( ) LIMIT 1";

    protected static String SQL_GET_RANDOM_SUB_ALBUM = "SELECT id FROM albums WHERE parent_id = ? ORDER BY RAND( ) LIMIT 1";

    protected static String SQL_UPDATE_ALBUM_COVER = "UPDATE albums SET cover = ? WHERE id = ?";
    
    protected static String SQL_GET_ALBUM_COVER = "SELECT p.id, p.filename, p.title, p.date, p.relative_path, p.album_id, a.owner_id owner_id FROM photos p LEFT JOIN albums a ON a.cover = p.id WHERE a.id = ?";
    
    protected static String SQL_GET_VISIBLE_ALBUM_COVER = ""
            + "(SELECT p.id, p.filename, p.title, p.date, p.relative_path, p.album_id, a.owner_id owner_id "
            + "FROM photos p "
            + "LEFT JOIN albums a ON a.cover = p.id "
            + "LEFT JOIN albums_tokens at ON at.album_id = a.id "
            + "WHERE a.id = ? AND (at.token_id = ? OR a.public = 1)) UNION DISTINCT "
            + "(SELECT p.id, p.filename, p.title, p.date, p.relative_path, p.album_id, a.owner_id owner_id "
            + "FROM photos p "
            + "LEFT JOIN albums a ON a.cover = p.id "
            + "LEFT JOIN accounts_accesses aa ON aa.album_id = a.id "
            + "WHERE a.id = ?)";
    
    protected int getAllPhotosCount(String albumId, Connection c) throws SQLException {
        int result = 0;
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            statement = c.prepareStatement("SELECT photos_count FROM albums WHERE id = ?");
            statement.setString(1, albumId);

            rs = statement.executeQuery();
            if (rs.next()) {
                result += rs.getInt(1);
            }
            
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(statement);
            
            statement = c.prepareStatement(SQL_GET_ALBUMS_BY_PARENT_ID);
            statement.setString(1, albumId);

            rs = statement.executeQuery();
            while (rs.next()) {
                result += getAllPhotosCount(rs.getString("id"), c);
            }
            
        } finally {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(statement);
        }
        return result;
    }
    
    protected int getPhotosCountForLoggedUser(String albumId, String userId, Connection c) throws SQLException {
        int result = 0;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            statement = c.prepareStatement(SQL_GET_PHOTOS_COUNT_BY_PARENT_ID_FOR_LOGGED);
            statement.setString(1, albumId);
            statement.setString(2, userId);

            rs = statement.executeQuery();
            while (rs.next()) {
                result += rs.getInt(1);
            }
            
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(statement);
            
            statement = c.prepareStatement(SQL_GET_VISIBLE_ALBUMS_BY_PARENT_ID_FOR_LOGGED);
            statement.setString(1, albumId);
            statement.setString(2, userId);

            rs = statement.executeQuery();
            while (rs.next()) {
                result += getPhotosCountForLoggedUser(rs.getString("id"), userId, c);
            }
        } finally {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(statement);
        }
        return result;
    }
    
    protected int getVisiblePhotosCount(String albumId, String token, Connection c) throws SQLException {
        int result = 0;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            statement = c.prepareStatement(SQL_GET_VISIBLE_PHOTOS_COUNT_BY_ALBUM_ID);
            statement.setString(1, albumId);
            statement.setString(2, token);

            rs = statement.executeQuery();
            while (rs.next()) {
                result += rs.getInt(1);
            }
            
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(statement);
            
            statement = c.prepareStatement(SQL_GET_VISIBLE_ALBUMS_BY_PARENT_ID);
            statement.setString(1, albumId);
            statement.setString(2, token);

            rs = statement.executeQuery();
            while (rs.next()) {
                result += getVisiblePhotosCount(rs.getString("id"), token, c);
            }
        } finally {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(statement);
        }
        return result;
    }
    
    public void save(List<Album> albums) throws SQLException {
        QueryRunner queryRunner = new QueryRunner();
        Connection connection = DatabaseUtils.getConnection();
        connection.setAutoCommit(false);
        try {
            for (Album album : albums) {
                queryRunner.update(connection, SQL_CREATE_ALBUM, getParamsToSave(album));
            }
            DbUtils.commitAndCloseQuietly(connection);
        } catch (SQLException ex) {
            DbUtils.rollbackAndCloseQuietly(connection);
            throw ex;
        }
    }
    
    public void save(Album album) throws SQLException {
        String id = album.getId();
        if (id == null) {
            album.setId(StringUtils.randomUUID());
        }
        QueryRunner queryRunner = new QueryRunner(DatabaseUtils.getDataSource());
        queryRunner.update(SQL_CREATE_ALBUM, getParamsToSave(album));
    }
    
    protected Object[] getParamsToSave(Album album) throws SQLException {
        Timestamp beginTimestamp = null;
        Timestamp endTimestamp = null;
        if (album.getBeginDate() != null) {
            beginTimestamp = new Timestamp(album.getBeginDate().getTime());
        }
        if (album.getEndDate() != null) {
            endTimestamp = new Timestamp(album.getEndDate().getTime());
        }
        
        Object[] result = new Object[18];
        result[0] = album.getId();
        result[1] = album.getName();
        result[2] = album.getDescription();
        result[3] = beginTimestamp;
        result[4] = endTimestamp;
        result[5] = album.getPhotosCount();
        result[6] = album.isDownloadable();
        result[7] = album.getRelativePath();
        result[8] = album.getParentId();
        result[9] = album.isPublic();
        result[10] = album.getOwnerId();
        result[11] = album.getName();
        result[12] = album.getDescription();
        result[13] = album.isPublic();
        result[14] = album.getPhotosCount();
        result[15] = album.isDownloadable();
        result[16] = beginTimestamp;
        result[17] = endTimestamp;
        return result;
    }
    
    public void delete(Album album) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DatabaseUtils.getDataSource());
        queryRunner.update(SQL_DELETE_ALBUM, album.getId());
    }
    
    public Album getVisibleAlbumForLoggedUser(String userId, String albumId) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DatabaseUtils.getDataSource());
        Connection connection = queryRunner.getDataSource().getConnection();
        Album result = queryRunner.query(SQL_GET_VISIBLE_ALBUM_BY_ID_LOGGED, getBeanHandler(connection, userId, false), albumId, userId);
        return result;
    }
    
    public Album getVisibleAlbum(String token, String albumId) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DatabaseUtils.getDataSource());
        Connection connection = queryRunner.getDataSource().getConnection();
        Album result = queryRunner.query(SQL_GET_VISIBLE_ALBUM_BY_ID, getBeanHandler(connection, token, true), albumId, token);
        return result;
    }
    
    public Album getAlbum(String albumId) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DatabaseUtils.getDataSource());
        Connection connection = queryRunner.getDataSource().getConnection();
        Album result = null;
        try {
            result = queryRunner.query(SQL_GET_ALBUM_BY_ID, getBeanHandler(connection, null, false), albumId);
        } finally {
            DbUtils.closeQuietly(connection);
        }
        return result;
    }
    
    public Album getAlbumByPath(String path) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DatabaseUtils.getDataSource());
        Connection connection = queryRunner.getDataSource().getConnection();
        Album result = null;
        try {
            result = queryRunner.query(SQL_GET_ALBUM_BY_RELATIVE_PATH, getBeanHandler(connection, null, false));
        } finally {
            DbUtils.closeQuietly(connection);
        }
        return result;
    }
   
    public List<Album> getAllAlbums() throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DatabaseUtils.getDataSource());
        Connection connection = queryRunner.getDataSource().getConnection();
        List<Album> result = null;
        try {
            result = queryRunner.query(SQL_GET_ALBUMS, getBeanListHandler(connection, null, false));
        } finally {
            DbUtils.closeQuietly(connection);
        }
        return result;
    }
    
    public List<Album> getAlbums(String parentId) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DatabaseUtils.getDataSource());
        Connection connection = queryRunner.getDataSource().getConnection();
        List<Album> result;
        try {
            if (parentId == null) {
                result = queryRunner.query(SQL_GET_ROOT_ALBUMS_FOR_ADMIN, getBeanListHandler(connection, null, false));
            } else {
                result = queryRunner.query(SQL_GET_ALBUMS_BY_PARENT_ID_FOR_ADMINISTRATOR, getBeanListHandler(connection, null, false), parentId);
            }
        } finally {
            DbUtils.closeQuietly(connection);
        }
        return result;
    }

    public List<Album> getVisibleAlbums(String token, String parentId) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DatabaseUtils.getDataSource());
        Connection connection = queryRunner.getDataSource().getConnection();
        List<Album> result;
        try {
            if (parentId == null) {
                result = queryRunner.query(SQL_GET_ROOT_VISIBLE_ALBUMS, getBeanListHandler(connection, token, true), token);
            } else {
                result = queryRunner.query(SQL_GET_VISIBLE_ALBUMS_BY_PARENT_ID, getBeanListHandler(connection, token, true), parentId, token);
            }
        } finally {
            DbUtils.closeQuietly(connection);
        }
        return result;
    }

    public List<Album> getVisibleAlbumsForLoggedUser(String parentId) throws SQLException {
        String id = ((User) SecurityUtils.getSubject().getPrincipal()).getId();
        QueryRunner queryRunner = new QueryRunner(DatabaseUtils.getDataSource());
        Connection connection = queryRunner.getDataSource().getConnection();
        List<Album> result;
        try {
            if (parentId == null) {
                result = queryRunner.query(SQL_GET_ROOT_VISIBLE_ALBUMS_FOR_LOGGED, getBeanListHandler(connection, id, false), id);
            } else {
                result = queryRunner.query(SQL_GET_VISIBLE_ALBUMS_BY_PARENT_ID_FOR_LOGGED, getBeanListHandler(connection, id, false), parentId, id);
            }
        } finally {
            DbUtils.closeQuietly(connection);
        }
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
        QueryRunner queryRunner = new QueryRunner(DatabaseUtils.getDataSource());
        queryRunner.update(SQL_UPDATE_ALBUM_COVER, photoId, albumId);
        return photoId;
    }

    /**
     * Gets a random subalbum of an album
     * @param albumId
     * @return the id of a andom subalbum
     * @throws SQLException
     */
    protected String getRandomSubAlbumId(String albumId) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DatabaseUtils.getDataSource());
        String result = queryRunner.query(SQL_GET_RANDOM_SUB_ALBUM, new ResultSetHandler<String>() {
            @Override
            public String handle(ResultSet rs) throws SQLException {
                if (rs.next()) {
                    return rs.getString(1);
                }
                return null;
            }
        }, albumId);
        return result;
    }

    /**
     * Get a random photo of an album
     * @param albumId
     * @return the id of a random photo
     * @throws SQLException
     */
    protected String getRandomAlbumPhoto(String albumId) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DatabaseUtils.getDataSource());
        String result = queryRunner.query(SQL_GET_RANDOM_PHOTO, new ResultSetHandler<String>() {
            @Override
            public String handle(ResultSet rs) throws SQLException {
                if (rs.next()) {
                    return rs.getString(1);
                }
                return null;
            }
        }, albumId);
        return result;
    }

    public Photo getAlbumCover(String albumId) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DatabaseUtils.getDataSource());
        Photo result = queryRunner.query(SQL_GET_ALBUM_COVER, PhotoDao.getBeanHandler(null), albumId);
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
        QueryRunner queryRunner = new QueryRunner(DatabaseUtils.getDataSource());
        Photo result = queryRunner.query(SQL_GET_VISIBLE_ALBUM_COVER, PhotoDao.getBeanHandler(token), albumId, token, albumId);
        return result;
    }
    
    protected RowProcessor getRowProcessor(final Connection connection, final String identifier, final boolean isToken) {
        Map<String, String> map = new HashMap<>(6);
        map.put("id", "id");
        map.put("name", "name");
        map.put("description", "description");
        map.put("begin_date", "beginDate");
        map.put("end_date", "endDate");
        map.put("photos_count", "photosCount");
        map.put("relative_path", "relativePath");
        map.put("parent_id", "parentId");
        map.put("public", "isPublic");
        map.put("owner_id", "ownerId");
        map.put("subAlbumsCount", "subAlbumsCount");
        
        return new BasicRowProcessor(new BeanProcessor(map)) {
            @Override
            public <T> T toBean(ResultSet rs, Class<T> type) throws SQLException {
                T result = super.toBean(rs, type);
                if (result instanceof Album) {
                    Album album = (Album) result;
                    String url = "album/" + album.getId() + "-cover.jpg";
                    if (identifier != null && isToken) {
                        url += "?token=" + identifier;
                    }
                    album.setCoverUrl(url);
                    int count;
                    if (isToken) {
                        count = getVisiblePhotosCount(album.getId(), identifier, connection);
                    } else if (identifier != null) {
                        count = getPhotosCountForLoggedUser(album.getId(), identifier, connection);
                    } else {
                        count = getAllPhotosCount(album.getId(), connection);
                    }
                    album.setPhotosCount(count);
                }
                return result;
            }

            @Override
            public <T> List<T> toBeanList(ResultSet rs, Class<T> type) throws SQLException {
                List<T> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(toBean(rs, type));
                }
                return result;
            }
            
        };
    }
    
    protected BeanHandler<Album> getBeanHandler(final Connection connection, final String identifier, final boolean isToken) {
        return new BeanHandler<>(Album.class, getRowProcessor(connection, identifier, isToken));
    }
    
    protected BeanListHandler<Album> getBeanListHandler(final Connection connection, final String identifier, final boolean isToken) {
        return new BeanListHandler<>(Album.class, getRowProcessor(connection, identifier, isToken));
    }
    
}
