package org.debox.photo.util.i18n;

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

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class I18nUtils {
    
    private static final Logger log = LoggerFactory.getLogger(I18nUtils.class);
    
    protected static Map<String, Properties> i18n = new HashMap<>();
    
    public static String get(String key) {
        return getProperty(key, Locale.getDefault().getLanguage());
    }
    
    public static String getProperty(String key, String lang) {
        Properties bundle = getBundle(lang);
        return bundle.getProperty(key);
    }

    /**
     * TODO use real bundle system
     */
    public static Properties getBundle(String lang) {
        if (i18n.get(lang) == null) {
            Properties properties = new Properties();
            try {
                properties.load(CustomMessageResolver.class.getClassLoader().getResourceAsStream("/org/debox/photo/i18n/" + lang + ".properties"));
                
            } catch (IOException ex) {
                log.error("Unable to load bundle");
                throw new RuntimeException("Unable to load bundle", ex);
            }

            Map<String, Object> result = new HashMap<>(properties.size());
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();
                result.put("i18n." + key, value);
            }
            i18n.put(lang, properties);
        }
        return i18n.get(lang);
    }
    
}
