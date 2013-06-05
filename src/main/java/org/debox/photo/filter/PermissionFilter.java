package org.debox.photo.filter;

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

import java.net.HttpURLConnection;
import org.apache.shiro.SecurityUtils;
import org.debox.photo.model.user.DeboxPermission;
import org.debux.webmotion.server.WebMotionFilter;
import org.debux.webmotion.server.render.Render;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class PermissionFilter extends WebMotionFilter {
    
    public Render checkAlbumPermission(String permission, String albumId) {
        if (!SecurityUtils.getSubject().isPermitted(new DeboxPermission("album", permission, albumId))) {
            return renderStatus(HttpURLConnection.HTTP_FORBIDDEN);
        }
        doProcess();
        return null;
    }
    
}
