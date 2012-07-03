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
import java.nio.file.attribute.FileTime;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.debox.photo.job.im4java.StringOutputConsumer;
import org.debox.photo.model.Photo;
import org.debox.photo.model.ThumbnailSize;
import org.im4java.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class ImageUtils {

    private static final Logger logger = LoggerFactory.getLogger(ImageUtils.class);
    public static final String JPEG_MIME_TYPE = "image/jpeg";

    public static String getTargetPath(String targetDirectory, Photo photo, ThumbnailSize size) {
        return targetDirectory + photo.getRelativePath() + File.separatorChar + size.getPrefix() + photo.getName();
    }

    public static Date getShootingDate(Path path) {
        Date date = null;
        try {
            IMOperation op = new IMOperation();
            op.format("%[exif:DateTimeOriginal]");
            op.addImage(path.toString());

            ImageCommand cmd = new IdentifyCmd();
            StringOutputConsumer output = new StringOutputConsumer();
            cmd.setOutputConsumer(output);
            cmd.run(op);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd hh:mm:ss");
            String strDate = output.getOutput();
            date = dateFormat.parse(strDate);

        } catch (ParseException | IOException | InterruptedException | IM4JavaException ex) {
            logger.warn("Unable to get DateTime property for path \"" + path.toString() + "\", reason: " + ex.getMessage());
            try {
                FileTime fileTime = Files.getLastModifiedTime(path);
                date = new Date(fileTime.toMillis());
            } catch (IOException ioe) {
                logger.warn("Unable to get last modified time property for path \"" + path.toString() + "\", reason: " + ioe.getMessage());
            }
        }
        return date;
    }

    public static String getOrientation(String path) throws IOException, IM4JavaException, InterruptedException {
        IMOperation op = new IMOperation();
        op.format("%[exif:Orientation]");
        op.addImage(path);

        ImageCommand cmd = new IdentifyCmd();
        StringOutputConsumer output = new StringOutputConsumer();
        cmd.setOutputConsumer(output);
        cmd.run(op);

        String result = output.getOutput();
        return result;
    }

    public static int[] getSize(String path) throws IOException, InterruptedException, IM4JavaException {
        IMOperation op = new IMOperation();
        op.format("%W %H");
        op.addImage(path);

        ImageCommand cmd = new IdentifyCmd();
        StringOutputConsumer output = new StringOutputConsumer();
        cmd.setOutputConsumer(output);
        cmd.run(op);

        String strResult = output.getOutput();
        String strWidth = StringUtils.substringBefore(strResult, " ");
        String strheight = StringUtils.substringAfter(strResult, " ");

        int[] result = new int[2];
        result[0] = Integer.valueOf(strWidth);
        result[1] = Integer.valueOf(strheight);
        return result;
    }

    public static IMOperation rotate(IMOperation operation, String orientation) {
        if (orientation == null) {
            return operation;
        }

        switch (orientation) {
            case "1":
                break;
            case "2":
                operation.flop();
                break;
            case "3":
                operation.rotate(180.0);
                break;
            case "4":
                operation.flip();
                break;
            case "5":
                operation.transpose();
                break;
            case "6":
                operation.rotate(90.0);
                break;
            case "7":
                operation.transverse();
                break;
            case "8":
                operation.rotate(270.0);
                break;
            default:
                break;
        }
        return operation;
    }

    public static void generateThumbnail(String sourcePath, String targetPath, ThumbnailSize size, String orientation) throws IOException, InterruptedException, IM4JavaException {
        ImageCommand cmd = new ConvertCmd(true);
        IMOperation operation = new IMOperation();
        operation.addImage(sourcePath);

        if (size.isCropped()) {
            operation.thumbnail(size.getWidth(), size.getHeight(), "^");
        } else {
            operation.thumbnail(size.getWidth(), size.getHeight());
        }

        operation = ImageUtils.rotate(operation, orientation);
        operation.profile(sourcePath);
        operation.addImage(targetPath);
        cmd.run(operation);

        logger.debug("Generate from {} to {}", sourcePath, targetPath);

        if (size.isCropped()) {
            int[] thumbnailSize = ImageUtils.getSize(targetPath);
            int width = thumbnailSize[0];
            int height = thumbnailSize[1];
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
            operation.addImage(targetPath);
            operation.crop(size.getWidth(), size.getHeight(), x, y);
            operation.addImage(targetPath);

            cmd = new MogrifyCmd(true);
            cmd.run(operation);
        }
    }
    
}
