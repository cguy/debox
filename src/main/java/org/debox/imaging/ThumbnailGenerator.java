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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.Callable;
import org.apache.commons.lang3.tuple.Pair;
import org.debox.photo.model.Media;
import org.debox.photo.model.configuration.ThumbnailSize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class ThumbnailGenerator implements Callable<Pair<String, FileInputStream>> {

    private static final Logger logger = LoggerFactory.getLogger(ThumbnailGenerator.class);
    protected Media media;
    protected ThumbnailSize[] sizes;

    public ThumbnailGenerator(Media media, ThumbnailSize... sizes) {
        this.media = media;
        this.sizes = sizes;
    }

    @Override
    public Pair<String, FileInputStream> call() throws Exception {
        Pair<String, FileInputStream> result = null;
        String sourcePath = ImageUtils.getSourcePath(media);
        
        try {
            // Sort thumbnails size (desc) to optimize image processing (use bigger image to create thumbnail, but not the huge original)
            Arrays.sort(sizes, new ThumbnailSize.Comparator());
            for (ThumbnailSize size : sizes) {
                String thumbnailPath = ImageUtils.getThumbnailPath(media, size);
                ImageUtils.thumbnail(sourcePath, thumbnailPath, size);
                sourcePath = thumbnailPath;
            }
            // If only one size was requested, we return the corresponding stream
            if (sizes.length == 1) {
                result = Pair.of(sourcePath, new FileInputStream(sourcePath));
            }
        } catch (SQLException | FileNotFoundException ex) {
            logger.error(ex.getMessage(), ex);
        }
        return result;
    }
    
}
