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
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.apache.commons.lang.StringUtils;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.formats.jpeg.JpegImageMetadata;
import org.apache.sanselan.formats.tiff.TiffField;
import org.apache.sanselan.formats.tiff.constants.TiffConstants;
import org.debox.photo.model.Album;
import org.debox.photo.model.ApplicationContext;
import org.debox.photo.util.ImageUtils;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author cguy
 */
public class StructureInitializerJob implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(StructureInitializerJob.class);
    protected File source;
    protected File target;

    public StructureInitializerJob(File source, File target) {
        this.source = source;
        this.target = target;
    }

    @Override
    public void run() {
        try {
            copyDirectories(source, target);
            
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    protected BufferedImage rotate(File fileToRead) throws IOException {
        BufferedImage image = ImageIO.read(fileToRead);
        return rotate(fileToRead, image);
    }

    protected BufferedImage rotate(File fileToRead, BufferedImage imageToRotate) throws IOException {
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
            ex.printStackTrace();
        }
        return image;
    }

    protected void copyDirectories(File source, File target) throws IOException {
        if (source.isDirectory()) {
            if (!target.exists()) {
                target.mkdirs();
            }
            
            String name = StringUtils.removeStart(target.getAbsolutePath(), ApplicationContext.getTarget().getAbsolutePath() + File.separatorChar);
            if (ApplicationContext.getAlbum(name) == null) {
                Album album = new Album();
                album.setName(name);
                album.setSource(source.getAbsolutePath());
                album.setTarget(target.getAbsolutePath());
                ApplicationContext.addAlbum(album);
            }
            
            for (File file : source.listFiles()) {
                copyDirectories(new File(source, file.getName()), new File(target, file.getName()));
            }
        } else {
            logger.info(source.getAbsolutePath());
            BufferedImage image = ImageIO.read(source);
            if (image == null) {
                return;
            }
            
            BufferedImage thumbnail = Scalr.resize(image, 225);
            thumbnail = rotate(source, thumbnail);

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

            File imageFile = new File(target.getParentFile(), ImageUtils.THUMBNAIL_PREFIX + source.getName());
            ImageIO.write(thumbnail, "jpg", imageFile);

            thumbnail = Scalr.resize(image, 1600);
            thumbnail = rotate(source, thumbnail);
            imageFile = new File(target.getParentFile(), ImageUtils.LARGE_PREFIX + source.getName());
            ImageIO.write(thumbnail, "jpg", imageFile);

            image.flush();
            thumbnail.flush();
        }
    }
}
