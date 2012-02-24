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
package org.debox.photo.server.renderer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.debox.photo.util.FileUtils;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.mapping.Mapping;
import org.debux.webmotion.server.render.Render;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class ZipDownloadRenderer extends Render {

    private static final Logger logger = LoggerFactory.getLogger(FileDownloadRenderer.class);
    protected Path directory;
    protected String filename;
    protected Map<String, String> names;

    public ZipDownloadRenderer(String directory, String filename) {
        this.directory = Paths.get(directory);
        this.filename = filename;
    }

    public ZipDownloadRenderer(String directory, String filename, Map<String, String> names) {
        this.directory = Paths.get(directory);
        this.filename = filename;
        this.names = names;
    }

    @Override
    public void create(Mapping mapping, Call call) throws IOException, ServletException {
        HttpContext context = call.getContext();
        HttpServletResponse response = context.getResponse();

        byte[] bytes = FileUtils.zipDirectoryContent(directory, names);

        response.setContentType("application/zip");
        response.setContentLength((int) bytes.length);
        response.setHeader("Content-Transfer-Encoding", "application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".zip\"");

        try (ServletOutputStream out = response.getOutputStream()) {
            out.write(bytes);
            out.flush();
        }
    }

}
