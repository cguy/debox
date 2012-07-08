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
package org.debox.photo.server;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class SLF4JBridgeConfigurator implements ServletContextListener {
    
    private static final Logger logger = LoggerFactory.getLogger(SLF4JBridgeConfigurator.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        SLF4JBridgeHandler.install();
        logger.info("SLF4JBridgeHandler is successfully installed.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
    
}
