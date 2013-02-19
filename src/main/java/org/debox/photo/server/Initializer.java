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
package org.debox.photo.server;

import com.mchange.v2.c3p0.DataSources;
import java.io.File;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Set;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.debox.photo.util.DatabaseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class Initializer implements ServletContextListener {
    
    private static final Logger logger = LoggerFactory.getLogger(Initializer.class);
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("Initializing web context");
        File configurationFile = DatabaseUtils.getConfigurationFilePath().toFile();
        if (configurationFile.exists()) {
            try {
                PropertiesConfiguration configuration = new PropertiesConfiguration(configurationFile);
                DatabaseUtils.setDataSourceConfiguration(configuration);
            } catch (ConfigurationException ex) {
                logger.error("Unable to load debox configuration from file {}", configurationFile.getAbsolutePath(), ex);
            }
        } else {
            logger.warn("Configuration file {} doesn't exist.", configurationFile.getAbsolutePath());
        }
        logger.info("Web context initialized");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("Destroying web context");
        try {
            DataSources.destroy(DatabaseUtils.getDataSource());
        } catch (SQLException ex) {
            logger.error("Unable to destroy datasource", ex.getMessage());
        }
        
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
            } catch (SQLException ex) {
                logger.error("Unable to deregister driver {}, cause: {}", driver.getClass().getName(), ex.getMessage());
            }
        }
        
        // TODO Check this source code ...
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);
        for(Thread t:threadArray) {
            if(t.getName().contains("Abandoned connection cleanup thread")) {
                synchronized(t) {
                    t.interrupt();
                }
            }
        }
        logger.info("Destroyed web context");
    }

}
