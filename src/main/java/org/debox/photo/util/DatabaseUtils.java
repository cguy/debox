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
import com.mchange.v2.c3p0.DataSources;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.dbutils.DbUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.realm.Realm;
import org.debox.photo.dao.DeboxJdbcRealm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class DatabaseUtils {

    private static final Logger logger = LoggerFactory.getLogger(DeboxJdbcRealm.class);
    public static final String PROPERTY_DATABASE_HOST = "database.host";
    public static final String PROPERTY_DATABASE_NAME = "database.name";
    public static final String PROPERTY_DATABASE_USERNAME = "database.username";
    public static final String PROPERTY_DATABASE_PASSWORD = "database.password";
    public static final String TEST_QUERY = "SELECT 1";
    protected static ComboPooledDataSource comboPooledDataSource;
    protected static Configuration properties = null;
    protected static final Object lock = new Object();
    
    public static Configuration getConfiguration() {
        return properties;
    }

    public static synchronized void setDataSourceConfiguration(Configuration properties) {
        DatabaseUtils.properties = properties;
    }

    public static boolean hasConfiguration() {
        return properties != null && !StringUtils.atLeastOneIsEmpty(
            properties.getString(PROPERTY_DATABASE_HOST),
            properties.getString(PROPERTY_DATABASE_NAME),
            properties.getString(PROPERTY_DATABASE_USERNAME));
    }
    
    public static boolean testConnection() {
        boolean result = true;
        try (
            Connection connection = DatabaseUtils.getDataSource(true).getConnection();
            PreparedStatement statement = connection.prepareStatement(TEST_QUERY);
        ) {
            statement.executeQuery();
        } catch (SQLException ex) {
            logger.error("Unable to connect to the database", ex);
            result = false;
        }
        return result;
    }

    public static ComboPooledDataSource getDataSource() {
        return getDataSource(false);
    }
    
    public static ComboPooledDataSource getDataSource(boolean forceRefreshProperties) {
        if (comboPooledDataSource == null && hasConfiguration() || forceRefreshProperties) {
            synchronized (lock) {
                if (comboPooledDataSource != null && !forceRefreshProperties) {
                    return comboPooledDataSource;
                }
                if (forceRefreshProperties) {
                    try {
                        DataSources.destroy(comboPooledDataSource);
                    } catch (SQLException ex) {
                        logger.error("Unable to destroy datasource", ex);
                    }
                    comboPooledDataSource = null;
                }

                Connection connection = null;
                try {
                    comboPooledDataSource = new ComboPooledDataSource();
                    comboPooledDataSource.setDriverClass("com.mysql.jdbc.Driver");

                    String user = properties.getString(PROPERTY_DATABASE_USERNAME);
                    String password = properties.getString(PROPERTY_DATABASE_PASSWORD, "");
                    String host = properties.getString(PROPERTY_DATABASE_HOST);
                    String databaseName = properties.getString(PROPERTY_DATABASE_NAME);
                    String url = "jdbc:mysql://" + host + "/" + databaseName;

                    comboPooledDataSource.setJdbcUrl(url);
                    comboPooledDataSource.setUser(user);
                    comboPooledDataSource.setPassword(password);
                    comboPooledDataSource.setIdleConnectionTestPeriod(300);
                    comboPooledDataSource.setPreferredTestQuery(TEST_QUERY);
                    comboPooledDataSource.setMinPoolSize(5);
                    comboPooledDataSource.setMaxPoolSize(20);
                    comboPooledDataSource.setInitialPoolSize(10);
                    comboPooledDataSource.setCheckoutTimeout(5000);

                    connection = comboPooledDataSource.getConnection();

                } catch (SQLException | PropertyVetoException ex) {
                    logger.error(ex.getMessage(), ex);
                } finally {
                    DbUtils.closeQuietly(connection);
                }
            }
        }
        return comboPooledDataSource;
    }
    
    public static void applyDatasourceToShiro() {
        RealmSecurityManager securityManager = (RealmSecurityManager) SecurityUtils.getSecurityManager();
        for (Realm realm : securityManager.getRealms()) {
            if (realm instanceof DeboxJdbcRealm) {
                DeboxJdbcRealm jdbcRealm = (DeboxJdbcRealm) realm;
                jdbcRealm.setDataSource(DatabaseUtils.getDataSource());
            }
        }
    }

    public static Connection getConnection() throws SQLException {
        return comboPooledDataSource.getConnection();
    }
}
