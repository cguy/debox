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

import java.util.ArrayList;
import java.util.List;
import org.debox.photo.model.Configuration;
import org.debox.photo.model.Provider;
import org.debox.photo.server.ApplicationContext;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FacebookApi;
import org.scribe.oauth.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class ServiceUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(ServiceUtil.class);

    public static OAuthService getFacebookService() {
        ApplicationContext context = ApplicationContext.getInstance();
        Configuration configuration = context.getOverallConfiguration();
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
                    logger.warn("Facebook is unconfigured, cause: {}", ex.getMessage());
                }
                break;
            default:
                return null;
        }

        return result;
    }

    public static List<Provider> getAuthenticationUrls() {
        ApplicationContext context = ApplicationContext.getInstance();
        Configuration configuration = context.getOverallConfiguration();
        String activated = configuration.get(Configuration.Key.THIRDPARTY_ACTIVATION);
        
        List<Provider> result = new ArrayList<>();
        if (!Boolean.parseBoolean(activated)) {
            return result;
        }
        
        result.add(getProvider("facebook"));
        return result;
    }
}
