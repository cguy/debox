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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.JdbcUtils;
import org.debox.photo.dao.mysql.JdbcMysqlRealm;
import org.debox.photo.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class UserDao extends JdbcMysqlRealm {

    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);
    
    protected static String SQL_GET_USERS_COUNT = "SELECT count(id) FROM users";
    protected static String SQL_CREATE_USER = "INSERT INTO users VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE username = ?, password = ?, password_salt = ?";
    
    public int getUsersCount() throws SQLException {
        int result = -1;
        
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(SQL_GET_USERS_COUNT);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getInt(1);
            }

        } finally {
            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
        }

        return result;
    }

    public void save(User user) throws SQLException {
        ByteSource salt = GENERATOR.nextBytes();
        String hashedPassword = new Sha256Hash(user.getPassword(), salt.toBase64(), 1024).toBase64();

        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(SQL_CREATE_USER);
            statement.setString(1, user.getId());
            statement.setString(2, user.getUsername());
            statement.setString(3, hashedPassword);
            statement.setString(4, salt.toBase64());
            statement.setString(5, user.getUsername());
            statement.setString(6, hashedPassword);
            statement.setString(7, salt.toBase64());
            statement.executeUpdate();

        } finally {
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
        }
    }
}
