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
package org.debox.photo.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.mapping.Mapping;
import org.debux.webmotion.server.render.Render;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class FileDownloadRenderer extends Render {

    private static final Logger logger = LoggerFactory.getLogger(FileDownloadRenderer.class);
    
    protected Path file;
    protected String mimeType;
    protected String filename;

    public FileDownloadRenderer(Path file, String filename, String mimeType) {
        this.file = file;
        this.mimeType = mimeType;
        this.filename = filename;
    }

    @Override
    public void create(Mapping mapping, Call call) throws IOException, ServletException {
        HttpContext context = call.getContext();
        HttpServletResponse response = context.getResponse();

        response.setContentType("application/force-download");
        response.setContentLength((int) Files.size(file));
        response.setHeader("Content-Transfer-Encoding", mimeType);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        try (
                ServletOutputStream out = response.getOutputStream();
                InputStream inputStream = new FileInputStream(file.toFile())) {
            
            byte[] bytes = IOUtils.toByteArray(inputStream, Files.size(file));
            out.write(bytes);
        }
    }
}
