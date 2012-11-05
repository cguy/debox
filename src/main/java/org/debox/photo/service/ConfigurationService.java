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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.debox.photo.model.Configuration;
import org.debox.photo.model.configuration.ThirdPartyConfiguration;
import org.debox.photo.server.ApplicationContext;
import org.debox.photo.util.StringUtils;
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.render.Render;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class ConfigurationService extends WebMotionController {

    public Render getConfiguration() {
        Configuration configuration = ApplicationContext.getInstance().getConfiguration();
        
        Configuration.Key[] dontTransform = new Configuration.Key[] {Configuration.Key.TITLE, Configuration.Key.SOURCE_PATH, Configuration.Key.TARGET_PATH};
        
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
        
        ThirdPartyConfiguration google = new ThirdPartyConfiguration();
        google.setApiKey(configuration.get(Configuration.Key.GOOGLE_API_KEY));
        google.setSecret(configuration.get(Configuration.Key.GOOGLE_SECRET));
        google.setCallbackURL(configuration.get(Configuration.Key.GOOGLE_CALLBACK_URL));
        model.put("google", google);
        
        ThirdPartyConfiguration twitter = new ThirdPartyConfiguration();
        twitter.setApiKey(configuration.get(Configuration.Key.TWITTER_API_KEY));
        twitter.setSecret(configuration.get(Configuration.Key.TWITTER_SECRET));
        twitter.setCallbackURL(configuration.get(Configuration.Key.TWITTER_CALLBACK_URL));
        model.put("twitter", twitter);
        
        return renderJSON(model);
    }

    public Render editConfiguration(String title, String sourceDirectory, String targetDirectory) throws IOException, SQLException {
        Path source = Paths.get(sourceDirectory);
        Path target = Paths.get(targetDirectory);

        boolean error = false;
        if (!Files.isDirectory(source)) {
            getContext().addErrorMessage("source", "source.notdirectory");
            error = true;
        }

        if (!Files.exists(source)) {
            getContext().addErrorMessage("source", "source.notexist");
            error = true;
        }

        if (source.equals(target)) {
            getContext().addErrorMessage("path", "paths.equals");
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
        configuration.set(Configuration.Key.SOURCE_PATH, sourceDirectory);
        configuration.set(Configuration.Key.TARGET_PATH, targetDirectory);
        configuration.set(Configuration.Key.TITLE, title);
        ApplicationContext.getInstance().saveConfiguration(configuration);

        return renderJSON(configuration.get());
    }
    
    public Render editThirdPartyConfiguration(boolean activated, ThirdPartyConfiguration facebook, 
            ThirdPartyConfiguration google, ThirdPartyConfiguration twitter) throws SQLException {
        
        Configuration configuration = ApplicationContext.getInstance().getConfiguration();
        configuration.set(Configuration.Key.THIRDPARTY_ACTIVATION, Boolean.toString(activated));
        
        configuration.set(Configuration.Key.FACEBOOK_API_KEY, facebook.getApiKey());
        configuration.set(Configuration.Key.FACEBOOK_SECRET, facebook.getSecret());
        configuration.set(Configuration.Key.FACEBOOK_CALLBACK_URL, facebook.getCallbackURL());
        
        configuration.set(Configuration.Key.GOOGLE_API_KEY, google.getApiKey());
        configuration.set(Configuration.Key.GOOGLE_SECRET, google.getSecret());
        configuration.set(Configuration.Key.GOOGLE_CALLBACK_URL, google.getCallbackURL());
        
        configuration.set(Configuration.Key.TWITTER_API_KEY, twitter.getApiKey());
        configuration.set(Configuration.Key.TWITTER_SECRET, twitter.getSecret());
        configuration.set(Configuration.Key.TWITTER_CALLBACK_URL, twitter.getCallbackURL());
        
        ApplicationContext.getInstance().saveConfiguration(configuration);
        
        return renderStatus(HttpURLConnection.HTTP_OK);
    }

    public Render getUserSettings() {
        // TODO Implement this service
        return renderSuccess();
    }

    public Render setUserSettings() {
        // TODO Implement this service
        return renderSuccess();
    }
    
}
