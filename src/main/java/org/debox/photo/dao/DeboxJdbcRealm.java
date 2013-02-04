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

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.JdbcUtils;
import org.apache.shiro.util.SimpleByteSource;
import org.debox.photo.model.user.DeboxUser;
import org.debox.photo.model.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class DeboxJdbcRealm extends JdbcRealm {

    private static final Logger logger = LoggerFactory.getLogger(DeboxJdbcRealm.class);
    protected static final String SALTED_AUTHENTICATION_QUERY = "select password, password_salt, a.id id, firstname, lastname, avatar from accounts a INNER JOIN users u on u.id = a.id where username = ?";
    protected static final String USER_ROLES_QUERY = "select r.name as role_name from users_roles ur LEFT JOIN roles r ON ur.role_id = r.id where ur.user_id = ?";
    protected UserDao userDao = new UserDao();

    public DeboxJdbcRealm() {
        this.setAuthenticationQuery(SALTED_AUTHENTICATION_QUERY);
        this.setUserRolesQuery(USER_ROLES_QUERY);
        this.setSaltStyle(SaltStyle.COLUMN);
    }
    
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        String username = upToken.getUsername();

        // Null username is invalid
        if (username == null) {
            throw new AccountException("Null usernames are not allowed by this realm.");
        }

        Connection conn = null;
        SimpleAuthenticationInfo info = null;
        try {
            conn = dataSource.getConnection();

            String[] queryResults = getPasswordForUser(conn, username);
            String password = queryResults[0];
            String salt = queryResults[1];
            String id = queryResults[2];
            String firstname = queryResults[3];
            String lastname = queryResults[4];
            String avatar = queryResults[5];

            if (password == null) {
                throw new UnknownAccountException("No account found for user [" + username + "]");
            }

            DeboxUser user = new DeboxUser();
            user.setId(id);
            user.setUsername(username);
            user.setThirdPartyAccounts(userDao.getThirdPartyAccounts(user));
            user.setFirstName(firstname);
            user.setLastName(lastname);
            user.setAvatar(avatar);

            info = new SimpleAuthenticationInfo(user, password.toCharArray(), getName());

            if (salt != null) {
                info.setCredentialsSalt(new SimpleByteSource(salt));
            }

        } catch (IOException | SQLException e) {
            final String message = "There was a SQL error while authenticating user [" + username + "]";
            logger.error(message, e);

            // Rethrow any SQL errors as an authentication exception
            throw new AuthenticationException(message, e);
        } finally {
            JdbcUtils.closeConnection(conn);
        }

        return info;
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

    private String[] getPasswordForUser(Connection conn, String username) throws SQLException {
        String[] result = new String[6];
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(authenticationQuery);
            ps.setString(1, username);

            // Execute query
            rs = ps.executeQuery();

            // Loop over results - although we are only expecting one result, since usernames should be unique
            boolean foundResult = false;
            while (rs.next()) {

                // Check to ensure only one row is processed
                if (foundResult) {
                    throw new AuthenticationException("More than one user row found for user [" + username + "]. Usernames must be unique.");
                }

                for (int i = 0 ; i < result.length ; i++) {
                    result[i] = rs.getString(i+1);
                }
                foundResult = true;
            }
        } finally {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(ps);
        }

        return result;
    }
}
