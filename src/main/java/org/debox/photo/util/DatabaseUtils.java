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
package org.debox.photo.util;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.apache.commons.configuration.Configuration;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.util.JdbcUtils;
import org.debox.photo.dao.JdbcMysqlRealm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class DatabaseUtils {

    private static final Logger logger = LoggerFactory.getLogger(JdbcMysqlRealm.class);
    public static final String PROPERTY_DATABASE_HOST = "database.host";
    public static final String PROPERTY_DATABASE_PORT = "database.port";
    public static final String PROPERTY_DATABASE_NAME = "database.name";
    public static final String PROPERTY_DATABASE_USERNAME = "database.username";
    public static final String PROPERTY_DATABASE_PASSWORD = "database.password";
    public static final String TEST_QUERY = "SELECT 1";
    protected static ComboPooledDataSource comboPooledDataSource;
    protected static Configuration properties = null;

    public static Configuration getConfiguration() {
        return properties;
    }

    public static synchronized void setDataSourceConfiguration(Configuration properties) {
        DatabaseUtils.properties = properties;
    }

    public static boolean hasConfiguration() {
        return properties != null && !StringUtils.atLeastOneIsEmpty(
                properties.getString(PROPERTY_DATABASE_HOST),
                properties.getString(PROPERTY_DATABASE_PORT),
                properties.getString(PROPERTY_DATABASE_NAME),
                properties.getString(PROPERTY_DATABASE_USERNAME));
    }

    public static boolean testConnection() {
        boolean result = true;
        try (
            Connection connection = DatabaseUtils.getDataSource().getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT 1");
        ) {
            statement.executeQuery();
        } catch (SQLException ex) {
            logger.error("Unable to connect to the database", ex);
            result = false;
        }
        return result;
    }

    public static synchronized ComboPooledDataSource getDataSource() {
        if (comboPooledDataSource == null && hasConfiguration()) {
            Connection connection = null;
            try {
                comboPooledDataSource = new ComboPooledDataSource();
                comboPooledDataSource.setDriverClass("com.mysql.jdbc.Driver");

                String host = properties.getString(PROPERTY_DATABASE_HOST);
                String port = properties.getString(PROPERTY_DATABASE_PORT);
                String name = properties.getString(PROPERTY_DATABASE_NAME);
                String user = properties.getString(PROPERTY_DATABASE_USERNAME);
                String password = properties.getString(PROPERTY_DATABASE_PASSWORD);

                String url = "jdbc:mysql://" + host + ':' + port + '/' + name;
                comboPooledDataSource.setJdbcUrl(url);
                comboPooledDataSource.setUser(user);
                comboPooledDataSource.setPassword(password);
                comboPooledDataSource.setIdleConnectionTestPeriod(300);
                comboPooledDataSource.setPreferredTestQuery(TEST_QUERY);

                connection = comboPooledDataSource.getConnection();

            } catch (SQLException | PropertyVetoException ex) {
                logger.error(ex.getMessage(), ex);
            } finally {
                JdbcUtils.closeConnection(connection);
            }
        }
        return comboPooledDataSource;
    }
    
    public static void applyDatasourceToShiro() {
        RealmSecurityManager securityManager = (RealmSecurityManager) SecurityUtils.getSecurityManager();
        for (Realm realm : securityManager.getRealms()) {
            if (realm instanceof JdbcMysqlRealm) {
                JdbcMysqlRealm mysqlRealm = (JdbcMysqlRealm) realm;
                mysqlRealm.setDataSource(DatabaseUtils.getDataSource());
            }
        }
    }

    public static Connection getConnection() throws SQLException {
        return comboPooledDataSource.getConnection();
    }
}
