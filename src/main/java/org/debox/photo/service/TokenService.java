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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.debox.photo.dao.AlbumDao;
import org.debox.photo.dao.PermissionDao;
import org.debox.photo.dao.UserDao;
import org.debox.photo.model.Album;
import org.debox.photo.model.user.AnonymousUser;
import org.debox.photo.model.user.ThirdPartyAccount;
import org.debox.photo.model.user.User;
import org.debox.photo.thirdparty.ServiceUtil;
import org.debox.photo.util.SessionUtils;
import org.debox.photo.util.StringUtils;
import org.debux.webmotion.server.render.Render;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class TokenService extends DeboxService {
    
    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);
    
    protected static AlbumDao albumDao = new AlbumDao();
    
    protected UserDao userDao = new UserDao();
    protected PermissionDao permissionDao = new PermissionDao();
    
    public Render createToken(String label) throws SQLException, UnsupportedEncodingException {
        AnonymousUser user = new AnonymousUser();
        user.setId(StringUtils.randomUUID());
        user.setLabel(URLDecoder.decode(label, "UTF-8"));
        user.setOwnerId(SessionUtils.getUserId());
        
        userDao.save(user);
        return renderJSON(user);
    }
    
    public Render getTokens() throws SQLException, IOException {
        User user = SessionUtils.getUser();
        List<ThirdPartyAccount> accounts = userDao.getThirdPartyAccounts(user);
        return renderJSON(
                "tokens", userDao.getAllAnonymousUsersByCreator(user.getId()),
                "albums", albumDao.getAlbums(user.getId(), null),
                "providers", ServiceUtil.getAuthenticationUrls(),
                "accounts", accounts);
    }
    
    public Render getToken(String id) throws SQLException {
        AnonymousUser token = userDao.getAnonymousUser(id);
        if (token == null) {
            return renderError(HttpURLConnection.HTTP_NOT_FOUND, "");
        }
        
        return renderJSON(
                "albums", albumDao.getAlbums(SessionUtils.getUserId(), null),
                "token", token);
    }
    
    public Render editToken(String id, String label, List<String> albums, List<String> ignore) throws SQLException {
        AnonymousUser token = userDao.getAnonymousUser(id);
        if (token == null) {
            return renderError(HttpURLConnection.HTTP_NOT_FOUND, "");
        }
        
        if (label != null) {
            token.setLabel(label);
        }
        List<Album> newVisibleAlbums = new ArrayList<>();
        if (albums != null || ignore != null) {
            // TODO FIX that
            List<Album> currentVisibleAlbums = albumDao.getAllAlbums(token.getId());
            List<Album> toUnauthorizeAlbums = new ArrayList<>(currentVisibleAlbums);
            for (String albumId : albums) {
                Album album = albumDao.getAlbum(albumId);
               toUnauthorizeAlbums.remove(album);
            }
            
            if (albums != null) {
                if (ignore != null) {
                    // Convert String list to Album list
                    List<Album> albumsToIgnore = new ArrayList<>(ignore.size());
                    for (String toIgnoreAlbumId : ignore) {
                        albumsToIgnore.add(albumDao.getAlbum(toIgnoreAlbumId));
                    }
                    
                    // Keep old visible subAlbums if their parent has to be ignored
                    for (Album visibleAlbum : currentVisibleAlbums) {
                        for (Album albumToIgnore : albumsToIgnore) {
                            if (visibleAlbum.isSubAlbum(albumToIgnore) && albums.contains(albumToIgnore.getId())) {
                                newVisibleAlbums.add(visibleAlbum);
                            }
                        }
                    }
                }
                
                for (String albumId : albums) {
                    Album album = albumDao.getAlbum(albumId);
                    newVisibleAlbums.add(album);
                }
            }
            
            for (Album toUnauthorizeAlbum : toUnauthorizeAlbums) {
                permissionDao.deleteReadPermission(id, toUnauthorizeAlbum);
            }
        }
        
        userDao.save(token, token.getId());
        for (Album toAutorizeAlbum : newVisibleAlbums) {
            permissionDao.saveReadPermission(id, toAutorizeAlbum);
        }
        
        return renderJSON(token);
    }
    
    public Render deleteToken(String id) throws SQLException {
        AnonymousUser token = userDao.getAnonymousUser(id);
        if (token == null) {
            return renderError(HttpURLConnection.HTTP_NOT_FOUND, "");
        }
        userDao.delete(token);
        return renderStatus(HttpURLConnection.HTTP_NO_CONTENT);
    }
    
    public Render reinitToken(String id) throws SQLException {
        AnonymousUser token = userDao.getAnonymousUser(id);
        if (token == null) {
            return renderError(HttpURLConnection.HTTP_NOT_FOUND, "");
        }
        
        AnonymousUser newToken = new AnonymousUser();
        newToken.setLabel(token.getLabel());
        newToken.setId(StringUtils.randomUUID());
        newToken.setOwnerId(SessionUtils.getUserId());
        
        userDao.save(newToken, id);
        return renderJSON(newToken);
    }
}
