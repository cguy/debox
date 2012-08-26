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
package org.debox.imaging.thumbnailator;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.Thumbnails.Builder;
import net.coobird.thumbnailator.geometry.Positions;
import org.debox.imaging.ImageHandler;
import org.debox.photo.model.ThumbnailSize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class ThumbnailatorImageHandler implements ImageHandler {

    private static final Logger log = LoggerFactory.getLogger(ThumbnailatorImageHandler.class);

    @Override
    public void thumbnail(Path source, Path target, ThumbnailSize size) {
        Builder<File> builder = Thumbnails.of(source.toFile());
        
        Integer rotation = getOrientation(source);
        if (rotation != null) {
            builder.rotate(rotation);
        }
        
        if (size.isCropped()) {
            builder = builder.crop(Positions.CENTER);
        }
        builder = builder.size(size.getWidth(), size.getHeight());

        try {
            builder.toFile(target.toString());
        } catch (IOException ex) {
            log.error("Unable to write file {}, reason: {}", target.toString(), ex.getMessage());
        }
    }

    protected Integer getOrientation(Path imagePath) {
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(imagePath.toFile());
            ExifIFD0Directory directory = metadata.getDirectory(ExifIFD0Directory.class);
            if (directory == null) {
                return null;
            }

            Integer rawValue = directory.getInteger(ExifIFD0Directory.TAG_ORIENTATION);
            Integer result = null;
            switch (rawValue) {
                case 3:
                    result = 180;
                    break;
                case 6:
                    result = 90;
                    break;
                case 8:
                    result = 270;
                    break;
            }
            return result;

        } catch (ImageProcessingException | IOException ex) {
            log.error("Unable to read orientation from file " + imagePath.toString(), ex);
        }
        return null;
    }

    @Override
    public void thumbnail(Path source, Path target, ThumbnailSize size, boolean async) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
