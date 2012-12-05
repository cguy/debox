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
import java.util.Map.Entry;
import org.apache.shiro.util.JdbcUtils;
import org.debox.photo.model.Configuration;
import org.debox.photo.util.DatabaseUtils;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class ConfigurationDao {

    protected static final String SQL_GET_CONFIGURATION = "SELECT `key`, `value` FROM configurations";
    protected static final String SQL_SET_CONFIGURATION = "INSERT INTO configurations VALUES (?, ?) ON DUPLICATE KEY UPDATE value = ?";
    protected static final String SQL_GET_USER_CONFIGURATION = "SELECT `key`, `value` FROM users_configurations WHERE user_id = ?";
    protected static final String SQL_SET_USER_CONFIGURATION = "INSERT INTO users_configurations VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE value = ?";
    
    public Configuration getOverallConfiguration() throws SQLException {
        Configuration configuration = new Configuration();
        
        Connection connection = DatabaseUtils.getConnection();
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL_GET_CONFIGURATION);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                configuration.set(Configuration.Key.getById(resultSet.getString(1)),resultSet.getString(2));
            }
        } finally {
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
        }
        
        return configuration;
    }
    
    public void save(Configuration applicationConfiguration) throws SQLException {
        Connection connection = DatabaseUtils.getConnection();
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL_SET_CONFIGURATION);
            for (Entry<String, String> configuration : applicationConfiguration.get().entrySet()) {
                statement.setString(1, configuration.getKey());
                statement.setString(2, configuration.getValue());
                statement.setString(3, configuration.getValue());
                statement.addBatch();
            }
            statement.executeBatch();
        } finally {
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
        }
    }
    
    public void saveUserConfiguration(String userId, Configuration applicationConfiguration) throws SQLException {
        Connection connection = DatabaseUtils.getConnection();
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement("DELETE FROM users_configurations WHERE user_id = ?");
            statement.setString(1, userId);
            statement.executeUpdate();
            
            statement = connection.prepareStatement(SQL_SET_USER_CONFIGURATION);
            for (Entry<String, String> configuration : applicationConfiguration.get().entrySet()) {
                statement.setString(1, userId);
                statement.setString(2, configuration.getKey());
                statement.setString(3, configuration.getValue());
                statement.setString(4, configuration.getValue());
                statement.addBatch();
            }
            statement.executeBatch();
        } finally {
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
        }
    }

    public Configuration getUserConfiguration(String userId) throws SQLException {
        Configuration configuration = new Configuration();
        
        Connection connection = DatabaseUtils.getConnection();
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL_GET_USER_CONFIGURATION);
            statement.setString(1, userId);
            
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                configuration.set(Configuration.Key.getById(resultSet.getString(1)),resultSet.getString(2));
            }
        } finally {
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
        }
        
        return configuration;
    }

}
