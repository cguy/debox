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

import java.awt.image.ImagingOpException;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import org.debox.photo.util.ImageUtils;
import org.im4java.core.*;
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
            ImageCommand cmd = new ConvertCmd(true);

            IMOperation operation = new IMOperation();
            operation.addImage(imageFile.getAbsolutePath());
            operation.thumbnail(1600, 1000);
            operation.addImage(targetPath + File.separatorChar + ImageUtils.LARGE_PREFIX + imageId + ".jpg");
            cmd.run(operation);

            String thumbnailPath = targetPath + File.separatorChar + ImageUtils.THUMBNAIL_PREFIX + imageId + ".jpg";
            int squareSize = 200;

            operation = new IMOperation();
            operation.addImage(imageFile.getAbsolutePath());
            operation.thumbnail(squareSize, squareSize, '^');
            operation.addImage(thumbnailPath);
            cmd.run(operation);

            Info info = new Info(thumbnailPath, true);
            int width = info.getImageWidth();
            int height = info.getImageHeight();
            int x = 0;
            int y = 0;
            if (width > height) {
                x = (width - height) / 2;
                y = 0;
            } else if (width < height) {
                x = 0;
                y = (height - width) / 2;
            }

            operation = new IMOperation();
            operation.addImage(targetPath + File.separatorChar + ImageUtils.THUMBNAIL_PREFIX + imageId + ".jpg");
            operation.crop(squareSize, squareSize, x, y);
            operation.addImage(targetPath + File.separatorChar + ImageUtils.THUMBNAIL_PREFIX + imageId + ".jpg");
            
            cmd = new MogrifyCmd(true);
            cmd.run(operation);

            logger.info("{} image processed", imageFile.getName());

        } catch (IOException | IllegalArgumentException | ImagingOpException ex) {
            logger.error(ex.getMessage(), ex);

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return null;
    }
}
