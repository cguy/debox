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

import java.net.HttpURLConnection;
import java.sql.SQLException;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.shiro.SecurityUtils;
import org.debox.photo.dao.ConfigurationDao;
import org.debox.photo.model.Configuration;
import org.debox.photo.model.User;
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.render.Render;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class HomeController extends WebMotionController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
    protected static ConfigurationDao configurationDao = new ConfigurationDao();

    public Render index() {
        try {
            Configuration configuration = configurationDao.get();
            User user = (User) SecurityUtils.getSubject().getPrincipal();
            String username = "null";
            if (user != null) {
                username = "\"" + StringEscapeUtils.escapeHtml4(user.getUsername()) + "\"";
            }
            
            String title = configuration.get(Configuration.Key.TITLE);
            title = "\"" + StringEscapeUtils.escapeHtml4(title) + "\"";
            
            return renderView("index.jsp", "title", title, "username", username);

        } catch (SQLException ex) {
            logger.error(ex.getMessage(), ex);
            return renderError(HttpURLConnection.HTTP_INTERNAL_ERROR, "Unable to access database");
        }
    }
    
}
