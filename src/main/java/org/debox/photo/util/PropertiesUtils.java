package org.debox.photo.util;

/*
 * #%L
 * debox-photos
 * %%
 * Copyright (C) 2012 - 2013 Debox
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
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.debux.webmotion.server.WebMotionUtils;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class PropertiesUtils {
    
    protected static final Object lock = new Object();
    protected static PropertiesConfiguration configuration = null;
    
    public static String getSecretKey() throws ConfigurationException {
        return getConfiguration().getString("application.secret");
    }
    
    public static String getCryptographyIV() throws ConfigurationException {
        return getConfiguration().getString("application.secret.iv");
    }
    
    public static Path getConfigurationFilePath() {
        String strPath = System.getProperty("debox.configuration");
        if (strPath != null) {
            return Paths.get(strPath);
        }
        return Paths.get(WebMotionUtils.getUserConfigurationPath(), "debox.properties");
    }
    
    public static PropertiesConfiguration getConfiguration() throws ConfigurationException {
        if (configuration == null) {
            synchronized(lock) {
                if (configuration == null) {
                    File file = getConfigurationFilePath().toFile();
                    configuration = new PropertiesConfiguration(file);
                }
            }
        }
        return configuration;
    }
    
    public static boolean isConfigured() {
        return getConfigurationFilePath().toFile().exists();
    }
    
    public static PropertiesConfiguration getConfigurationCopy() throws ConfigurationException {
        return (PropertiesConfiguration) getConfiguration().clone();
    }
    
    public static void save() throws ConfigurationException {
        configuration.save(getConfigurationFilePath().toFile());
    }
    
}
