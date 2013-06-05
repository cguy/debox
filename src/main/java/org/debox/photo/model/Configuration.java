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

import java.util.HashMap;
import java.util.Map;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class Configuration {
    
    public static enum Key {
        WORKING_DIRECTORY("working.directory"),
        TITLE,
        ALBUMS_DIRECTORY("albums.directory"),
        THUMBNAILS_DIRECTORY("thumbnails.directory"),
        THIRDPARTY_ACTIVATION("thirdparty.activation"),
        FACEBOOK_API_KEY("facebook.apiKey"),
        FACEBOOK_SECRET("facebook.secret"),
        FACEBOOK_CALLBACK_URL("facebook.callback.url");
        
        protected String id;
        
        Key () {
            this.id = this.name().toLowerCase();
        }
        
        Key(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
        
        public static Key getById(String id) {
            for (Key key : Key.values()) {
                if (id.equals(key.getId())) {
                    return key;
                }
            }
            return null;
        }
        
    }

    protected Map<String, String> data = new HashMap<>();
    
    public Map<String, String> get() {
        return data;
    }
    
    public String get(Key key) {
        return data.get(key.getId());
    }
    
    public void set(Key key, String value) {
        data.put(key.getId(), value);
    }
    
    public void remove(Key key) {
        data.remove(key.getId());
    }
    
}
