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
package org.debox.photo.dao.mysql;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import org.apache.shiro.authc.*;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.util.JdbcUtils;
import org.apache.shiro.util.SimpleByteSource;
import org.debox.photo.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class JdbcMysqlRealm extends JdbcRealm {

    private static final Logger logger = LoggerFactory.getLogger(JdbcMysqlRealm.class);
    
    protected static final RandomNumberGenerator GENERATOR = new SecureRandomNumberGenerator();
    protected static final String DEFAULT_SALTED_AUTHENTICATION_QUERY = "select password, password_salt, id from users where username = ?";
    protected static final String PROPERTY_DATABASE_HOST = "database.host";
    protected static final String PROPERTY_DATABASE_PORT = "database.port";
    protected static final String PROPERTY_DATABASE_NAME = "database.name";
    protected static final String PROPERTY_DATABASE_USER = "database.user";
    protected static final String PROPERTY_DATABASE_PASSWORD = "database.password";
    protected static final String PROPERTY_DATABASE_OPTIONS = "database.options";
    
    protected static ComboPooledDataSource comboPooledDataSource;

    public JdbcMysqlRealm() {
        this.setDataSource(getDataSource());
        this.setAuthenticationQuery(DEFAULT_SALTED_AUTHENTICATION_QUERY);
        this.setSaltStyle(SaltStyle.COLUMN);
    }

    public static synchronized ComboPooledDataSource getDataSource() {
        if (comboPooledDataSource == null) {
            try {
                Properties properties = new Properties();
                properties.load(JdbcMysqlRealm.class.getResourceAsStream("application.properties"));
                
                comboPooledDataSource = new ComboPooledDataSource();
                comboPooledDataSource.setDriverClass("com.mysql.jdbc.Driver");
                
                String host = properties.getProperty(PROPERTY_DATABASE_HOST);
                String port = properties.getProperty(PROPERTY_DATABASE_PORT);
                String name = properties.getProperty(PROPERTY_DATABASE_NAME);
                String user = properties.getProperty(PROPERTY_DATABASE_USER);
                String password = properties.getProperty(PROPERTY_DATABASE_PASSWORD);
                String options = properties.getProperty(PROPERTY_DATABASE_OPTIONS);
                
                String url = "jdbc:mysql://" + host + ':' + port + '/' + name + options;
                comboPooledDataSource.setJdbcUrl(url);
                comboPooledDataSource.setUser(user);
                comboPooledDataSource.setPassword(password);
                
            } catch (IOException | PropertyVetoException ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
        return comboPooledDataSource;
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

            if (password == null) {
                throw new UnknownAccountException("No account found for user [" + username + "]");
            }

            User user = new User();
            user.setId(id);
            user.setUsername(username);
            
            info = new SimpleAuthenticationInfo(user, password.toCharArray(), getName());
            
            if (salt != null) {
                info.setCredentialsSalt(new SimpleByteSource(salt));
            }

        } catch (SQLException e) {
            final String message = "There was a SQL error while authenticating user [" + username + "]";
            logger.error(message, e);

            // Rethrow any SQL errors as an authentication exception
            throw new AuthenticationException(message, e);
        } finally {
            JdbcUtils.closeConnection(conn);
        }

        return info;
    }
    
    private String[] getPasswordForUser(Connection conn, String username) throws SQLException {
        String[] result = new String[3];
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

                result[0] = rs.getString(1);
                result[1] = rs.getString(2);
                result[2] = rs.getString(3);

                foundResult = true;
            }
        } finally {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(ps);
        }

        return result;
    }
    
}
