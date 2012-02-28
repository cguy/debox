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
import java.sql.SQLException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.debox.photo.dao.ConfigurationDao;
import org.debox.photo.dao.UserDao;
import org.debox.photo.dao.JdbcMysqlRealm;
import org.debox.photo.model.Configuration;
import org.debox.photo.model.User;
import org.debox.photo.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
@WebListener
public class Initializer implements ServletContextListener {
    
    private static final Logger logger = LoggerFactory.getLogger(Initializer.class);
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            UserDao userDao = new UserDao();
            int userCount = userDao.getUsersCount();
            if (userCount == 0) {
                User admin = new User();
                admin.setId(StringUtils.randomUUID());
                admin.setUsername("admin");
                admin.setPassword("password");
                
                userDao.save(admin);
            }
            
            ConfigurationDao configurationDao = new ConfigurationDao();
            Configuration configuration = configurationDao.get();
            if (StringUtils.isEmpty(configuration.get(Configuration.Key.TITLE))) {
                configuration.set(Configuration.Key.TITLE, "Galerie photo");
                configurationDao.save(configuration);
            }
        } catch (SQLException ex) {
            logger.error("Unable to access database", ex);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("Destroying web context");
        ComboPooledDataSource dataSource = JdbcMysqlRealm.getDataSource();
        if (dataSource != null) {
            dataSource.close();
        }
    }

}
