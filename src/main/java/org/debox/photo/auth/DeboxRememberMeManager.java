package org.debox.photo.auth;

/*
 * #%L
 * debox-photos
 * %%
 * Copyright (C) 2012 - 2013 Debox
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

import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.subject.WebSubjectContext;
import org.apache.shiro.web.util.WebUtils;
import org.debox.photo.dao.UserDao;
import org.debox.photo.model.user.AnonymousUser;
import org.debox.photo.model.user.User;
import org.debox.photo.util.PropertiesUtils;
import org.debux.webmotion.server.call.CookieManager;
import org.debux.webmotion.server.call.CookieManager.CookieEntity;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.call.ServerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class DeboxRememberMeManager extends CookieRememberMeManager implements RememberMeManager {
    
    private static final Logger log = LoggerFactory.getLogger(DeboxRememberMeManager.class);
    
    protected static final String AUTHENTICATION_COOKIE_NAME = "auth";
    
    protected UserDao userDao = new UserDao();
    
    protected ServerContext context = new ServerContext();
    
    public DeboxRememberMeManager() throws Exception {
        context.setSecret(PropertiesUtils.getSecretKey());
    }

    @Override
    public PrincipalCollection getRememberedPrincipals(SubjectContext subjectContext) {
        if (!WebUtils.isHttp(subjectContext)) {
            if (log.isDebugEnabled()) {
                String msg = "SubjectContext argument is not an HTTP-aware instance.  This is required to obtain a " +
                        "servlet request and response in order to retrieve the rememberMe cookie. Returning " +
                        "immediately and ignoring rememberMe operation.";
                log.debug(msg);
            }
            return null;
        }
        WebSubjectContext wsc = (WebSubjectContext) subjectContext;
        HttpServletRequest request = WebUtils.getHttpRequest(wsc);
        
        String servletPath = request.getServletPath();
        if (servletPath.startsWith("/static/")) {
            return null;
        }
        
        PrincipalCollection principalCollection = null;
        try {
            principalCollection = getPrincipalFromAccessToken(subjectContext);
            if (principalCollection == null) {
                principalCollection = getPrincipalFromCookie(subjectContext);
            }
            
        } finally {
            if (principalCollection == null) {
                HttpServletResponse response = WebUtils.getHttpResponse(wsc);
                this.removeAuthenticationCookie(request, response);
            }
        }
        
        return principalCollection;
    }
    
    protected PrincipalCollection getPrincipalFromCookie(SubjectContext subjectContext) {
        WebSubjectContext wsc = (WebSubjectContext) subjectContext;
        HttpServletRequest request = WebUtils.getHttpRequest(wsc);
        HttpServletResponse response = WebUtils.getHttpResponse(wsc);
        
        CookieManager cookieManager = new CookieManager(new HttpContext(context, request, response), "test", true, false);
        CookieEntity cookie = cookieManager.get(AUTHENTICATION_COOKIE_NAME);
        
        String userId = cookie.getValue();
        PrincipalCollection principalCollection = null;
        try {
            User user = userDao.getUser(userId);
            if (user != null) {
                principalCollection = new SimplePrincipalCollection(user, "deboxRealm");
            } else {
                log.info("User not found with id: {}", userId);
            }
            
        } catch (SQLException ex) {
            log.error("Unable to get user from userId: " + userId, ex);
        }

        return principalCollection;
    }
    
    protected PrincipalCollection getPrincipalFromAccessToken(SubjectContext subjectContext) {
        WebSubjectContext wsc = (WebSubjectContext) subjectContext;
        HttpServletRequest request = WebUtils.getHttpRequest(wsc);
        
        String token = request.getParameter("token");
        if (token == null) {
            return null;
        }
        
        PrincipalCollection principalCollection = null;
        try {
            AnonymousUser user = userDao.getAnonymousUser(token);
            if (user != null) {
                principalCollection = new SimplePrincipalCollection(user, "deboxRealm");
            }
            
        } catch (SQLException ex) {
            log.error("Unable to get user from token: " + token, ex);
        }

        return principalCollection;
    }

    @Override
    public void forgetIdentity(SubjectContext subjectContext) {
        // Nothing to do
    }

    @Override
    public void onSuccessfulLogin(Subject subject, AuthenticationToken token, AuthenticationInfo info) {
        if (!WebUtils.isHttp(subject)) {
            if (log.isDebugEnabled()) {
                String msg = "Subject argument is not an HTTP-aware instance.  This is required to obtain a servlet " +
                        "request and response in order to set the rememberMe cookie. Returning immediately and " +
                        "ignoring rememberMe operation.";
                log.debug(msg);
            }
            return;
        }

        HttpServletRequest request = WebUtils.getHttpRequest(subject);
        HttpServletResponse response = WebUtils.getHttpResponse(subject);

        User user = (User) subject.getPrincipal();
        String userId = user.getId();
        
        CookieManager cookieManager = new CookieManager(new HttpContext(context, request, response), "test", true, false);
        CookieEntity cookie = cookieManager.create(AUTHENTICATION_COOKIE_NAME, userId);
        cookie.setMaxAge(10000000);
        cookieManager.add(cookie);
    }

    @Override
    public void onFailedLogin(Subject subject, AuthenticationToken token, AuthenticationException ae) {
        // Nothing to do
    }

    @Override
    public void onLogout(Subject subject) {
        HttpServletRequest request = WebUtils.getHttpRequest(subject);
        HttpServletResponse response = WebUtils.getHttpResponse(subject);
        removeAuthenticationCookie(request, response);
    }
    
    protected void removeAuthenticationCookie(HttpServletRequest request, HttpServletResponse response) {
        CookieManager cookieManager = new CookieManager(new HttpContext(context, request, response), "test", true, false);
        cookieManager.remove(AUTHENTICATION_COOKIE_NAME);
    }
    
}
