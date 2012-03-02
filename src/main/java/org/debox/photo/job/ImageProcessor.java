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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Callable;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.debox.photo.job.im4java.StringOutputConsumer;
import org.debox.photo.model.Configuration;
import org.debox.photo.model.Photo;
import org.debox.photo.model.ThumbnailSize;
import org.im4java.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class ImageProcessor implements Callable<Pair<String, FileInputStream>> {

    private static final Logger logger = LoggerFactory.getLogger(ImageProcessor.class);
    
    protected Configuration configuration;
    protected Photo photo;
    protected ThumbnailSize[] sizes;

    public ImageProcessor(Configuration configuration, Photo photo, ThumbnailSize ... sizes) {
        this.configuration = configuration;
        this.photo = photo;
        this.sizes = sizes;
    }

    @Override
    public Pair<String, FileInputStream> call() throws Exception {
        Pair<String, FileInputStream> result = null;
        
        try {
            String imagePath = configuration.get(Configuration.Key.SOURCE_PATH) + photo.getRelativePath() + File.separatorChar + photo.getName();
            logger.debug("{} image processing ...", imagePath);

            // Read orientation from source
            String orientation = getOrientation(imagePath);
            
            // Sort thumbnails size (desc) to optimize image processing (use bigger image to create thumbnail, but not the huge original)
            Arrays.sort(sizes, new ThumbnailSize.Comparator());
            for (ThumbnailSize size : sizes) {
                String targetPath = configuration.get(Configuration.Key.TARGET_PATH) + photo.getRelativePath() + File.separatorChar + size.getPrefix() + photo.getName() + ".jpg";
                
                generateThumbnail(imagePath, targetPath, size, orientation);
                
                imagePath = targetPath;
                orientation = null;
            }
            
            // If only one size was requested, we return the corresponding stream
            if (sizes.length == 1) {
                result = Pair.of(imagePath, new FileInputStream(imagePath));
            }
            
        } catch (IOException | IllegalArgumentException | ImagingOpException ex) {
            logger.error(ex.getMessage(), ex);

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return result;
    }
    
    protected void generateThumbnail(String sourcePath, String targetPath, ThumbnailSize size, String orientation) throws IOException, InterruptedException, IM4JavaException {
        ImageCommand cmd = new ConvertCmd(true);
        IMOperation operation = new IMOperation();
        operation.addImage(sourcePath);

        if (size.isCropped()) {
            operation.thumbnail(size.getWidth(), size.getHeight(), "^");
        } else {
            operation.thumbnail(size.getWidth(), size.getHeight());
        }
        
        operation = rotate(operation, orientation);
        operation.addImage(targetPath);
        cmd.run(operation);
        
        logger.debug("Generate from {} to {}", sourcePath, targetPath);
        
        if (size.isCropped()) {
            int[] thumbnailSize = getSize(targetPath);
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

    protected String getOrientation(String path) throws IOException, IM4JavaException, InterruptedException {
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
    
    protected int[] getSize(String path) throws IOException, InterruptedException, IM4JavaException {
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

    protected IMOperation rotate(IMOperation operation, String orientation) {
        if (operation == null) {
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
    
}
