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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.shiro.util.JdbcUtils;
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

    protected UserDao userDao = new UserDao();
    
    public void saveAlbumComment(String albumId, Comment comment) throws SQLException {
        save(albumId, comment, CREATE_ALBUM_LINK);
    }

    public void savePhotoComment(String photoId, Comment comment) throws SQLException {
        save(photoId, comment, CREATE_PHOTO_LINK);
    }
    
    protected void save(String mediaId, Comment comment, String joinQuery) throws SQLException {
        try (
                Connection c = DatabaseUtils.getConnection();
                PreparedStatement stmt = c.prepareStatement(CREATE);
                PreparedStatement joinStmt = c.prepareStatement(joinQuery)) {
            
            stmt.setString(1, comment.getId());
            stmt.setString(2, comment.getUser().getId());
            stmt.setTimestamp(3, new Timestamp(comment.getDate().getTime()));
            stmt.setString(4, comment.getContent());
            stmt.executeUpdate();
            
            joinStmt.setString(1, comment.getId());
            joinStmt.setString(2, mediaId);
            joinStmt.executeUpdate();
        }
    }
    
    public List<Comment> getByAlbum(String albumId) throws SQLException {
        return getByMedia(albumId, GET_BY_ALBUM);
    }
    
    public List<Comment> getByPhoto(String photoId) throws SQLException {
        return getByMedia(photoId, GET_BY_PHOTO);
    }
    
    protected List<Comment> getByMedia(String mediaId, String query) throws SQLException {
        ResultSet rs = null;
        List<Comment> result = new ArrayList<>();
        try (
                Connection c = DatabaseUtils.getConnection();
                PreparedStatement stmt = c.prepareStatement(query)) {
            
            stmt.setString(1, mediaId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Comment comment = new Comment();
                comment.setId(rs.getString("id"));
                comment.setDate(new Date(rs.getTimestamp("publish_time").getTime()));
                comment.setContent(rs.getString("content"));
                comment.setUser(userDao.getUser(rs.getString("author_id")));
                
                result.add(comment);
            }
        } finally {
            JdbcUtils.closeResultSet(rs);
        }
        return result;
    }
    
}
