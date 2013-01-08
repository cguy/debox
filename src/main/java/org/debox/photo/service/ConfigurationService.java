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

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.apache.shiro.SecurityUtils;
import org.debox.photo.model.Configuration;
import org.debox.photo.model.configuration.ThirdPartyConfiguration;
import org.debox.photo.server.ApplicationContext;
import org.debox.photo.util.SessionUtils;
import org.debox.photo.util.StringUtils;
import org.debux.webmotion.server.render.Render;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class ConfigurationService extends DeboxService {
    
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationService.class);

    public Render getConfiguration() {
        Configuration configuration = ApplicationContext.getInstance().getOverallConfiguration();
        
        Configuration.Key[] dontTransform = new Configuration.Key[] {Configuration.Key.TITLE, Configuration.Key.WORKING_DIRECTORY};
        
        Map<String, Object> model = new HashMap<>();
        for (Configuration.Key key : dontTransform) {
            model.put(key.getId(), configuration.get(key));
        }
        
        model.put("thirdPartyActivation", configuration.get(Configuration.Key.THIRDPARTY_ACTIVATION));
        
        ThirdPartyConfiguration facebook = new ThirdPartyConfiguration();
        facebook.setApiKey(configuration.get(Configuration.Key.FACEBOOK_API_KEY));
        facebook.setSecret(configuration.get(Configuration.Key.FACEBOOK_SECRET));
        facebook.setCallbackURL(configuration.get(Configuration.Key.FACEBOOK_CALLBACK_URL));
        model.put("facebook", facebook);
        
//        ThirdPartyConfiguration google = new ThirdPartyConfiguration();
//        google.setApiKey(configuration.get(Configuration.Key.GOOGLE_API_KEY));
//        google.setSecret(configuration.get(Configuration.Key.GOOGLE_SECRET));
//        google.setCallbackURL(configuration.get(Configuration.Key.GOOGLE_CALLBACK_URL));
//        model.put("google", google);
//        
//        ThirdPartyConfiguration twitter = new ThirdPartyConfiguration();
//        twitter.setApiKey(configuration.get(Configuration.Key.TWITTER_API_KEY));
//        twitter.setSecret(configuration.get(Configuration.Key.TWITTER_SECRET));
//        twitter.setCallbackURL(configuration.get(Configuration.Key.TWITTER_CALLBACK_URL));
//        model.put("twitter", twitter);
        
        return renderJSON(model);
    }

    public Render editConfiguration(String title, String workingDirectory) throws IOException, SQLException {
        Path path = Paths.get(workingDirectory);

        boolean error = false;
        if (!Files.isDirectory(path) && !path.toFile().mkdirs()) {
            getContext().addErrorMessage("workingDirectory", "path.notdirectory");
            error = true;
        }
        if (StringUtils.isEmpty(title)) {
            getContext().addErrorMessage("name", "isEmpty");
            error = true;
        }

        if (error) {
            return renderStatus(500);
        }

        getContext().addInfoMessage("success", "configuration.edit.success");

        Configuration configuration = new Configuration();
        configuration.set(Configuration.Key.WORKING_DIRECTORY, workingDirectory);
        configuration.set(Configuration.Key.TITLE, title);
        ApplicationContext.getInstance().saveConfiguration(configuration);

        return renderJSON(configuration.get());
    }
    
    public Render editThirdPartyConfiguration(boolean activated, ThirdPartyConfiguration facebook, 
            ThirdPartyConfiguration google, ThirdPartyConfiguration twitter) throws SQLException {
        
        Configuration configuration = ApplicationContext.getInstance().getOverallConfiguration();
        configuration.set(Configuration.Key.THIRDPARTY_ACTIVATION, Boolean.toString(activated));
        
        configuration.set(Configuration.Key.FACEBOOK_API_KEY, facebook.getApiKey());
        configuration.set(Configuration.Key.FACEBOOK_SECRET, facebook.getSecret());
        configuration.set(Configuration.Key.FACEBOOK_CALLBACK_URL, facebook.getCallbackURL());
        
//        configuration.set(Configuration.Key.GOOGLE_API_KEY, google.getApiKey());
//        configuration.set(Configuration.Key.GOOGLE_SECRET, google.getSecret());
//        configuration.set(Configuration.Key.GOOGLE_CALLBACK_URL, google.getCallbackURL());
//        
//        configuration.set(Configuration.Key.TWITTER_API_KEY, twitter.getApiKey());
//        configuration.set(Configuration.Key.TWITTER_SECRET, twitter.getSecret());
//        configuration.set(Configuration.Key.TWITTER_CALLBACK_URL, twitter.getCallbackURL());
        
        ApplicationContext.getInstance().saveConfiguration(configuration);
        
        return renderStatus(HttpURLConnection.HTTP_OK);
    }

    public Render getUserSettings() {
        String userId = SessionUtils.getUser(SecurityUtils.getSubject()).getId();
        try {
            Configuration configuration = ApplicationContext.getInstance().getUserConfiguration(userId);
            String albums = configuration.get(Configuration.Key.ALBUMS_DIRECTORY);
            String thumbnails = configuration.get(Configuration.Key.THUMBNAILS_DIRECTORY);
            boolean autoHosting = albums == null || thumbnails == null;
            return renderJSON("isAutoHosting", autoHosting, "albums", albums, "thumbnails", thumbnails);
            
        } catch (SQLException ex) {
            logger.error(ex.getMessage(), ex);
            return renderError(500);
        }
    }

    public Render setUserSettings(String hostingOption, String albums, String thumbnails) {
        String userId = SessionUtils.getUser(SecurityUtils.getSubject()).getId();
        try {
            Configuration configuration = ApplicationContext.getInstance().getUserConfiguration(userId);
            if ("local".equals(hostingOption)) {
                configuration.set(Configuration.Key.ALBUMS_DIRECTORY, albums);
                configuration.set(Configuration.Key.THUMBNAILS_DIRECTORY, thumbnails);
                
                String[] directories = {albums, thumbnails};
                for (String strDirectory : directories) {
                    File directory = new File(strDirectory);
                    boolean isCreatable = !directory.exists() && directory.mkdirs();
                    boolean isWritable = directory.exists() && directory.canWrite();
                    if (!isWritable && !isCreatable) {
                        return renderError(HttpURLConnection.HTTP_BAD_REQUEST, "Directory " + strDirectory + " is not writable");
                    }
                }
                
            } else {
                configuration.remove(Configuration.Key.ALBUMS_DIRECTORY);
                configuration.remove(Configuration.Key.THUMBNAILS_DIRECTORY);
            }
            ApplicationContext.getInstance().saveUserConfiguration(userId, configuration);
        } catch (SQLException ex) {
            return renderError(500);
        }
        return renderSuccess();
    }
    
}
