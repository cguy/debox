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
package org.debox.imaging;

import java.awt.image.ImagingOpException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Callable;
import org.apache.commons.lang3.tuple.Pair;
import org.debox.photo.model.Photo;
import org.debox.photo.model.configuration.ThumbnailSize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class ThumbnailGenerator implements Callable<Pair<String, FileInputStream>> {

    private static final Logger logger = LoggerFactory.getLogger(ThumbnailGenerator.class);
    protected String sourcePath;
    protected String targetPath;
    protected Photo photo;
    protected ThumbnailSize[] sizes;

    public ThumbnailGenerator(String sourcePath, Photo photo, String targetPath, ThumbnailSize... sizes) {
        this.sourcePath = sourcePath;
        this.targetPath = targetPath;
        this.photo = photo;
        this.sizes = sizes;
    }

    @Override
    public Pair<String, FileInputStream> call() throws Exception {
        Pair<String, FileInputStream> result = null;

        try {
            String imagePath = sourcePath + photo.getRelativePath() + File.separatorChar + photo.getFilename();

            // Sort thumbnails size (desc) to optimize image processing (use bigger image to create thumbnail, but not the huge original)
            Arrays.sort(sizes, new ThumbnailSize.Comparator());
            for (ThumbnailSize size : sizes) {
                String photoTargetPath = targetPath + photo.getRelativePath() + File.separatorChar + size.getPrefix() + photo.getFilename();
                ImageUtils.thumbnail(imagePath, photoTargetPath, size);

                imagePath = photoTargetPath;
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
    
}
