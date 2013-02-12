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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.RowProcessor;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.debox.photo.model.Album;
import org.debox.photo.model.comment.Comment;
import org.debox.photo.model.Media;
import org.debox.photo.model.Photo;
import org.debox.photo.model.Video;
import org.debox.photo.util.DatabaseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class CommentDao {

    private static final Logger logger = LoggerFactory.getLogger(CommentDao.class);
    
    protected static String CREATE = "INSERT INTO comments (id, author_id, publish_time, content) VALUES (?, ?, ?, ?)";
    protected static String UPDATE = "UPDATE comments SET content = ?, last_modification = NOW() WHERE id = ?";
    protected static String CREATE_ALBUM_LINK = "INSERT INTO albums_comments (comment_id, album_id) VALUES (?, ?)";
    protected static String CREATE_PHOTO_LINK = "INSERT INTO photos_comments (comment_id, photo_id) VALUES (?, ?)";
    protected static String CREATE_VIDEO_LINK = "INSERT INTO videos_comments (comment_id, video_id) VALUES (?, ?)";
    protected static String GET_BY_ALBUM = "SELECT * FROM comments c INNER JOIN albums_comments ac ON c.id = ac.comment_id WHERE ac.album_id = ? ORDER BY c.publish_time";
    protected static String GET_BY_PHOTO = "SELECT * FROM comments c INNER JOIN photos_comments pc ON c.id = pc.comment_id WHERE pc.photo_id = ? ORDER BY c.publish_time";
    protected static String GET_BY_VIDEO = "SELECT * FROM comments c INNER JOIN videos_comments vc ON c.id = vc.comment_id WHERE vc.video_id = ? ORDER BY c.publish_time";
    protected static String COUNT_BY_MEDIA_OWNER_ID = "SELECT count(*) FROM ("
            + "(SELECT c.* FROM comments c INNER JOIN videos_comments vc ON c.id = vc.comment_id INNER JOIN videos v ON v.id = vc.video_id INNER JOIN albums a ON v.album_id = a.id WHERE a.owner_id = ?) "
            + "UNION "
            + "(SELECT c.* FROM comments c INNER JOIN photos_comments pc ON c.id = pc.comment_id INNER JOIN photos p ON p.id = pc.photo_id INNER JOIN albums a ON p.album_id = a.id WHERE a.owner_id = ?) "
            + "UNION "
            + "(SELECT c.* FROM comments c INNER JOIN albums_comments ac ON c.id = ac.comment_id INNER JOIN albums a ON a.id = ac.album_id WHERE a.owner_id = ?) "
            + ") all_comments";
    protected static String VIDEO_COMMENTS_BY_MEDIA_OWNER_ID = ""
            + "SELECT c.*, v.* "
            + "FROM comments c "
            + "INNER JOIN videos_comments vc ON c.id = vc.comment_id "
            + "INNER JOIN videos v ON v.id = vc.video_id "
            + "INNER JOIN albums a ON v.album_id = a.id WHERE a.owner_id = ?";
    protected static String PHOTO_COMMENTS_BY_MEDIA_OWNER_ID = ""
            + "SELECT c.*, p.* "
            + "FROM comments c "
            + "INNER JOIN photos_comments pc ON c.id = pc.comment_id "
            + "INNER JOIN photos p ON p.id = pc.photo_id "
            + "INNER JOIN albums a ON p.album_id = a.id WHERE a.owner_id = ?";
    protected static String ALBUM_COMMENTS_BY_MEDIA_OWNER_ID = ""
            + "SELECT c.*, a.* FROM comments c INNER JOIN albums_comments ac ON c.id = ac.comment_id INNER JOIN albums a ON a.id = ac.album_id WHERE a.owner_id = ?";
    
    protected static String GET_BY_ID = "SELECT * FROM comments WHERE id = ?";
    protected static String DELETE = "DELETE FROM comments WHERE id = ?";
    
    protected UserDao userDao = new UserDao();
    protected AlbumDao albumDao = new AlbumDao();
    protected BeanListHandler<Comment> beanListHandler = new BeanListHandler<>(Comment.class, getRowProcessor(null));

    public void save(Album album, Comment comment) throws SQLException {
        save(album.getId(), comment, CREATE_ALBUM_LINK);
    }

    public void save(Comment comment) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DatabaseUtils.getDataSource());
        queryRunner.update(UPDATE, comment.getContent(), comment.getId());
    }

    public void save(Media media, Comment comment) throws SQLException {
        if (media instanceof Video) {
            save(media.getId(), comment, CREATE_VIDEO_LINK);
        } else {
            save(media.getId(), comment, CREATE_PHOTO_LINK);
        }
    }

    public Comment getById(String id) throws SQLException {
        return get(id, GET_BY_ID).get(0);
    }

    protected void save(String mediaId, Comment comment, String joinQuery) throws SQLException {
        Connection connection = DatabaseUtils.getConnection();
        connection.setAutoCommit(false);
        try {
            Timestamp time = new Timestamp(comment.getDate().getTime());
    
            QueryRunner queryRunner = new QueryRunner();
            queryRunner.update(connection, CREATE, comment.getId(), comment.getUser().getId(), time, comment.getContent());
            queryRunner.update(connection, joinQuery, comment.getId(), mediaId);

            DbUtils.commitAndCloseQuietly(connection);
        } catch (SQLException ex) {
            DbUtils.rollbackAndCloseQuietly(connection);
            throw ex;
        }
    }

    public List<Comment> getByAlbum(String albumId) throws SQLException {
        return get(albumId, GET_BY_ALBUM);
    }

    public List<Comment> getByMedia(Media media) throws SQLException {
        if (media instanceof Video) {
            return get(media.getId(), GET_BY_VIDEO);
        }
        return get(media.getId(), GET_BY_PHOTO);
    }
    
    public Long countByMediaOwner(String mediaOwnerId) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DatabaseUtils.getDataSource());
        Long result = queryRunner.query(COUNT_BY_MEDIA_OWNER_ID, new ResultSetHandler<Long>() {
            @Override
            public Long handle(ResultSet rs) throws SQLException {
                if (rs.next()) {
                    return rs.getLong(1);
                }
                return 0L;
            }
        }, mediaOwnerId, mediaOwnerId, mediaOwnerId);
        return result;
    }
    
    public List<Comment> getVideoCommentsByMediaOwner(String mediaOwnerId) throws SQLException {
        return getCommentsByMediaOwner(mediaOwnerId, VIDEO_COMMENTS_BY_MEDIA_OWNER_ID, Video.class, VideoDao.getRowProcessor(null));
    }
    
    public List<Comment> getPhotoCommentsByMediaOwner(String mediaOwnerId) throws SQLException {
        return getCommentsByMediaOwner(mediaOwnerId, PHOTO_COMMENTS_BY_MEDIA_OWNER_ID, Photo.class, PhotoDao.getRowProcessor(null));
    }
    
    public List<Comment> getAlbumCommentsByMediaOwner(String mediaOwnerId) throws SQLException {
        return getCommentsByMediaOwner(mediaOwnerId, ALBUM_COMMENTS_BY_MEDIA_OWNER_ID, Album.class, albumDao.getRowProcessor(mediaOwnerId, false));
    }
    
    public <M> List<Comment> getCommentsByMediaOwner(String mediaOwnerId, String sql, final Class<M> clazz, final RowProcessor mediaRowProcessor) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DatabaseUtils.getDataSource());
        List<Comment> result = queryRunner.query(sql, new ResultSetHandler<List<Comment>>() {
            @Override
            public List<Comment> handle(ResultSet rs) throws SQLException {
                List<Comment> result = new ArrayList<>();
                RowProcessor commentRowProcessor = getRowProcessor(clazz);
                while (rs.next()) {
                    Comment<M> comment = commentRowProcessor.toBean(rs, Comment.class);
                    comment.setMedia(mediaRowProcessor.toBean(rs, clazz));
                    result.add(comment);
                }
                return result;
            }
        }, mediaOwnerId);
        return result;
    }
    
    protected List<Comment> get(String identifier, String query) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DatabaseUtils.getDataSource());
        List<Comment> result = queryRunner.query(query, beanListHandler, identifier);
        return result;
    }

    public void delete(String commentId) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DatabaseUtils.getDataSource());
        queryRunner.update(DELETE, commentId);
    }

    protected <M> RowProcessor getRowProcessor(Class<M> mediaClass) {
        return new BasicRowProcessor() {
            @Override
            public <T> T toBean(ResultSet rs, Class<T> type) throws SQLException {
                Comment<M> comment = new Comment();
                comment.setId(rs.getString("id"));
                comment.setDate(new Date(rs.getTimestamp("publish_time").getTime()));
                comment.setContent(rs.getString("content"));
                comment.setUser(userDao.getUser(rs.getString("author_id")));
                return (T) comment;
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

}
