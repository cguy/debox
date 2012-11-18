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
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.debox.photo.util.DatabaseUtils;
import org.debux.webmotion.server.mapping.Properties;
import org.debux.webmotion.server.render.Render;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class InstallationService extends DeboxService {
    
    private static final Logger log = LoggerFactory.getLogger(InstallationService.class);
    
    public Render setWorkingDirectory(String path) {
        Path workingPath = Paths.get(path);
        File workingDirectory = workingPath.toFile();
        boolean isCreatable = !workingDirectory.exists() && workingDirectory.mkdirs();
        boolean isWritable = workingDirectory.exists() && workingDirectory.canWrite();
        if (!isWritable && !isCreatable) {
            return renderError(HttpURLConnection.HTTP_BAD_REQUEST, "Given path is not writable");
        }
        return renderSuccess();
    }
    
    public Render setDataSource(Properties properties, String host, String port, String name, String username, String password) {
        properties.addProperty(DatabaseUtils.PROPERTY_DATABASE_HOST, host);
        properties.addProperty(DatabaseUtils.PROPERTY_DATABASE_PORT, port);
        properties.addProperty(DatabaseUtils.PROPERTY_DATABASE_NAME, name);
        properties.addProperty(DatabaseUtils.PROPERTY_DATABASE_USERNAME, username);
        properties.addProperty(DatabaseUtils.PROPERTY_DATABASE_PASSWORD, password);
        DatabaseUtils.setDataSourceConfiguration(properties);
        
        String message = null;
        try (
            Connection connection = DatabaseUtils.getDataSource().getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT 1");
        ) {
            statement.executeQuery();

        } catch (Exception ex) {
            message = ex.getMessage();
        }
        
        if (message != null) {
            getContext().getResponse().setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
            return renderJSON("message", message);
        }
        return renderSuccess();
    }
    
}
