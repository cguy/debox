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
import org.debox.photo.model.Provider;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FacebookApi;
import org.scribe.oauth.OAuthService;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class ServiceUtil {
    
    static OAuthService facebook = new ServiceBuilder()
                           .provider(FacebookApi.class)
                           .apiKey("396075103783998")
                           .apiSecret("63156c3e0ce7328d5b03ae9650528e7b")
                           .callback("http://localhost:8080/photo/facebook")
                           .build();
    
    public static OAuthService getFacebookService() {
        return facebook; 
    }
    
    public static Provider getProvider(String id) {
        Provider result = new Provider();
        result.setId(id);
        
        switch (id) {
            case "facebook":
                result.setName("Facebook");
                result.setUrl(facebook.getAuthorizationUrl(null));
                result.setEnabled(true);
                break;
            case "twitter":
                result.setName("Twitter");
                break;
            case "gplus":
                result.setName("Google +");
                break;
            default:
                return null;
        }
        
        return result;
    }
    
    public static List<Provider> getAuthenticationUrls() {
        List<Provider> result = new ArrayList<>();
        result.add(getProvider("facebook"));
        result.add(getProvider("gplus"));
        result.add(getProvider("twitter"));
        return result;
    }
    
}
