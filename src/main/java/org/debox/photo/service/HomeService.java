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
package org.debox.photo.service;

import org.debox.photo.server.renderer.JacksonRenderJsonImpl;
import org.debux.webmotion.server.render.Render;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class HomeService extends DeboxService {

    private static final Logger logger = LoggerFactory.getLogger(HomeService.class);

//    public Render renderTemplates() {
//        if (!ApplicationContext.isConfigured()) {
//            return renderJSON("templates", TemplateUtils.getTemplates(getServletContext()));
//        }
//        Map<String, Object> headerData = getHeaderData();
//
//        Map<String, Object> result = new HashMap<>(2);
//        result.put("config", headerData);
//        result.put("templates", TemplateUtils.getTemplates(getServletContext()));
//        List<Provider> providers = ServiceUtil.getAuthenticationUrls();
//        result.put("hasProviders", providers != null);
//        result.put("providers", providers);
//
//        return renderJSON(result);
//    }

    public Render getHome() throws Exception {
        JacksonRenderJsonImpl json = (JacksonRenderJsonImpl) new AlbumService().getAlbums(null, null, null);
        return render("home", "albums", json.getModel().get("albums"));
    }

}
