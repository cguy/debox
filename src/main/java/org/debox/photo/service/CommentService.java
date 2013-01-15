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
package org.debox.photo.service;

import java.net.HttpURLConnection;
import java.sql.SQLException;
import java.util.Date;
import org.apache.shiro.SecurityUtils;
import org.debox.photo.dao.AlbumDao;
import org.debox.photo.dao.CommentDao;
import org.debox.photo.model.Album;
import org.debox.photo.model.Comment;
import org.debox.photo.model.Media;
import org.debox.photo.model.user.User;
import org.debox.photo.util.SessionUtils;
import org.debox.photo.util.StringUtils;
import org.debux.webmotion.server.render.Render;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class CommentService extends DeboxService {
    
    protected static CommentDao commentDao = new CommentDao();
    protected static AlbumDao albumDao = new AlbumDao();
    protected static MediaService mediaService = new MediaService();
    
    protected Comment create(String content) {
        User user = SessionUtils.getUser(SecurityUtils.getSubject());
        Comment comment = new Comment();
        comment.setUser(user);
        comment.setContent(content);
        comment.setId(StringUtils.randomUUID());
        comment.setDate(new Date());
        
        return comment;
    }
    
    public Render createAlbumComment(String albumId, String content) throws SQLException {
        Album album = albumDao.getAlbum(albumId);
        if (album == null) {
            return renderNotFound();
        }
        Comment comment = create(content);
        commentDao.save(album, comment);
        return renderJSON(comment);
    }
    
    public Render createMediaComment(String mediaId, String content) throws SQLException {
        Media media = mediaService.getMediaById(mediaId, null);
        if (media == null) {
            return renderNotFound();
        }
        Comment comment = create(content);
        commentDao.save(media, comment);
        return renderJSON(comment);
    }
    
    public Render getMediaComments(String mediaId) throws SQLException {
        Media media = mediaService.getMediaById(mediaId, null);
        if (media == null) {
            return renderNotFound();
        }
        return renderJSON("mediaId", mediaId, "comments", commentDao.getByMedia(media));
    }
    
    public Render deleteComment(String commentId) throws SQLException {
        Comment comment = commentDao.getById(commentId);
        String userId = SessionUtils.getUserId();
        String ownerId = comment.getUser().getId();
        if (!userId.equals(ownerId) && !SessionUtils.isAdministrator()) {
            return renderError(HttpURLConnection.HTTP_FORBIDDEN, "You are not autorized to delete this comment.");
        }
        
        commentDao.delete(commentId);
        return renderStatus(HttpURLConnection.HTTP_NO_CONTENT);
    }
    
}
