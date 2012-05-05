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
import org.debox.photo.dao.AlbumDao;
import org.debox.photo.dao.TokenDao;
import org.debox.photo.model.Album;
import org.debox.photo.model.Token;
import org.debox.photo.util.StringUtils;
import org.debux.webmotion.server.render.Render;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class TokenController extends DeboxController {
    
    private static final Logger logger = LoggerFactory.getLogger(TokenController.class);
    
    protected static AlbumDao albumDao = new AlbumDao();
    protected static TokenDao tokenDao = new TokenDao();

    public Render createToken(String label) throws SQLException, UnsupportedEncodingException {
        Token token = new Token();
        token.setId(StringUtils.randomUUID());
        token.setLabel(URLDecoder.decode(label, "UTF-8"));

        tokenDao.save(token);
        return renderJSON(token);
    }
    
    public Render getTokens() throws SQLException {
        return renderJSON("tokens", tokenDao.getAll(), "albums", albumDao.getVisibleAlbums(null, null, true));
    }
    
    public Render getToken(String id) throws SQLException {
        Token token = tokenDao.getById(id);
        if (token == null) {
            return renderError(HttpURLConnection.HTTP_NOT_FOUND, "");
        }

        return renderJSON(
                "albums", albumDao.getVisibleAlbums(null, null, true),
                "token", token);
    }

    public Render editToken(String id, String label, String[] albums) throws SQLException {
        Token token = tokenDao.getById(id);
        if (token == null) {
            return renderError(HttpURLConnection.HTTP_NOT_FOUND, "");
        }

        if (label != null) {
            token.setLabel(label);
        }
        
        if (albums != null) {
            // Test that all albums that have accessible subAlbums are also accessible
            // TODO [cguy:2012-05-05] Rewrite this code
//            List<Album> allAlbums = albumDao.getAlbums();
//            for (String accessibleAlbumId : albums) {
//                for (Album album : allAlbums) {
//                    if (accessibleAlbumId.equals(album.getId())) {
//                        if (album.getParentId() != null && !Arrays.asList(albums).contains(album.getParentId())) {
//                            return renderError(HttpURLConnection.HTTP_INTERNAL_ERROR, 
//                                    "You're trying to give an access for a subAlbum without access for its parent");
//                        }
//                        break;
//                    }
//                }
//            }
            
            token.setAlbums(null);
            for (String albumId : albums) {
                Album album = albumDao.getAlbum(albumId);
                token.getAlbums().add(album);
            }
        }

        tokenDao.save(token);
        return renderJSON(token);
    }
    
    public Render deleteToken(String id) throws SQLException {
        tokenDao.delete(id);
        return renderStatus(HttpURLConnection.HTTP_OK);
    }
    
}
