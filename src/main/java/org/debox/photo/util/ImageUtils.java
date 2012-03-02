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

import java.io.File;
import org.debox.photo.model.Configuration;
import org.debox.photo.model.Photo;
import org.debox.photo.model.ThumbnailSize;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class ImageUtils {
    
    public static final String JPEG_MIME_TYPE = "image/jpeg";
    public static final String JPEG_EXT = ".jpg";
    
    public static String getTargetPath(Configuration configuration, Photo photo, ThumbnailSize size) {
        return configuration.get(Configuration.Key.TARGET_PATH) + photo.getRelativePath() + File.separatorChar + size.getPrefix() + photo.getName() + JPEG_EXT;
    }
    
}
