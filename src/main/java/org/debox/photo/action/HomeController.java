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
package org.debox.photo.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.debox.photo.model.Configuration;
import org.debox.photo.model.User;
import org.debox.photo.server.ApplicationContext;
import org.debux.webmotion.server.render.Render;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class HomeController extends DeboxController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
    
    public Render renderTemplates() {
        Configuration configuration = ApplicationContext.getInstance().getConfiguration();
        String title = configuration.get(Configuration.Key.TITLE);
        
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        String username = null;
        if (user != null) {
            username = user.getUsername();
        }
        
        Map<String, String> headerData = new HashMap<>(2);
        headerData.put("title", title);
        headerData.put("username", username);
        
        Map<String, Object> result = new HashMap<>(2);
        result.put("config", headerData);
        result.put("templates", getTemplates());
        
        return renderJSON(result);
    }

    protected Map<String, Object> getTemplates() {
        Map<String, Object> templates = new HashMap<>();

        try {
            // Note : current path is /WEB-INF/classes/
            URL templatesDirectoryUrl = this.getClass().getClassLoader().getResource("../templates");
            URI templatesURI = templatesDirectoryUrl.toURI();

            File templatesDirectory = new File(templatesURI);
            if (templatesDirectory != null && templatesDirectory.isDirectory()) {
                for (File child : templatesDirectory.listFiles()) {
                    try (FileInputStream fis = new FileInputStream(child)) {
                        
                        String filename = StringUtils.substringBeforeLast(child.getName(), ".");
                        String content = IOUtils.toString(fis, "UTF-8");
                        if ((SecurityUtils.getSubject().isAuthenticated() && filename.contains("admin")) || !filename.contains("admin")) {
                            templates.put(filename, content);
                        } else {
                            templates.put(filename, "");
                        }

                    } catch (IOException ex) {
                        logger.error("Unable to load template " + child.getAbsolutePath(), ex);
                    }
                }
            }

        } catch (URISyntaxException ex) {
            logger.error("Unable to load templates", ex);
        }

        return templates;
    }
}
