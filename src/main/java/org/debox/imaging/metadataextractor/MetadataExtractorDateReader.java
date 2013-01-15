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
package org.debox.imaging.metadataextractor;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;
import org.debox.imaging.DateReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class MetadataExtractorDateReader implements DateReader {
    
    private static final Logger log = LoggerFactory.getLogger(MetadataExtractorDateReader.class);

    @Override
    public Date getShootingDate(Path path) {
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(path.toFile());
            ExifSubIFDDirectory directory = metadata.getDirectory(ExifSubIFDDirectory.class);
            if (directory == null) {
                return null;
            }
            Date result = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
            return result;

        } catch (ImageProcessingException | IOException ex) {
            log.error("Error while reading shooting date in exif metadata of file {}, reason: {}", path, ex.getMessage());
        }
        return null;
    }
    
}
