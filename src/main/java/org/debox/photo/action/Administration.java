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
import org.debox.photo.job.StructureInitializerJob;
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
//        copyDirectories(sourceDirectory, new File(ApplicationContext.getTarget(), album.getId()));
        return renderJSON(album);
    }
    
    public Render editConfiguration(String sourceDirectory, String targetDirectory) {
        File source = new File(sourceDirectory);
        File target = new File(targetDirectory);
        boolean error = false;
        if (!source.isDirectory() || !source.exists()) {
            getContext().addErrorMessage("source", "source.error");
            error = true;
        }
        if (!target.isDirectory() || !target.exists()) {
            getContext().addErrorMessage("target", "target.error");
            error = true;
        }
        if (source.getAbsolutePath().equals(target.getAbsolutePath())) {
            getContext().addErrorMessage("path", "paths.equals");
            error = true;
        }
        
        if (error) {
            return renderStatus(500);
        }
        
        ApplicationContext.setSource(source);
        ApplicationContext.setTarget(target);
        getContext().addInfoMessage("success", "configuration.edit.success");
        
        Runnable runnable = new StructureInitializerJob(source, target);
        new Thread(runnable).start();
        
        return renderJSON("sourceDirectory", sourceDirectory, "targetDirectory", targetDirectory);
    }
    
    public Render deleteAlbum() {
        return null;
    }

    public Render getData() {
        String sourceDirectory = null;
        if (ApplicationContext.getSource() != null) {
            sourceDirectory = ApplicationContext.getSource().getAbsolutePath();
        }
        
        String targetDirectory = null;
        if (ApplicationContext.getTarget() != null) {
            targetDirectory = ApplicationContext.getTarget().getAbsolutePath();
        }
        return renderJSON(
            "sourceDirectory", sourceDirectory,
            "targetDirectory", targetDirectory,
            "albums", ApplicationContext.getAlbums()
        );
    }
}
