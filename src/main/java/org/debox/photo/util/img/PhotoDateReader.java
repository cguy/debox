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
package org.debox.photo.util.img;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.Date;
import java.util.concurrent.RecursiveTask;
import org.debox.photo.model.Photo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class PhotoDateReader extends RecursiveTask<Photo> {
    
    private static final Logger logger = LoggerFactory.getLogger(PhotoDateReader.class);

    protected String basePath;
    protected Photo photo;
    
    public PhotoDateReader(String basePath, Photo photo) {
        this.basePath = basePath;
        this.photo = photo;
    }

    @Override
    protected Photo compute() {
        String path = basePath + photo.getRelativePath() + File.separatorChar + photo.getName();
        logger.debug("Get shooting date from photo: {}", path);
        Date date = ImageUtils.getShootingDate(Paths.get(path));
        if (date == null) {
            Path p = Paths.get(path);
            FileTime lastModifiedTime;
            try {
                lastModifiedTime = Files.getLastModifiedTime(p);
                date = new Date(lastModifiedTime.toMillis());
                
            } catch (IOException eee) {
                logger.error("Error while getting the last modification date of " + path, eee);
                date = new Date();
            }
        }
        photo.setDate(date);
        return photo;
    }

}
