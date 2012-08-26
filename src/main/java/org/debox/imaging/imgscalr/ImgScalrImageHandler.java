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
package org.debox.imaging.imgscalr;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import org.debox.imaging.ImageHandler;
import org.debox.photo.model.ThumbnailSize;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.imgscalr.Scalr.Mode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class ImgScalrImageHandler implements ImageHandler {

    private static final Logger log = LoggerFactory.getLogger(ImgScalrImageHandler.class);

    @Override
    public void thumbnail(Path source, Path target, ThumbnailSize size) {
        try {
            BufferedImage image = ImageIO.read(source.toFile());

            BufferedImage resized = resize(image, size);
            image.flush();

            Scalr.Rotation orientation = getOrientation(source);
            if (orientation != null) {
                BufferedImage tmp = Scalr.rotate(resized, orientation);
                resized.flush();
                resized = tmp;
            }

            BufferedImage result = resized;
            if (size.isCropped()) {
                BufferedImage cropped = cropSquare(resized);
                resized.flush();

                result = cropped;
            }
            ImageIO.write(result, "jpeg", target.toFile());
            result.flush();

        } catch (IOException ex) {
            log.error("Unable to create thumbnail for source: " + source.toString() + ", reason:", ex);
        }
    }

    protected Scalr.Rotation getOrientation(Path imagePath) {
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(imagePath.toFile());
            ExifIFD0Directory directory = metadata.getDirectory(ExifIFD0Directory.class);
            if (directory == null) {
                return null;
            }

            Integer rawValue = directory.getInteger(ExifIFD0Directory.TAG_ORIENTATION);
            Scalr.Rotation result = null;
            switch (rawValue) {
                case 1:
                    break;
                case 2:
                    result = Scalr.Rotation.FLIP_HORZ;
                    break;
                case 3:
                    result = Scalr.Rotation.CW_180;
                    break;
                case 4:
                    result = Scalr.Rotation.FLIP_VERT;
                    break;
                case 5:
                    break;
                case 6:
                    result = Scalr.Rotation.CW_90;
                    break;
                case 7:
                    break;
                case 8:
                    result = Scalr.Rotation.CW_270;
                    break;
            }
            return result;
            
        } catch (ImageProcessingException | IOException ex) {
            log.error("Unable to read orientation from file " + imagePath.toString(), ex);
        }
        return null;
    }

    protected BufferedImage resize(BufferedImage image, ThumbnailSize size) {
        int originalWidth = image.getWidth();
        int originalHeight = image.getHeight();
        float ratio = (float) originalWidth / (float) originalHeight;

        int targetWidth;
        int targetHeight;

        if (ratio >= 1.0) {
            targetWidth = (int) (size.getWidth() * ratio);
            targetHeight = size.getHeight();
        } else {
            targetWidth = size.getWidth();
            targetHeight = (int) (size.getHeight() / ratio);
        }

        BufferedImage result = Scalr.resize(image, Method.QUALITY, Mode.AUTOMATIC, targetWidth, targetHeight, Scalr.OP_ANTIALIAS);

        return result;
    }

    protected BufferedImage cropSquare(BufferedImage image) {
        int originalWidth = image.getWidth();
        int originalHeight = image.getHeight();
        float ratio = (float) originalWidth / (float) originalHeight;

        int squareSize;
        int offsetX = 0;
        int offsetY = 0;

        if (ratio >= 1.0) {
            offsetX = (originalWidth - originalHeight) / 2;
            squareSize = originalHeight;
        } else {
            offsetY = (originalHeight - originalWidth) / 2;
            squareSize = originalWidth;
        }

        BufferedImage result = Scalr.crop(image, offsetX, offsetY, squareSize, squareSize);
        return result;
    }

    @Override
    public void thumbnail(Path source, Path target, ThumbnailSize size, boolean async) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
