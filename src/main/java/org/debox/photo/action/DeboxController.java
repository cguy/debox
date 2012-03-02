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
package org.debox.photo.action;

import java.net.HttpURLConnection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.debox.photo.dao.PhotoDao;
import org.debox.photo.model.Photo;
import org.debox.photo.model.ThumbnailSize;
import org.debox.photo.server.renderer.JacksonRenderJsonImpl;
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.render.Render;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class DeboxController extends WebMotionController {

    private static final Logger logger = LoggerFactory.getLogger(DeboxController.class);
    protected PhotoDao photoDao = new PhotoDao();

    @Override
    public Render renderJSON(Object... model) {
        return new JacksonRenderJsonImpl(toMap(model));
    }

    @Override
    protected Map<String, Object> toMap(Object... model) {
        if (model.length == 1) {
            Map<String, Object> map = new LinkedHashMap<>(1);
            map.put(null, model[0]);
            return map;
        }

        return super.toMap(model);
    }

    protected void handleLastModifiedHeader(Photo photo, ThumbnailSize size) {
        try {
            long lastModified = photoDao.getGenerationTime(photo, size);
            long ifModifiedSince = getContext().getRequest().getDateHeader("If-Modified-Since");
            if (lastModified != -1) {
                if (lastModified <= ifModifiedSince) {
                    getContext().getResponse().setStatus(HttpURLConnection.HTTP_NOT_MODIFIED);
                }
                getContext().getResponse().addDateHeader("Last-Modified", lastModified);
            }

        } catch (SQLException ex) {
            logger.error("Unable to handle Last-Modified header, cause : " + ex.getMessage(), ex);
        }
    }
}
