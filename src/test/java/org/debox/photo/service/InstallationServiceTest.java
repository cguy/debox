package org.debox.photo.service;

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
import java.io.IOException;
import java.net.HttpURLConnection;
import org.apache.commons.io.FileUtils;
import org.debox.photo.utils.HttpUtil;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class InstallationServiceTest {
    
    private static final Logger log = LoggerFactory.getLogger(InstallationServiceTest.class);
    
    protected static final String URI_DATASOURCE = "/datasource";
    protected static final String URI_WORKING_DIRECTORY = "/working-directory";
    protected static final String URI_ADMINISTRATOR_REGISTRATION = "/register-administrator";
    
    @Test
    public void testDatabaseSetup() throws IOException {
        int status = HttpUtil.postAndGetHttpStatus(URI_DATASOURCE, "type", "h2", "url","h2:/tmp/debox/database", "username","sa", "password","");
        assertEquals(status, HttpURLConnection.HTTP_NO_CONTENT);
    }
    
    @Test
    public void testWorkingDirectorySetup() throws IOException {
        // Init use case
        testDatabaseSetup();
        
        int status = HttpUtil.postAndGetHttpStatus(URI_WORKING_DIRECTORY, "path", "/tmp/debox-media");
        assertEquals(status, HttpURLConnection.HTTP_NO_CONTENT);
    }
    
    @Test
    public void testAdministrationRegistration() throws IOException {
        // Init use case
        testWorkingDirectorySetup();
        
        int status = HttpUtil.postAndGetHttpStatus(URI_ADMINISTRATOR_REGISTRATION, "username", null, "password", "password", "confirm", "password", "firstname", "John", "lastname", "Smith");
        assertEquals(status, HttpURLConnection.HTTP_PRECON_FAILED);
        status = HttpUtil.postAndGetHttpStatus(URI_ADMINISTRATOR_REGISTRATION, "username", "admin", "password", null, "confirm", "password", "firstname", "John", "lastname", "Smith");
        assertEquals(status, HttpURLConnection.HTTP_PRECON_FAILED);
        status = HttpUtil.postAndGetHttpStatus(URI_ADMINISTRATOR_REGISTRATION, "username", "admin", "password", "password", "confirm", null, "firstname", "John", "lastname", "Smith");
        assertEquals(status, HttpURLConnection.HTTP_PRECON_FAILED);
        status = HttpUtil.postAndGetHttpStatus(URI_ADMINISTRATOR_REGISTRATION, "username", "admin", "password", "password", "confirm", "password", "firstname", null, "lastname", "Smith");
        assertEquals(status, HttpURLConnection.HTTP_PRECON_FAILED);
        status = HttpUtil.postAndGetHttpStatus(URI_ADMINISTRATOR_REGISTRATION, "username", "admin", "password", "password", "confirm", "password", "firstname", "John", "lastname", null);
        assertEquals(status, HttpURLConnection.HTTP_PRECON_FAILED);
        status = HttpUtil.postAndGetHttpStatus(URI_ADMINISTRATOR_REGISTRATION, "username", "admin", "password", "firstPassword", "confirm", "NotTheSame", "firstname", "John", "lastname", "Smith");
        assertEquals(status, HttpURLConnection.HTTP_PRECON_FAILED);
        
        status = HttpUtil.postAndGetHttpStatus(URI_ADMINISTRATOR_REGISTRATION, "username", "admin", "password", "password", "confirm", "password", "firstname", "John", "lastname", "Smith");
        assertEquals(status, HttpURLConnection.HTTP_NO_CONTENT);
    }
    
    @After
    public void deleteDatabaseFile() {
        try {
            FileUtils.deleteDirectory(new File("/tmp/debox"));
        } catch (IOException ex) {
            log.error("Unable to delete database file, cause: {}", ex);
        }
        try {
            FileUtils.deleteDirectory(new File("/tmp/debox-media"));
        } catch (IOException ex) {
            log.error("Unable to delete working directory, cause: {}", ex);
        }
    }

}
