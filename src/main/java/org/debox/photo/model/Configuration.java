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
        SOURCE_PATH,
        TARGET_PATH
    }

    protected Map<String, String> data = new HashMap<>();
    
    public Map<String, String> get() {
        return data;
    }
    
    public String get(Key key) {
        return data.get(key.name().toLowerCase());
    }
    
    public void set(Key key, String value) {
        data.put(key.name().toLowerCase(), value);
    }
    
}
