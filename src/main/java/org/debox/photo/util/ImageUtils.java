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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.formats.jpeg.JpegImageMetadata;
import org.apache.sanselan.formats.tiff.TiffField;
import org.apache.sanselan.formats.tiff.constants.TiffConstants;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class ImageUtils {
    
    public static final String JPEG_MIME_TYPE = "image/jpeg";
    public static final String LARGE_PREFIX = "1600_";
    public static final String THUMBNAIL_PREFIX = "th_";
    
    private static final Logger logger = LoggerFactory.getLogger(ImageUtils.class);
    
    public static BufferedImage rotate(File fileToRead) throws IOException {
        BufferedImage image = ImageIO.read(fileToRead);
        return rotate(fileToRead, image);
    }

    public static BufferedImage rotate(File fileToRead, BufferedImage imageToRotate) throws IOException {
        BufferedImage image = imageToRotate;
        try {
            JpegImageMetadata metadata = (JpegImageMetadata) Sanselan.getMetadata(fileToRead);
            TiffField field = metadata.findEXIFValue(TiffConstants.TIFF_TAG_ORIENTATION);

            int orientation = field.getIntValue();
            if (orientation == TiffConstants.ORIENTATION_VALUE_ROTATE_90_CW) {
                image = Scalr.rotate(image, Scalr.Rotation.CW_90);
            } else if (orientation == TiffConstants.ORIENTATION_VALUE_ROTATE_180) {
                image = Scalr.rotate(image, Scalr.Rotation.CW_180);
            } else if (orientation == TiffConstants.ORIENTATION_VALUE_ROTATE_270_CW) {
                image = Scalr.rotate(image, Scalr.Rotation.CW_270);
            }
        } catch (ImageReadException ex) {
            logger.error(ex.getMessage(), ex);
        }
        return image;
    }
    
    public static BufferedImage cropSquare(BufferedImage source) {
        int height = source.getHeight();
        int width = source.getWidth();
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

        BufferedImage result = Scalr.crop(source, x, y, squareSize, squareSize);
        return result;
    }
    
}
