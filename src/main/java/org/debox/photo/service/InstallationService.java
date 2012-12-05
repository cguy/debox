package org.debox.photo.service;

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

import java.io.File;
import java.net.HttpURLConnection;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.debox.photo.dao.UserDao;
import org.debox.photo.model.Configuration;
import org.debox.photo.model.Role;
import org.debox.photo.model.user.DeboxUser;
import org.debox.photo.server.ApplicationContext;
import org.debox.photo.util.DatabaseUtils;
import org.debox.photo.util.StringUtils;
import org.debux.webmotion.server.WebMotionUtils;
import org.debux.webmotion.server.render.Render;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class InstallationService extends DeboxService {
    
    private static final Logger log = LoggerFactory.getLogger(InstallationService.class);
    
    protected UserDao userDao = new UserDao();
    
    public Render setWorkingDirectory(String path) {
        Path workingPath = Paths.get(path);
        File workingDirectory = workingPath.toFile();
        boolean isCreatable = !workingDirectory.exists() && workingDirectory.mkdirs();
        boolean isWritable = workingDirectory.exists() && workingDirectory.canWrite();
        if (!isWritable && !isCreatable) {
            return renderError(HttpURLConnection.HTTP_BAD_REQUEST, "Given path is not writable");
        }
        
        ApplicationContext.getInstance().getOverallConfiguration().set(Configuration.Key.WORKING_DIRECTORY, workingPath.toAbsolutePath().toString());
        return renderSuccess();
    }
    
    public Render setDataSource(String host, String port, String name, String username, String password) {
        CompositeConfiguration configuration = new CompositeConfiguration();
        configuration.addProperty(DatabaseUtils.PROPERTY_DATABASE_HOST, host);
        configuration.addProperty(DatabaseUtils.PROPERTY_DATABASE_PORT, port);
        configuration.addProperty(DatabaseUtils.PROPERTY_DATABASE_NAME, name);
        configuration.addProperty(DatabaseUtils.PROPERTY_DATABASE_USERNAME, username);
        configuration.addProperty(DatabaseUtils.PROPERTY_DATABASE_PASSWORD, password);
        DatabaseUtils.setDataSourceConfiguration(configuration);
        
        boolean connectionTest = DatabaseUtils.testConnection();
        if (!connectionTest) {
            getContext().getResponse().setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
            return renderJSON("message", "Unable to connect to the database, please see server logs.");
        }
        return renderSuccess();
    }
    
    public Render createUserAndRoles(String username, String password, String firstname, String lastname) throws SQLException {
        if (StringUtils.atLeastOneIsEmpty(username, password, firstname, lastname)) {
            return renderError(HttpURLConnection.HTTP_PRECON_FAILED, "Username, password, firstname and lastname are mandatory.");
        }
        
        DeboxUser user = new DeboxUser();
        user.setId(StringUtils.randomUUID());
        user.setUsername(username);
        user.setPassword(password);
        user.setFirstName(firstname);
        user.setLastName(lastname);
        
        Role administratorRole = new Role();
        administratorRole.setId(StringUtils.randomUUID());
        administratorRole.setName("administrator");
        
        Role userRole = new Role();
        userRole.setId(StringUtils.randomUUID());
        userRole.setName("user");
        
        try {
            saveDatabaseConfiguration();
            userDao.save(administratorRole);
            userDao.save(userRole);
            userDao.save(user, administratorRole);
            
            Configuration configuration = ApplicationContext.getInstance().getOverallConfiguration();
            configuration.set(Configuration.Key.TITLE, "debox");
            ApplicationContext.getInstance().saveConfiguration(configuration);
            ApplicationContext.setConfigured(true);
            
            DatabaseUtils.applyDatasourceToShiro();
            
        } catch (ConfigurationException ex) {
            log.error("Unable to save database configuration, cause: {}", ex.getMessage(), ex);
            getContext().getResponse().setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
            return renderJSON("message", ex.getMessage());
        } catch (SQLException ex) {
            log.error("Unable to register user {}, cause: {}", username, ex.getErrorCode() + " - " + ex.getMessage(), ex);
            getContext().getResponse().setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
            return renderJSON("message", ex.getMessage());
        }
        
        return renderSuccess();
    }
    
    protected void saveDatabaseConfiguration() throws ConfigurationException {
        Path path = Paths.get(WebMotionUtils.getUserConfigurationPath(), "debox.properties");
        PropertiesConfiguration configuration = new PropertiesConfiguration();
        configuration.append(DatabaseUtils.getConfiguration());
        configuration.save(path.toString());
    }
    
}
