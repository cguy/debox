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
package org.debox.photo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.debox.imaging.ImageUtils;
import org.debox.photo.dao.PhotoDao;
import org.debox.photo.exception.HttpException;
import org.debox.photo.exception.InternalErrorException;
import org.debox.photo.model.Configuration;
import org.debox.photo.model.Media;
import org.debox.photo.model.configuration.ThumbnailSize;
import org.debox.photo.model.user.DeboxUser;
import org.debox.photo.model.user.ThirdPartyAccount;
import org.debox.photo.model.user.User;
import org.debox.photo.server.ApplicationContext;
import org.debox.photo.server.renderer.JacksonRenderJsonImpl;
import org.debox.photo.util.SessionUtils;
import org.debox.photo.util.TemplateUtils;
import org.debox.util.HttpUtils;
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.render.Render;
import org.debux.webmotion.server.render.RenderStatus;
import org.scribe.exceptions.OAuthException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.context.WebContext;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class DeboxService extends WebMotionController {

    private static final Logger logger = LoggerFactory.getLogger(DeboxService.class);
    protected static PhotoDao photoDao = new PhotoDao();
    
    protected ServletContext getServletContext() {
        return this.getContext().getServletContext();
    }
    
    protected HttpServletRequest getRequest() {
        return this.getContext().getRequest();
    }
    
    protected HttpServletResponse getResponse() {
        return this.getContext().getResponse();
    }

    @Override
    public Render renderSuccess() {
        return renderStatus(HttpURLConnection.HTTP_NO_CONTENT);
    }
    
    public Render renderHttpError(HttpException ex) {
        return renderError(ex.getHttpStatus(), ex.getMessage());
    }
    
    public Render render(String key, Object... model) {
        String[] values = getContext().getRequest().getHeader("Accept").split("(\\,|;)");
        for (String accept : values) {
            if ("text/html".equals(accept) || "application/xhtml+xml".equals(accept)) {
                break;
            } else if ("application/json".equals(accept)) {
                return renderJSON(model);
            }
        }
        return renderTemplatedPage(key, model);
    }
    
    public Render renderTemplatedPage(String templateName, Object... model) {
        Render result;
        try {
            WebContext context = new WebContext(getRequest(), getResponse(), getServletContext());
            if (model != null) {
                context.setVariables(toMap(model));
            }
            context.setVariable("config", getHeaderData());
            context.setVariable("body", templateName);
            
            String structure = TemplateUtils.getTemplateEngine(getServletContext()).process("structure", context);
            
            result = renderContent(structure, "text/html");
            
        } catch (Exception ex) {
            logger.error("Unable to render template", ex);
            throw new InternalErrorException();
        }
        return result;
    }
    
    @Override
    public JacksonRenderJsonImpl renderJSON(Object... model) {
        return new JacksonRenderJsonImpl(toMap(model));
    }
    
    @Override
    protected Map<String, Object> toMap(Object... model) {
        if (model.length == 1) {
            if (model[0] instanceof Map) {
                return (Map) model[0];
            }
            Map<String, Object> map = new LinkedHashMap<>(1);
            map.put(null, model[0]);
            return map;
        }
        
        Map<String, Object> map = new LinkedHashMap<>(model.length / 2);
        for (int index = 0; index < model.length; index += 2) {
            if (model[index] instanceof String) {
                String key = (String) model[index];
                Object value = model[index + 1];

                map.put(key, value);
            } else if (model[index] instanceof Map) {
                map.putAll((Map) model[index]);
                index--;
            }
        }

        return map;
    }
    
    protected Map<String, Object> getHeaderData() {
        Configuration configuration = ApplicationContext.getInstance().getOverallConfiguration();
        String title = configuration.get(Configuration.Key.TITLE);
        Subject subject = SecurityUtils.getSubject();
        String username = getUsername();
        User user = SessionUtils.getUser(subject);
        String userId = null;
        if (user != null) {
            userId = user.getId();
        }
        Map<String, Object> headerData = new HashMap<>(6);
        headerData.put("title", title);
        headerData.put("avatar", null);
        headerData.put("username", username);
        headerData.put("userId", userId);
        headerData.put("administrator", subject.hasRole("administrator"));
        headerData.put("authenticated", SessionUtils.isLogged(subject));
        return headerData;
    }
    
    protected String getUsername() {
        Subject subject = SecurityUtils.getSubject();
        String username = null;
        Object principal = subject.getPrincipal();
        if (SessionUtils.isLogged(subject)) {
            if (principal instanceof DeboxUser) {
                DeboxUser user = (DeboxUser) principal;
                if (user.getFirstName() != null && user.getLastName() != null) {
                    username = user.getFirstName() + " " + user.getLastName();
                } else {
                    username = user.getUsername();
                }

            } else if (principal instanceof ThirdPartyAccount) {
                ThirdPartyAccount user = (ThirdPartyAccount) principal;
                switch (user.getProviderId()) {
                    case "facebook":
                        FacebookClient client = new DefaultFacebookClient(user.getToken());
                        com.restfb.types.User me = client.fetchObject("me", com.restfb.types.User.class);
                        username = me.getFirstName() + " " + me.getLastName();
                        break;
                    case "google":
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
                        break;
                }

            }
        }
        return username;
    }

    protected RenderStatus handleLastModifiedHeader(Media media, ThumbnailSize size) {
        try {
            long lastModified = photoDao.getGenerationTime(media.getId(), size);
            long ifModifiedSince = getContext().getRequest().getDateHeader("If-Modified-Since");

            if (lastModified == -1) {
                String strPath = ImageUtils.getThumbnailPath(media, size);
                Path path = Paths.get(strPath);

                try {
                    BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
                    FileTime lastModifiedTimeAttribute = attributes.lastModifiedTime();

                    lastModified = lastModifiedTimeAttribute.toMillis();
                    photoDao.saveThumbnailGenerationTime(media.getId(), size, lastModified);

                } catch (IOException ioe) {
                    logger.error("Unable to access last modified property from file: " + strPath, ioe);
                }

                logger.warn("Get -1 value for photo " + media.getFilename() + " and size " + size.name());
            }

            if (lastModified != -1) {
                getContext().getResponse().addDateHeader("Last-Modified", lastModified);
                if (lastModified <= ifModifiedSince) {
                    return new RenderStatus(HttpURLConnection.HTTP_NOT_MODIFIED);
                }
            }

        } catch (SQLException ex) {
            logger.error("Unable to handle Last-Modified header, cause : " + ex.getMessage(), ex);
        }

        return new RenderStatus(HttpURLConnection.HTTP_NO_CONTENT);
    }

}
