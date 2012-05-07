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
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import org.debox.photo.dao.JdbcMysqlRealm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class DatabaseUtils {

    private static final Logger logger = LoggerFactory.getLogger(JdbcMysqlRealm.class);
    protected static final String PROPERTY_DATABASE_HOST = "database.host";
    protected static final String PROPERTY_DATABASE_PORT = "database.port";
    protected static final String PROPERTY_DATABASE_NAME = "database.name";
    protected static final String PROPERTY_DATABASE_USERNAME = "database.username";
    protected static final String PROPERTY_DATABASE_PASSWORD = "database.password";
    protected static final String PROPERTY_DATABASE_IDLE_CONNECTION_TEST_PERIOD = "database.idle.connection.test.period";
    protected static final String PROPERTY_DATABASE_PREFERED_TEST_QUERY = "database.prefered.test.query";
    protected static ComboPooledDataSource comboPooledDataSource;

    public static synchronized ComboPooledDataSource getDataSource() {
        if (comboPooledDataSource == null) {
            try {
                Properties properties = new Properties();
                properties.load(JdbcMysqlRealm.class.getClassLoader().getResourceAsStream("application.properties"));

                comboPooledDataSource = new ComboPooledDataSource();
                comboPooledDataSource.setDriverClass("com.mysql.jdbc.Driver");

                String host = properties.getProperty(PROPERTY_DATABASE_HOST);
                String port = properties.getProperty(PROPERTY_DATABASE_PORT);
                String name = properties.getProperty(PROPERTY_DATABASE_NAME);
                String user = properties.getProperty(PROPERTY_DATABASE_USERNAME);
                String password = properties.getProperty(PROPERTY_DATABASE_PASSWORD);
                Integer period = Integer.valueOf(properties.getProperty(PROPERTY_DATABASE_IDLE_CONNECTION_TEST_PERIOD));
                String testQuery = properties.getProperty(PROPERTY_DATABASE_PREFERED_TEST_QUERY);

                String url = "jdbc:mysql://" + host + ':' + port + '/' + name;
                comboPooledDataSource.setJdbcUrl(url);
                comboPooledDataSource.setUser(user);
                comboPooledDataSource.setPassword(password);
                comboPooledDataSource.setIdleConnectionTestPeriod(period);
                comboPooledDataSource.setPreferredTestQuery(testQuery);

            } catch (IOException | PropertyVetoException ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
        return comboPooledDataSource;
    }
    
    public static Connection getConnection() throws SQLException {
        return comboPooledDataSource.getConnection();
    }
    
}
