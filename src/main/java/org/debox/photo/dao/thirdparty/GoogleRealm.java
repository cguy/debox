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
package org.debox.photo.dao.thirdparty;

import com.restfb.DefaultFacebookClient;
import com.sun.syndication.io.FeedException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;
import java.util.logging.Level;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.JdbcUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.debox.connector.api.exception.AuthenticationProviderException;
import org.debox.model.OAuth2Token;
import org.debox.photo.dao.JdbcMysqlRealm;
import org.debox.photo.dao.UserDao;
import org.debox.photo.model.user.ThirdPartyAccount;
import org.debox.photo.model.user.User;
import org.debox.photo.thirdparty.ServiceUtil;
import org.debox.util.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GoogleRealm extends JdbcMysqlRealm {
    
    private static final Logger logger = LoggerFactory.getLogger(FacebookRealm.class);
    
    @Override
    public boolean supports(AuthenticationToken token) {
        if (token instanceof ThirdPartyTokenWrapper) {
            return true;
        }
        return false;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

        //null usernames are invalid
        if (principals == null) {
            throw new AuthorizationException("PrincipalCollection method argument cannot be null.");
        }

        User user = (User) getAvailablePrincipal(principals);
        Connection conn = null;
        Set<String> roleNames = null;
        Set<String> permissions = null;
        try {
            conn = dataSource.getConnection();

            // Retrieve roles and permissions from database
            roleNames = getRoleNamesForUser(conn, user.getId());
            if (permissionsLookupEnabled) {
                permissions = getPermissions(conn, user.getId(), roleNames);
            }

        } catch (SQLException e) {
            final String message = "There was a SQL error while authorizing user [" + user.getId() + "]";
            if (logger.isErrorEnabled()) {
                logger.error(message, e);
            }

            // Rethrow any SQL errors as an authorization exception
            throw new AuthorizationException(message, e);
        } finally {
            JdbcUtils.closeConnection(conn);
        }

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo(roleNames);
        info.setStringPermissions(permissions);
        return info;
    }
    
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        try {
            ThirdPartyTokenWrapper tokenWrapper = (ThirdPartyTokenWrapper) token;

            OAuth2Token oauthToken = ServiceUtil.getAuthenticationToken(tokenWrapper.getCode().getValue());
            String response = HttpUtils.getResponse("https://www.googleapis.com/oauth2/v2/userinfo?access_token=" + oauthToken.getAccessToken());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(response);

            String userId = node.get("email").asText();
            ThirdPartyAccount account = userDao.getUser("google", userId);
            if (account == null) {
                account = new ThirdPartyAccount(ServiceUtil.getProvider("google"), userId, oauthToken.getAccessToken());
            } else {
                account.setToken(oauthToken.getAccessToken());
            }
            userDao.save(account);
            account.setUsername(node.get("name").asText());
            account.setAccountUrl(node.get("link").asText());

            return new SimpleAuthenticationInfo(account, tokenWrapper.getCode(), this.getName());

        } catch (IOException | IllegalArgumentException | FeedException | AuthenticationProviderException ex) {
            logger.error("Unable to get auth token, reason:", ex);
        } catch (SQLException ex) {
            logger.error("Unable to access database, reason:", ex);
        }
        return null;
    }

}
