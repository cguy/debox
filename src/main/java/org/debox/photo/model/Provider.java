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
package org.debox.photo.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class Provider implements Serializable {
    
    protected String id;
    protected String name;
    protected String url;
    protected boolean enabled;
    
    public Provider() {
        // Default constructor
    }
    
    public Provider(String id, String name, String url) {
        this(id, name, url, false);
    }
    
    public Provider(String id, String name, String url, boolean enabled) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.enabled = enabled;
    }
    
    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof Provider)) {
            return false;
        }
        return Objects.equals(this.id, ((Provider) object).getId());
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
}
