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

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.debox.photo.model.Configuration;
import org.debox.photo.model.DeboxUser;
import org.debox.photo.model.Provider;
import org.debox.photo.model.ThirdPartyAccount;
import org.debox.photo.server.ApplicationContext;
import org.debox.photo.thirdparty.ServiceUtil;
import org.debox.photo.util.SessionUtils;
import org.debox.util.HttpUtils;
import org.debux.webmotion.server.render.Render;
import org.scribe.exceptions.OAuthException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class HomeController extends DeboxController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
    
    public static String getUsername() {
        Subject subject = SecurityUtils.getSubject();
        String username = null;
        Object principal = subject.getPrincipal();
        if (SessionUtils.isLogged(subject)) {
            if (principal instanceof DeboxUser) {
                DeboxUser user = (DeboxUser) principal;
                username = user.getUsername();
            } else if (principal instanceof ThirdPartyAccount) {
                ThirdPartyAccount user = (ThirdPartyAccount) principal;
                
                if (user.getProviderId().equals("facebook")) {
                    FacebookClient client = new DefaultFacebookClient(user.getToken());
                    com.restfb.types.User me = client.fetchObject("me", com.restfb.types.User.class);
                    username = me.getFirstName() + " " + me.getLastName();
                    
                } else if (user.getProviderId().equals("google")) {
                    try {
                        String response = HttpUtils.getResponse("https://www.googleapis.com/oauth2/v2/userinfo?access_token=" + user.getToken());
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode node = mapper.readTree(response);
                        if (node.get("error") != null && node.get("error").get("code") != null) {
                            if (node.get("error").get("code").asInt() == 401) {
                                throw new OAuthException("google");
                            }
                        }
                        username = node.get("name").asText();
                        
                    } catch (IOException ex) {
                        logger.error("Unable to get Google session", ex);
                    }
                }
                
            }
        }
        return username;
    }
    
    public Render renderTemplates() {
        Configuration configuration = ApplicationContext.getInstance().getConfiguration();
        String title = configuration.get(Configuration.Key.TITLE);
        
        Subject subject = SecurityUtils.getSubject();
        String username = getUsername();
        
        Map<String, Object> headerData = new HashMap<>(2);
        headerData.put("title", title);
        headerData.put("username", username);
        headerData.put("isAdmin", subject.hasRole("administrator"));
        
        Map<String, Object> result = new HashMap<>(2);
        result.put("config", headerData);
        result.put("templates", getTemplates());
        List<Provider> providers = ServiceUtil.getAuthenticationUrls();
        result.put("hasProviders", providers != null);
        result.put("providers", providers);
        
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
                        if ((SessionUtils.isAdministrator(SecurityUtils.getSubject()) && filename.startsWith("administration")) || !filename.startsWith("administration")) {
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
