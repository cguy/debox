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
import java.util.Enumeration;
import java.util.Set;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.debox.photo.dao.ConfigurationDao;
import org.debox.photo.dao.UserDao;
import org.debox.photo.model.Configuration;
import org.debox.photo.model.user.DeboxUser;
import org.debox.photo.model.Role;
import org.debox.photo.util.DatabaseUtils;
import org.debox.photo.util.StringUtils;
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
        try {
            UserDao userDao = new UserDao();
            int roleCount = userDao.getRoleCount();
            int userCount = userDao.getUsersCount();
            
            Role role = null;
            if (roleCount == 0) {
                role = new Role();
                role.setId(StringUtils.randomUUID());
                role.setName("administrator");
                userDao.save(role);
                
                role = new Role();
                role.setId(StringUtils.randomUUID());
                role.setName("user");
                userDao.save(role);
            }
            
            if (userCount == 0) {
                DeboxUser admin = new DeboxUser();
                admin.setId(StringUtils.randomUUID());
                admin.setUsername("corentin.guy@debox.fr");
                admin.setPassword("password");
                userDao.save(admin, role);
            }
            
            ConfigurationDao configurationDao = new ConfigurationDao();
            Configuration configuration = configurationDao.get();
            if (StringUtils.isEmpty(configuration.get(Configuration.Key.TITLE))) {
                configuration.set(Configuration.Key.TITLE, "Galerie photo");
                configurationDao.save(configuration);
            }
            sce.getServletContext().setAttribute("configuration", configuration);
        } catch (SQLException ex) {
            logger.error("Unable to access database", ex);
        }
        logger.info("Initialized web context");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
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
