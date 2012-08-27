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
package org.debox.photo.thirdparty;

import com.sun.syndication.io.FeedException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.debox.connector.api.exception.AuthenticationProviderException;
import org.debox.model.OAuth2Token;
import org.debox.photo.model.Configuration;
import org.debox.photo.model.Provider;
import org.debox.photo.server.ApplicationContext;
import org.debox.util.OAuth2Utils;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FacebookApi;
import org.scribe.oauth.OAuthService;
import org.scribe.utils.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class ServiceUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(ServiceUtil.class);

    public static String getAuthenticationUrl() {
        ApplicationContext context = ApplicationContext.getInstance();
        Configuration configuration = context.getConfiguration();
        String apiKey = configuration.get(Configuration.Key.GOOGLE_API_KEY);
        String callback = configuration.get(Configuration.Key.GOOGLE_CALLBACK_URL);
        String authenticationUrl = "https://accounts.google.com/o/oauth2/auth"
            + "?client_id=%s"
            + "&redirect_uri=%s"
            + "&scope=%s"
            + "&response_type=code";
        
        Preconditions.checkEmptyString(apiKey, "You must provide an api key");
        Preconditions.checkEmptyString(callback, "You must provide a callback url");
        
        return String.format(authenticationUrl, apiKey, callback, "https%3A%2F%2Fwww.google.com%2Fm8%2Ffeeds%2F+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email");
    }

    public static OAuth2Token getAuthenticationToken(String code) throws IOException, IllegalArgumentException, FeedException, AuthenticationProviderException {
        ApplicationContext context = ApplicationContext.getInstance();
        Configuration configuration = context.getConfiguration();
        String apiKey = configuration.get(Configuration.Key.GOOGLE_API_KEY);
        String callback = configuration.get(Configuration.Key.GOOGLE_CALLBACK_URL);
        String secret = configuration.get(Configuration.Key.GOOGLE_SECRET);
        return OAuth2Utils.getAccessTokenFromCode("https://accounts.google.com/o/oauth2/token", apiKey, secret, callback, code);
    }

    public static OAuthService getFacebookService() {
        ApplicationContext context = ApplicationContext.getInstance();
        Configuration configuration = context.getConfiguration();
        String apiKey = configuration.get(Configuration.Key.FACEBOOK_API_KEY);
        String secret = configuration.get(Configuration.Key.FACEBOOK_SECRET);
        String callback = configuration.get(Configuration.Key.FACEBOOK_CALLBACK_URL);
        
        return new ServiceBuilder()
                .provider(FacebookApi.class)
                .apiKey(apiKey)
                .apiSecret(secret)
                .callback(callback)
                .build();
    }

    public static Provider getProvider(String id) {
        Provider result = new Provider();
        result.setId(id);

        switch (id) {
            case "facebook":
                result.setName("Facebook");
                try {
                    result.setUrl(getFacebookService().getAuthorizationUrl(null));
                    result.setEnabled(true);
                } catch (IllegalArgumentException ex) {
                    logger.warn("Facebook is unconfigured, cause: ", ex);
                }
                break;
            case "twitter":
                result.setName("Twitter");
                break;
            case "google":
                result.setName("Google");
                try {
                   result.setUrl(getAuthenticationUrl());
                    result.setEnabled(true);
                } catch (IllegalArgumentException ex) {
                    logger.warn("Google is unconfigured, cause: ", ex);
                }
                break;
            default:
                return null;
        }

        return result;
    }

    public static List<Provider> getAuthenticationUrls() {
        ApplicationContext context = ApplicationContext.getInstance();
        Configuration configuration = context.getConfiguration();
        String activated = configuration.get(Configuration.Key.THIRDPARTY_ACTIVATION);
        
        if (!Boolean.parseBoolean(activated)) {
            return null;
        }
        
        List<Provider> result = new ArrayList<>();
        result.add(getProvider("facebook"));
        result.add(getProvider("google"));
        result.add(getProvider("twitter"));
        return result;
    }
}
