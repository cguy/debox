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
package org.debox.photo.action;

import java.sql.SQLException;
import java.util.Date;
import org.apache.shiro.SecurityUtils;
import org.debox.photo.dao.AlbumDao;
import org.debox.photo.dao.CommentDao;
import org.debox.photo.model.Comment;
import org.debox.photo.model.ThirdPartyAccount;
import org.debox.photo.model.User;
import org.debox.photo.util.SessionUtils;
import org.debox.photo.util.StringUtils;
import org.debux.webmotion.server.render.Render;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class CommentController extends DeboxController {
    
    protected static CommentDao commentDao = new CommentDao();
    
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
        Comment comment = create(content);
        commentDao.saveAlbumComment(albumId, comment);
        return renderJSON(comment);
    }
    
    public Render createPhotoComment(String photoId, String content) throws SQLException {
        Comment comment = create(content);
        commentDao.savePhotoComment(photoId, comment);
        return renderJSON(comment);
    }
    
}
