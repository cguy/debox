package org.debox.photo.model.user;

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

import org.apache.shiro.authz.permission.WildcardPermission;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class DeboxPermission extends WildcardPermission {
    
    public static String DOMAIN_ALBUM = "album";
    public static String DOMAIN_PHOTO = "photo";
    public static String DOMAIN_VIDEO = "video";
    public static String DOMAIN_COMMENT = "comment";
    
    public static String ACTION_ALL = "*";
    public static String ACTION_READ = "read";
    public static String ACTION_WRITE = "write";
    public static String ACTION_SEE_COMMENTS = "seeComments";
    public static String ACTION_DOWNLOAD = "download";
    public static String ACTION_DELETE = "delete";

    protected String domain;
    protected String action;
    protected String instance;
    
    public DeboxPermission(String domain, String action, String instance) {
        super(domain + PART_DIVIDER_TOKEN + action + PART_DIVIDER_TOKEN + instance);
        this.domain = domain;
        this.action = action;
        this.instance = instance;
    }

    public String getDomain() {
        return domain;
    }

    public String getAction() {
        return action;
    }

    public String getInstance() {
        return instance;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }
    
    @Override
    public String toString() {
        return getDomain() + PART_DIVIDER_TOKEN + getAction() + PART_DIVIDER_TOKEN + getInstance();
    }

}
