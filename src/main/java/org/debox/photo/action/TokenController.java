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
import java.util.ArrayList;
import java.util.List;
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
    
    public Render editToken(String id, String label, List<String> albums, List<String> ignore) throws SQLException {
        Token token = tokenDao.getById(id);
        if (token == null) {
            return renderError(HttpURLConnection.HTTP_NOT_FOUND, "");
        }
        
        if (label != null) {
            token.setLabel(label);
        }
        if (albums != null || ignore != null) {
            List<Album> currentVisibleAlbums = new ArrayList<>(token.getAlbums());
            token.getAlbums().clear();
            if (albums != null) {
                if (ignore != null) {
                    // Convert String list to Album list
                    List<Album> albumsToIgnore = new ArrayList<>(ignore.size());
                    for (String toIgnoreAlbumId : ignore) {
                        albumsToIgnore.add(albumDao.getAlbum(toIgnoreAlbumId, null, false));
                    }
                    
                    // Keep old visible subAlbums if their parent has to be ignored
                    for (Album visibleAlbum : currentVisibleAlbums) {
                        for (Album albumToIgnore : albumsToIgnore) {
                            if (visibleAlbum.isSubAlbum(albumToIgnore) && albums.contains(albumToIgnore.getId())) {
                                token.getAlbums().add(visibleAlbum);
                            }
                        }
                    }
                }
                
                for (String albumId : albums) {
                    Album album = albumDao.getAlbum(albumId, null, false);
                    token.getAlbums().add(album);
                }
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
