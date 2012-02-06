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
package org.debox.photo.job;

import java.awt.image.BufferedImage;
import java.awt.image.ImagingOpException;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import javax.imageio.ImageIO;
import org.debox.photo.util.ImageUtils;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class ImageProcessor implements Callable {

    private static final Logger logger = LoggerFactory.getLogger(ImageProcessor.class);
    protected File imageFile;
    protected String imageId;
    protected String targetPath;

    public ImageProcessor(File image, String targetPath, String imageId) {
        this.imageFile = image;
        this.targetPath = targetPath;
        this.imageId = imageId;
    }

    @Override
    public Object call() throws Exception {
        try {
            logger.info("{} image processing ...", imageFile.getName());
            BufferedImage image = ImageIO.read(imageFile);
            
            // Create reduction (1600px)
            BufferedImage reduction = Scalr.resize(image, 1600);
            reduction = ImageUtils.rotate(imageFile, reduction);
            File targetImageFile = new File(targetPath, ImageUtils.LARGE_PREFIX + imageId + ".jpg");
            ImageIO.write(reduction, "jpg", targetImageFile);
            image.flush();

            // Create cropped thumbnail from reduction
            BufferedImage thumbnail = Scalr.resize(reduction, 225);
            reduction.flush();

            BufferedImage cropped = ImageUtils.cropSquare(thumbnail);
            thumbnail.flush();

            targetImageFile = new File(targetPath, ImageUtils.THUMBNAIL_PREFIX + imageId + ".jpg");
            ImageIO.write(cropped, "jpg", targetImageFile);
            cropped.flush();

            logger.info("{} image processed", imageFile.getName());

        } catch (IOException | IllegalArgumentException | ImagingOpException ex) {
            logger.error(ex.getMessage(), ex);

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return null;
    }
}
