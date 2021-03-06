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
package org.debox.photo.dao.thirdparty;

import com.restfb.DefaultFacebookClient;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.JdbcUtils;
import org.debox.photo.dao.DeboxJdbcRealm;
import org.debox.photo.dao.UserDao;
import org.debox.photo.model.Role;
import org.debox.photo.model.user.ThirdPartyAccount;
import org.debox.photo.model.user.User;
import org.debox.photo.thirdparty.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FacebookRealm extends DeboxJdbcRealm {

    private static final Logger logger = LoggerFactory.getLogger(FacebookRealm.class);
    
    protected UserDao userDao = new UserDao();

    @Override
    public boolean supports(AuthenticationToken token) {
        if (token instanceof ThirdPartyTokenWrapper) {
            return true;
        }
        return false;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        if (principals == null) {
            throw new AuthorizationException("PrincipalCollection method argument cannot be null.");
        }

        try {
            User user = (User) getAvailablePrincipal(principals);
            Connection conn = null;
            Set<String> roleNames = null;
            Set<String> permissions = null;
            try {
                conn = dataSource.getConnection();

                // Retrieve roles and permissions from database
                roleNames = getRoleNamesForUser(conn, user.getId());
                if (permissionsLookupEnabled) {
                    permissions = getPermissions(conn, user.getId(), roleNames);
                }

            } catch (SQLException e) {
                final String message = "There was a SQL error while authorizing user [" + user.getId() + "]";
                if (logger.isErrorEnabled()) {
                    logger.error(message, e);
                }

                // Rethrow any SQL errors as an authorization exception
                throw new AuthorizationException(message, e);
            } finally {
                JdbcUtils.closeConnection(conn);
            }

            SimpleAuthorizationInfo info = new SimpleAuthorizationInfo(roleNames);
            info.setStringPermissions(permissions);
            return info;
        } catch (Exception ex) {
            logger.error("Unable to get authorization info");
        }
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        try {
            ThirdPartyTokenWrapper facebookToken = (ThirdPartyTokenWrapper) token;

            org.scribe.model.Token accessToken = ServiceUtil.getFacebookService().getAccessToken(null, facebookToken.getCode());
            DefaultFacebookClient client = new DefaultFacebookClient(accessToken.getToken());
            com.restfb.types.User fbUser = client.fetchObject("me", com.restfb.types.User.class);

            ThirdPartyAccount account = userDao.getUser("facebook", fbUser.getId());
            Role role = null;
            if (account == null) {
                account = new ThirdPartyAccount(ServiceUtil.getProvider("facebook"), fbUser.getId(), accessToken.getToken());
                role = userDao.getRole("user"); // Only assign role for new user, don't want to update existing role
            } else {
                account.setToken(accessToken.getToken());
            }

            account.setUsername(fbUser.getName());
            account.setAccountUrl(fbUser.getLink());
            account.setFirstName(fbUser.getFirstName());
            account.setLastName(fbUser.getLastName());

            userDao.save(account, role);
            return new SimpleAuthenticationInfo(account, facebookToken.getCode(), this.getName());

        } catch (SQLException ex) {
            logger.error("Unable to access database, reason:", ex);
            return null;
        } catch (Exception ex) {
            logger.error("Unable to authenticate user, reason:", ex);
            return null;
        }
    }
    
    protected UserDao getUserDao() {
        if (userDao == null) {
            userDao = new UserDao();
        }
        return userDao;
    }
}
