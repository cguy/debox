package org.debox.photo.filter;

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

import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.debox.photo.server.ApplicationContext;
import org.debox.photo.util.DatabaseUtils;
import org.debux.webmotion.server.WebMotionFilter;
import org.debux.webmotion.server.mapping.Mapping;
import org.debux.webmotion.server.mapping.Properties;
import org.debux.webmotion.server.render.Render;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class ConfigurationFilter extends WebMotionFilter {

    private static final Logger log = LoggerFactory.getLogger(ConfigurationFilter.class);
    private static final Object lock = new Object();
    
    protected static final String[] CONFIGURATION_PATHS_ARRAYS = {"/install", "/tpl", "/datasource", "/working-directory"};
    protected static final List<String> CONFIGURATION_PATHS = Arrays.asList(CONFIGURATION_PATHS_ARRAYS);
    
    protected static Properties properties = null;
    protected static boolean isReady = false;

    public Render doFilter(HttpServletRequest request) {
        if (properties == null) {
            synchronized (lock) {
                final Mapping mapping = getContext().getServerContext().getMapping();
                // Load properties
                properties = mapping.getProperties();
                DatabaseUtils.setDataSourceConfiguration(properties);

                // Test database configuration
                if (DatabaseUtils.hasConfiguration() && !StringUtils.isEmpty(properties.getString("debox.working.directory"))) {
                    ApplicationContext.setConfigured(true);
                }
            }
        }
        if (ApplicationContext.isConfigured() || CONFIGURATION_PATHS.contains(getContext().getUrl())) {
            doProcess();
            return null;
        }
        return renderRedirect("/install");
    }
    
}
