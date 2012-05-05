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

import java.net.HttpURLConnection;
import java.sql.SQLException;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.debox.photo.dao.UserDao;
import org.debox.photo.model.User;
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.render.Render;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class AccountController extends WebMotionController {
    
    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);
    
    protected static UserDao userDao = new UserDao();
    
    public Render authenticate(String username, String password) {
        Subject currentUser = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        token.setRememberMe(true);

        try {
            currentUser.login(token);

        } catch (UnknownAccountException | IncorrectCredentialsException e) {
            logger.error(e.getMessage(), e);
            return renderError(HttpURLConnection.HTTP_UNAUTHORIZED, "");

        } catch (AuthenticationException e) {
            logger.error(e.getMessage(), e);
            return renderError(HttpURLConnection.HTTP_UNAUTHORIZED, "");
        }

        User user = (User) currentUser.getPrincipal();
        return renderJSON(user.getUsername());
    }
    
    public Render getLoggedUser() {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        return renderJSON(user);
    }

    public Render editCredentials(String userId, String username, String oldPassword, String password, String confirm) {
        try {
            User user = (User) SecurityUtils.getSubject().getPrincipal();
            if (!user.getId().equals(userId)) {
                return renderError(HttpURLConnection.HTTP_INTERNAL_ERROR, null);
            }

            boolean oldCredentialsChecked = userDao.checkCredentials(user.getUsername(), oldPassword);
            if (!oldCredentialsChecked) {
                return renderError(HttpURLConnection.HTTP_INTERNAL_ERROR, null);
            }

            user.setUsername(username);
            user.setPassword(password);

            userDao.save(user);

            return renderJSON("username", username);

        } catch (SQLException ex) {
            logger.error(ex.getMessage(), ex);
            return renderError(HttpURLConnection.HTTP_INTERNAL_ERROR, null);
        }
    }

    public Render logout() {
        try {
            SecurityUtils.getSubject().logout();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return renderStatus(HttpURLConnection.HTTP_OK);
    }

}
