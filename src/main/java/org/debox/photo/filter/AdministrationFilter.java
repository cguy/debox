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
package org.debox.photo.filter;

import java.net.HttpURLConnection;
import org.apache.shiro.SecurityUtils;
import org.debox.photo.util.SessionUtils;
import org.debux.webmotion.server.WebMotionFilter;
import org.debux.webmotion.server.render.Render;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class AdministrationFilter extends WebMotionFilter {

    public Render checkUserSession() {
        if (SessionUtils.isLogged(SecurityUtils.getSubject())) {
            doProcess();
            return null;
        }
        return renderError(HttpURLConnection.HTTP_FORBIDDEN, "You must be logged-in."); 
    }
}
