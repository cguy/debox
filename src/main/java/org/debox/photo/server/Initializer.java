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

import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.realm.Realm;
import org.debox.photo.dao.JdbcMysqlRealm;
import org.debox.photo.util.DatabaseUtils;
import org.debux.webmotion.server.WebMotionServerListener;
import org.debux.webmotion.server.call.ServerContext;
import org.debux.webmotion.server.mapping.Mapping;
import org.debux.webmotion.server.mapping.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class Initializer implements WebMotionServerListener {
    
    private static final Logger logger = LoggerFactory.getLogger(Initializer.class);
    
    @Override
    public void onStart(Mapping mapping, ServerContext context) {
        logger.info("Initializing Application");
        
        // Load properties
        Properties properties = mapping.getProperties();
        DatabaseUtils.setDataSourceConfiguration(properties);
        
        // Test database configuration
        if (!DatabaseUtils.hasConfiguration() && StringUtils.isEmpty(properties.getString("debox.working.directory"))) {
            logger.info("Application database access & working directory are not configured");
            return;
        }
        
        // Apply datasource to Apache Shiro realms
        final Collection<Realm> realms = ((RealmSecurityManager)SecurityUtils.getSecurityManager()).getRealms();
        for (Realm realm : realms) {
            if (realm instanceof JdbcMysqlRealm) {
                JdbcMysqlRealm mysqlRealm = (JdbcMysqlRealm) realm;
                mysqlRealm.setDataSource(DatabaseUtils.getDataSource());
            }
        }
    }

    @Override
    public void onStop(ServerContext context) {
        logger.info("Destroying web context");
        ComboPooledDataSource dataSource = DatabaseUtils.getDataSource();
        if (dataSource != null) {
            dataSource.close();
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
                    t.stop(); //don't complain, it works
                }
            }
        }
        
        logger.info("Destroyed web context");
    }

}
