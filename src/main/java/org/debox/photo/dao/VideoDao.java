/*
 * #%L
 * debox-videos
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
import org.debox.photo.model.Video;
import org.debox.photo.model.configuration.ThumbnailSize;
import org.debox.photo.util.DatabaseUtils;
import org.debox.photo.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class VideoDao {

    private static final Logger logger = LoggerFactory.getLogger(VideoDao.class);
    
    protected static String SQL_CREATE_VIDEO = "INSERT INTO videos VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE album_id = ?, relative_path = ?, title = ?, thumbnail = ?, ogg = ?, h264 = ?, webm = ?";
    protected static String SQL_INCREMENT_VIDEO_COUNT = "UPDATE albums SET videos_count = videos_count + 1 WHERE id = ?";
    
    protected static String SQL_DELETE_VIDEO = "DELETE FROM videos WHERE id = ?";
    protected static String SQL_GET_ALL = "SELECT p.id, p.filename, p.title, p.date, p.relative_path, p.album_id, p.ogg, p.webm, p.h264, p.thumbnail, a.owner_id owner_id FROM videos p INNER JOIN albums a ON p.album_id = a.id";
    protected static String SQL_GET_VIDEOS_BY_ALBUM_ID = "SELECT p.id, p.filename, p.title, p.date, p.relative_path, p.album_id, p.ogg, p.webm, p.h264, p.thumbnail, a.owner_id owner_id FROM videos p INNER JOIN albums a ON p.album_id = a.id WHERE album_id = ? ORDER BY date";
    protected static String SQL_GET_VISIBLE_VIDEOS_BY_ALBUM_ID = "SELECT p.id, p.filename, p.title, p.date, p.relative_path, p.album_id, p.ogg, p.webm, p.h264, p.thumbnail, a.owner_id owner_id FROM videos p INNER JOIN albums a ON p.album_id = a.id LEFT JOIN albums_tokens t ON p.album_id = t.album_id WHERE p.album_id = ? AND (t.token_id = ? OR public = 1) ORDER BY date";
    
    protected static String SQL_GET_VIDEO_BY_ID = "SELECT p.id, p.filename, p.title, p.date, p.relative_path, p.album_id,p.ogg, p.webm, p.h264, p.thumbnail,  a.owner_id owner_id FROM videos p INNER JOIN albums a ON p.album_id = a.id WHERE p.id = ?";
    protected static String SQL_GET_VISIBLE_VIDEO_BY_ID = ""
            + "(SELECT p.id, p.filename, p.title, p.date, p.relative_path, p.album_id,p.ogg, p.webm, p.h264, p.thumbnail,  a.owner_id owner_id FROM videos p INNER JOIN albums a ON p.album_id = a.id LEFT JOIN albums_tokens t ON p.album_id = t.album_id WHERE p.id = ? AND (t.token_id = ? OR public = 1))"
            + " UNION DISTINCT "
            + "(SELECT p.id, p.filename, p.title, p.date, p.relative_path, p.album_id,p.ogg, p.webm, p.h264, p.thumbnail,  a.owner_id owner_id FROM videos p INNER JOIN albums a ON p.album_id = a.id LEFT JOIN accounts_accesses aa ON p.album_id = aa.album_id WHERE p.id = ?)";
    protected static String SQL_GET_VIDEO_BY_SOURCE_PATH = "SELECT p.id, p.filename, p.title, p.date, p.relative_path, p.album_id, p.ogg, p.webm, p.h264, p.thumbnail, a.owner_id owner_id FROM videos p INNER JOIN albums a ON p.album_id = a.id WHERE source_path = ?";

    protected static String SQL_INSERT_VIDEO_GENERATION = "INSERT INTO videos_generation VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE time = ?";
    protected static String SQL_GET_VIDEO_GENERATION = "SELECT time FROM videos_generation WHERE id = ? AND size = ?";
    
    protected static UserDao userDao = new UserDao();
    
    public void saveVideoGenerationTime(String id, ThumbnailSize size, long time) throws SQLException {
        Timestamp timestamp = new Timestamp(time);
        QueryRunner queryRunner = new QueryRunner(DatabaseUtils.getDataSource());
        queryRunner.update(SQL_INSERT_VIDEO_GENERATION, id, size.name(), timestamp, timestamp);
    }
    
    public long getGenerationTime(String id, ThumbnailSize size) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DatabaseUtils.getDataSource());
        long result = queryRunner.query(SQL_GET_VIDEO_GENERATION, new ResultSetHandler<Long> () {
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
    
    public List<Video> getAll() throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DatabaseUtils.getDataSource());
        List<Video> result = queryRunner.query(SQL_GET_ALL, getBeanListHandler(null));
        return result;
    }
    
    public List<Video> getVideos(String albumId) throws SQLException {
        return getVideos(albumId, null);
    }
    
    public List<Video> getVideos(String albumId, String token) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DatabaseUtils.getDataSource());
        List<Video> result = queryRunner.query(SQL_GET_VIDEOS_BY_ALBUM_ID, getBeanListHandler(token), albumId);
        return result;
    }
    
    public Video getVideo(String videoId) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DatabaseUtils.getDataSource());
        Video result = queryRunner.query(SQL_GET_VIDEO_BY_ID, getBeanHandler(null), videoId);
        return result;
    }
    
    public Video getVisibleVideo(String token, String videoId) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DatabaseUtils.getDataSource());
        Video result = queryRunner.query(SQL_GET_VISIBLE_VIDEO_BY_ID, getBeanHandler(token), videoId, token, videoId);
        return result;
    }

    public void save(List<Video> videos) throws SQLException {
        QueryRunner queryRunner = new QueryRunner();
        Connection connection = DatabaseUtils.getConnection();
        connection.setAutoCommit(false);
        try {
            Object[][] params = new Object[videos.size()][17];
            int i = 0;
            for (Video video : videos) {
                String id = video.getId();
                if (id == null) {
                    id = StringUtils.randomUUID();
                }
                params[i][0] = id;
                params[i][1] = video.getFilename();
                params[i][2] = video.getTitle();
                params[i][3] = new Timestamp(video.getDate().getTime());
                params[i][4] = video.getRelativePath();
                params[i][5] = video.hasThumbnail();
                params[i][6] = video.supportsOgg();
                params[i][7] = video.supportsH264();
                params[i][8] = video.supportsWebM();
                params[i][9] = video.getAlbumId();
                params[i][10] = video.getAlbumId();
                params[i][11] = video.getRelativePath();
                params[i][12] = video.getTitle();
                params[i][13] = video.hasThumbnail();
                params[i][14] = video.supportsOgg();
                params[i][15] = video.supportsH264();
                params[i][16] = video.supportsWebM();
                i++;
            }
            queryRunner.batch(connection, SQL_CREATE_VIDEO, params);
            DbUtils.commitAndCloseQuietly(connection);
        } catch (SQLException ex) {
            DbUtils.rollbackAndCloseQuietly(connection);
            throw ex;
        }
    }

    public void save(Video video) throws SQLException {
        String id = video.getId();
        if (id == null) {
            id = StringUtils.randomUUID();
        }
        
        QueryRunner queryRunner = new QueryRunner(DatabaseUtils.getDataSource());
        // Save the video
        queryRunner.update(SQL_CREATE_VIDEO,
                id, video.getFilename(),
                video.getTitle(),
                new Timestamp(video.getDate().getTime()),
                video.getRelativePath(),
                video.hasThumbnail(),
                video.supportsOgg(),
                video.supportsH264(),
                video.supportsWebM(),
                video.getAlbumId(),
                video.getAlbumId(),
                video.getRelativePath(),
                video.getTitle(),
                video.hasThumbnail(),
                video.supportsOgg(),
                video.supportsH264(),
                video.supportsWebM());

        // Increase videos count for the album containing this video
        queryRunner.update(SQL_INCREMENT_VIDEO_COUNT, video.getAlbumId());
    }
    
    public void delete(Video video) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DatabaseUtils.getDataSource());
        queryRunner.update(SQL_DELETE_VIDEO, video.getId());
    }
    
    public void delete(List<Video> videos) throws SQLException {
        QueryRunner queryRunner = new QueryRunner();
        Connection connection = DatabaseUtils.getConnection();
        connection.setAutoCommit(false);
        try {
            for (Video video : videos) {
                queryRunner.update(connection, SQL_DELETE_VIDEO, video.getId());
            }
            DbUtils.commitAndCloseQuietly(connection);
        } catch (SQLException ex) {
            DbUtils.rollbackAndCloseQuietly(connection);
            throw ex;
        }
    }
        
    protected static RowProcessor getRowProcessor(final String token) {
        Map<String, String> map = new HashMap<>(10);
        map.put("id", "id");
        map.put("filename", "filename");
        map.put("title", "title");
        map.put("relative_path", "relativePath");
        map.put("album_id", "albumId");
        map.put("date", "date");
        map.put("owner_id", "ownerId");
        map.put("ogg", "supportsOgg");
        map.put("h264", "supportsH264");
        map.put("webm", "supportsWebM");
        
        return new BasicRowProcessor(new BeanProcessor(map)) {
            @Override
            public <T> T toBean(ResultSet rs, Class<T> type) throws SQLException {
                T result = super.toBean(rs, type);
                if (result instanceof Video) {
                    Video video = (Video) result;
                    video.computeAccessUrl(token);
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
    
    protected static BeanHandler<Video> getBeanHandler(final String token) {
        return new BeanHandler<>(Video.class, getRowProcessor(token));
    }
    
    protected static BeanListHandler<Video> getBeanListHandler(final String token) {
        return new BeanListHandler<>(Video.class, getRowProcessor(token));
    }
    
}
