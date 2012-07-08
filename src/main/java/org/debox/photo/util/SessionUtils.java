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
package org.debox.photo.util;

import org.apache.shiro.subject.Subject;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class SessionUtils {
    
    public static final String ADMIN_ROLE = "administrator";

    public static boolean isLogged(Subject subject) {
        if (subject == null) {
            return false;
        }
        return subject.isAuthenticated() || subject.isRemembered();
    }

    public static boolean isAdministrator(Subject subject) {
        if (subject == null) {
            return false;
        }
        return subject.hasRole(ADMIN_ROLE);
    }
    
}
