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
import org.debox.photo.model.Album;
import org.debox.photo.model.ApplicationContext;
import org.debox.photo.util.ImageUtils;
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.render.Render;
import org.imgscalr.Scalr;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class Administration extends WebMotionController {

    public Render createAlbum(String name, String source) throws IOException {
        Album album = new Album();
        album.setName(name);
        album.setSource(source);

        final File sourceDirectory = new File(source);
        if (!sourceDirectory.exists()) {
            return renderError(500, "Directory " + source + " doesn't exist.");
        }
        
        ApplicationContext.addAlbum(album);
        copyDirectories(sourceDirectory, new File(ApplicationContext.target, album.getId()));
        return renderJSON(album);
    }
    
    protected void copyDirectories(File source, File target) throws IOException {
        if (!target.exists()) {
            target.mkdir();
        }

        for (File file : source.listFiles()) {
            if (file.isDirectory()) {
                continue;
            }

            BufferedImage image = ImageIO.read(file);
            BufferedImage thumbnail = Scalr.resize(image, 225);
            thumbnail = ImageUtils.rotate(file, thumbnail);

            int height = thumbnail.getHeight();
            int width = thumbnail.getWidth();
            int squareSize = width;
            int x = 0;
            int y = 0;
            if (width > height) {
                squareSize = height;
                x = (width - height) / 2;
                y = 0;
            } else if (width < height) {
                squareSize = width;
                x = 0;
                y = (height - width) / 2;
            }

            thumbnail = Scalr.crop(thumbnail, x, y, squareSize, squareSize);

            File imageFile = new File(target, ImageUtils.THUMBNAIL_PREFIX + file.getName());
            ImageIO.write(thumbnail, "jpg", imageFile);

            thumbnail = Scalr.resize(image, 1600);
            thumbnail = ImageUtils.rotate(file, thumbnail);
            imageFile = new File(target, ImageUtils.LARGE_PREFIX + file.getName());
            ImageIO.write(thumbnail, "jpg", imageFile);

            image.flush();
            thumbnail.flush();
        }
    }

    public Render deleteAlbum() {
        return null;
    }

    public Render getAlbums() {
        return renderJSON(ApplicationContext.getAlbums());
    }
}
