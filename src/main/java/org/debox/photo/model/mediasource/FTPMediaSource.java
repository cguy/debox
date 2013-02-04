package org.debox.photo.model.mediasource;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.net.ftp.FTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class FTPMediaSource extends MediaSource {
    
    private static final Logger log = LoggerFactory.getLogger(FTPMediaSource.class);

    protected Type type = Type.FTP;
    
    protected String host;
    protected int port = FTP.DEFAULT_PORT;
    protected String username;
    protected String password;
    
    public FTPMediaSource(String host, String sourcePath, String targetPath) {
        this.host = host;
        this.sourcePath = sourcePath;
        this.targetPath = targetPath;
    }
    
    public FTPMediaSource(String host, String sourcePath, String targetPath, String username, String password) {
        this(host, sourcePath, targetPath);
        this.username = username;
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    @Override
    public String toString() {
        String string;
        try {
            ObjectMapper mapper = new ObjectMapper();
            
            Map<String, Object> map = new HashMap<>();
            map.put("host", getHost());
            map.put("port", getPort());
            map.put("username", getUsername());
            map.put("password", getPassword());
            map.put("sourcePath", getSourcePath());
            map.put("targetPath", getTargetPath());
            
            string = mapper.writeValueAsString(map);
            
        } catch (JsonProcessingException ex) {
            log.error("Unable to serialize object", ex);
            string = super.toString();
        }
        return string;
    }
    
}
