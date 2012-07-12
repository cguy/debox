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
import org.debox.photo.model.Provider;
import org.debox.util.OAuth2Utils;
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
    
    protected static final String AUTHENTICATION_URL = "https://accounts.google.com/o/oauth2/auth"
            + "?client_id=%s"
            + "&redirect_uri=%s"
            + "&scope=%s"
            + "&response_type=code";
    
    public static String getAuthenticationUrl() {
        return String.format(AUTHENTICATION_URL, "155246083973.apps.googleusercontent.com", "http://localhost:8080/photo/google", "https%3A%2F%2Fwww.google.com%2Fm8%2Ffeeds%2F+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email");
    }
    
     public static OAuth2Token getAuthenticationToken(String code) throws IOException, IllegalArgumentException, FeedException, AuthenticationProviderException {
        return OAuth2Utils.getAccessTokenFromCode("https://accounts.google.com/o/oauth2/token", "155246083973.apps.googleusercontent.com", "BQnD0XRGHzngap4vXKW3cR3L", "http://localhost:8080/photo/google", code);
    }
    
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
            case "google":
                result.setName("Google");
                result.setUrl(getAuthenticationUrl());
                result.setEnabled(true);
                break;
            default:
                return null;
        }
        
        return result;
    }
    
    public static List<Provider> getAuthenticationUrls() {
        List<Provider> result = new ArrayList<>();
        result.add(getProvider("facebook"));
        result.add(getProvider("google"));
        result.add(getProvider("twitter"));
        return result;
    }
    
}
