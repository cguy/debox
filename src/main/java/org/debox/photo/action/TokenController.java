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

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.List;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.debox.photo.dao.AlbumDao;
import org.debox.photo.dao.TokenDao;
import org.debox.photo.model.Album;
import org.debox.photo.model.Token;
import org.debox.photo.util.StringUtils;
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.render.Render;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class TokenController extends WebMotionController {
    
    private static final Logger logger = LoggerFactory.getLogger(TokenController.class);
    
    protected static AlbumDao albumDao = new AlbumDao();
    protected static TokenDao tokenDao = new TokenDao();

    public Render createToken(String label) throws SQLException, UnsupportedEncodingException {
        Subject currentUser = SecurityUtils.getSubject();
        if (!currentUser.isAuthenticated()) {
            return renderError(HttpURLConnection.HTTP_FORBIDDEN, "User must be logged in.");
        }

        Token token = new Token();
        token.setId(StringUtils.randomUUID());
        token.setLabel(URLDecoder.decode(label, "UTF-8"));

        tokenDao.save(token);
        return renderJSON(token);
    }
    
    public Render getToken(String id) throws SQLException {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            return renderError(HttpURLConnection.HTTP_FORBIDDEN, "");
        }

        Token token = tokenDao.getById(id);
        if (token == null) {
            return renderError(HttpURLConnection.HTTP_NOT_FOUND, "");
        }

        return renderJSON(
                "albums", albumDao.getAlbums(),
                "token", token);
    }

    public Render editToken(String id, String label, List<String> albums) throws SQLException {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            return renderError(HttpURLConnection.HTTP_FORBIDDEN, "");
        }

        Token token = tokenDao.getById(id);
        if (token == null) {
            return renderError(HttpURLConnection.HTTP_NOT_FOUND, "");
        }

        token.setLabel(label);
        token.setAlbums(null);
        
        if (albums != null) {
            for (String albumId : albums) {
                Album album = albumDao.getAlbum(albumId);
                token.getAlbums().add(album);
            }
        }

        tokenDao.save(token);
        return renderJSON(token);
    }
    
    public Render deleteToken(String id) throws SQLException {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            return renderError(HttpURLConnection.HTTP_FORBIDDEN, "");
        }
        tokenDao.delete(id);
        return renderStatus(HttpURLConnection.HTTP_OK);
    }
    
}
