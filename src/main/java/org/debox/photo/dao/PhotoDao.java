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
    protected static String SQL_GET_ALL = "SELECT p.id, p.filename, p.title, p.date, p.relative_path, p.album_id, a.owner_id owner_id FROM photos p INNER JOIN albums a ON p.album_id = a.id";
    protected static String SQL_GET_PHOTOS_BY_ALBUM_ID = "SELECT p.id, p.filename, p.title, p.date, p.relative_path, p.album_id, a.owner_id owner_id FROM photos p INNER JOIN albums a ON p.album_id = a.id WHERE album_id = ? ORDER BY date";
    protected static String SQL_GET_VISIBLE_PHOTOS_BY_ALBUM_ID = "SELECT p.id, p.filename, p.title, p.date, p.relative_path, p.album_id, a.owner_id owner_id FROM photos p INNER JOIN albums a ON p.album_id = a.id LEFT JOIN albums_tokens t ON p.album_id = t.album_id WHERE p.album_id = ? AND (t.token_id = ? OR public = 1) ORDER BY date";
    
    protected static String SQL_GET_PHOTO_BY_ID = "SELECT p.id, p.filename, p.title, p.date, p.relative_path, p.album_id, a.owner_id owner_id FROM photos p INNER JOIN albums a ON p.album_id = a.id WHERE p.id = ?";
    protected static String SQL_GET_VISIBLE_PHOTO_BY_ID = ""
            + "(SELECT p.id, p.filename, p.title, p.date, p.relative_path, p.album_id, a.owner_id owner_id FROM photos p INNER JOIN albums a ON p.album_id = a.id LEFT JOIN albums_tokens t ON p.album_id = t.album_id WHERE p.id = ? AND (t.token_id = ? OR public = 1))"
            + " UNION DISTINCT "
            + "(SELECT p.id, p.filename, p.title, p.date, p.relative_path, p.album_id, a.owner_id owner_id FROM photos p INNER JOIN albums a ON p.album_id = a.id LEFT JOIN accounts_accesses aa ON p.album_id = aa.album_id WHERE p.id = ?)";
    protected static String SQL_GET_PHOTO_BY_SOURCE_PATH = "SELECT p.id, p.filename, p.title, p.date, p.relative_path, p.album_id, a.owner_id owner_id FROM photos p INNER JOIN albums a ON p.album_id = a.id WHERE source_path = ?";

    protected static String SQL_INSERT_PHOTO_GENERATION = "INSERT INTO photos_generation VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE time = ?";
    protected static String SQL_GET_PHOTO_GENERATION = "SELECT time FROM photos_generation WHERE id = ? AND size = ?";
    
    protected static UserDao userDao = new UserDao();
    
    public void savePhotoGenerationTime(String id, ThumbnailSize size, long time) throws SQLException {
        Timestamp timestamp = new Timestamp(time);
        QueryRunner queryRunner = new QueryRunner(DatabaseUtils.getDataSource());
        queryRunner.update(SQL_INSERT_PHOTO_GENERATION, id, size.name(), timestamp, timestamp);
    }
    
    public long getGenerationTime(String id, ThumbnailSize size) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DatabaseUtils.getDataSource());
        long result = queryRunner.query(SQL_GET_PHOTO_GENERATION, new ResultSetHandler<Long> () {
            @Override
            public Long handle(ResultSet rs) throws SQLException {
                if (rs.next()) {
                    return rs.getTimestamp(1).getTime();
                }
                return -1L;
            }
        },id, size.name());
        return result;
    }
    
    public List<Photo> getAll() throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DatabaseUtils.getDataSource());
        List<Photo> result = queryRunner.query(SQL_GET_ALL, getBeanListHandler(null));
        return result;
    }
    
    public List<Photo> getPhotos(String albumId) throws SQLException {
        return getPhotos(albumId, null);
    }
    
    public List<Photo> getPhotos(String albumId, String token) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DatabaseUtils.getDataSource());
        List<Photo> result = queryRunner.query(SQL_GET_PHOTOS_BY_ALBUM_ID, getBeanListHandler(token), albumId);
        return result;
    }
    
    public Photo getPhoto(String photoId) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DatabaseUtils.getDataSource());
        Photo result = queryRunner.query(SQL_GET_PHOTO_BY_ID, getBeanHandler(null), photoId);
        return result;
    }
    
    public Photo getVisiblePhoto(String token, String photoId) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DatabaseUtils.getDataSource());
        Photo result = queryRunner.query(SQL_GET_VISIBLE_PHOTO_BY_ID, getBeanHandler(token), photoId, token, photoId);
        return result;
    }

    public void save(List<Photo> photos) throws SQLException {
        QueryRunner queryRunner = new QueryRunner();
        Connection connection = DatabaseUtils.getConnection();
        connection.setAutoCommit(false);
        try {
            Object[][] params = new Object[photos.size()][9];
            int i = 0;
            for (Photo photo : photos) {
                String id = photo.getId();
                if (id == null) {
                    id = StringUtils.randomUUID();
                }
                params[i][0] = id;
                params[i][1] = photo.getFilename();
                params[i][2] = photo.getTitle();
                params[i][3] = new Timestamp(photo.getDate().getTime());
                params[i][4] = photo.getRelativePath();
                params[i][5] = photo.getAlbumId();
                params[i][6] = photo.getAlbumId();
                params[i][7] = photo.getRelativePath();
                params[i][8] = photo.getTitle();
                i++;
            }
            queryRunner.batch(connection, SQL_CREATE_PHOTO, params);
            DbUtils.commitAndCloseQuietly(connection);
        } catch (SQLException ex) {
            DbUtils.rollbackAndCloseQuietly(connection);
            throw ex;
        }
    }

    public void save(Photo photo) throws SQLException {
        String id = photo.getId();
        if (id == null) {
            id = StringUtils.randomUUID();
        }
        
        QueryRunner queryRunner = new QueryRunner(DatabaseUtils.getDataSource());
        // Save the photo
        queryRunner.update(SQL_CREATE_PHOTO,
                id, photo.getFilename(),
                photo.getTitle(),
                new Timestamp(photo.getDate().getTime()),
                photo.getRelativePath(),
                photo.getAlbumId(),
                photo.getAlbumId(),
                photo.getRelativePath(),
                photo.getTitle());

        // Increase photos count for the album containing this photo
        queryRunner.update(SQL_INCREMENT_PHOTO_COUNT, photo.getAlbumId());
    }
    
    public void delete(Photo photo) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DatabaseUtils.getDataSource());
        queryRunner.update(SQL_DELETE_PHOTO, photo.getId());
    }
    
    public void delete(List<Photo> photos) throws SQLException {
        QueryRunner queryRunner = new QueryRunner();
        Connection connection = DatabaseUtils.getConnection();
        connection.setAutoCommit(false);
        try {
            for (Photo photo : photos) {
                queryRunner.update(connection, SQL_DELETE_PHOTO, photo.getId());
            }
            DbUtils.commitAndCloseQuietly(connection);
        } catch (SQLException ex) {
            DbUtils.rollbackAndCloseQuietly(connection);
            throw ex;
        }
    }
        
    protected static RowProcessor getRowProcessor(final String token) {
        Map<String, String> map = new HashMap<>(6);
        map.put("id", "id");
        map.put("filename", "filename");
        map.put("title", "title");
        map.put("relative_path", "relativePath");
        map.put("album_id", "albumId");
        map.put("date", "date");
        map.put("owner_id", "ownerId");
        
        return new BasicRowProcessor(new BeanProcessor(map)) {
            @Override
            public <T> T toBean(ResultSet rs, Class<T> type) throws SQLException {
                T result = super.toBean(rs, type);
                if (result instanceof Photo) {
                    Photo photo = (Photo) result;
                    photo.computeAccessUrl(token);
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
    
    protected static BeanHandler<Photo> getBeanHandler(final String token) {
        return new BeanHandler<>(Photo.class, getRowProcessor(token));
    }
    
    protected static BeanListHandler<Photo> getBeanListHandler(final String token) {
        return new BeanListHandler<>(Photo.class, getRowProcessor(token));
    }
    
}
