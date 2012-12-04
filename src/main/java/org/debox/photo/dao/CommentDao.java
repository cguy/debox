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
import org.apache.commons.dbutils.RowProcessor;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.debox.photo.model.Comment;
import org.debox.photo.util.DatabaseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class CommentDao {

    private static final Logger logger = LoggerFactory.getLogger(CommentDao.class);
    
    protected static String CREATE = "INSERT INTO comments (id, author_id, publish_time, content) VALUES (?, ?, ?, ?)";
    protected static String CREATE_ALBUM_LINK = "INSERT INTO albums_comments (comment_id, album_id) VALUES (?, ?)";
    protected static String CREATE_PHOTO_LINK = "INSERT INTO photos_comments (comment_id, photo_id) VALUES (?, ?)";
    protected static String GET_BY_ALBUM = "SELECT * FROM comments c INNER JOIN albums_comments ac ON c.id = ac.comment_id WHERE ac.album_id = ? ORDER BY c.publish_time";
    protected static String GET_BY_PHOTO = "SELECT * FROM comments c INNER JOIN photos_comments pc ON c.id = pc.comment_id WHERE pc.photo_id = ? ORDER BY c.publish_time";
    protected static String GET_BY_ID = "SELECT * FROM comments WHERE id = ?";
    protected static String DELETE = "DELETE FROM comments WHERE id = ?";
    
    protected UserDao userDao = new UserDao();
    protected BeanListHandler<Comment> beanListHandler = new BeanListHandler<>(Comment.class, getRowProcessor());

    public void saveAlbumComment(String albumId, Comment comment) throws SQLException {
        save(albumId, comment, CREATE_ALBUM_LINK);
    }

    public void savePhotoComment(String photoId, Comment comment) throws SQLException {
        save(photoId, comment, CREATE_PHOTO_LINK);
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

    public List<Comment> getByPhoto(String photoId) throws SQLException {
        return get(photoId, GET_BY_PHOTO);
    }

    protected List<Comment> get(String mediaId, String query) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DatabaseUtils.getDataSource());
        List<Comment> result = queryRunner.query(query, beanListHandler, mediaId);
        return result;
    }

    public void delete(String commentId) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DatabaseUtils.getDataSource());
        queryRunner.update(DELETE, commentId);
    }

    protected RowProcessor getRowProcessor() {
        return new BasicRowProcessor() {
            @Override
            public <T> T toBean(ResultSet rs, Class<T> type) throws SQLException {
                Comment comment = new Comment();
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
