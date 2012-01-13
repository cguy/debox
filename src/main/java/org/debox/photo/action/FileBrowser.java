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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.render.Render;
import org.imgscalr.Scalr;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class FileBrowser extends WebMotionController {

    protected static final File source = new File("/home/cguy/photos");
    protected static final File dest = new File("/home/cguy/photos_thumbnails");

    public Render init() throws IOException {
        copyDirectories(source, dest);
        return renderStatus(200);
    }
    
    protected void copyDirectories(File source, File destParent) throws IOException {
        if (source.isDirectory()) {
            File target = new File(destParent, source.getName());
            if (!target.exists()) {
                target.mkdirs();
            }
            for (File file : source.listFiles()) {
                copyDirectories(new File(source, file.getName()), target);
            }
        } else {
            System.out.println(">>>" + source.getAbsolutePath());
            BufferedImage image = ImageIO.read(source);
            BufferedImage thumbnail = Scalr.resize(image, 150);
            File imageFile = new File(destParent, source.getName());
            ImageIO.write(thumbnail, "jpg", imageFile);
        }
    }

    public Render index() throws IOException {
        return renderView("index.jsp");
    }

    public Render picture(String name) {
        return null;
    }
}
