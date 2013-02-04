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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.Thumbnails.Builder;
import net.coobird.thumbnailator.geometry.Positions;
import org.debox.imaging.ImageHandler;
import org.debox.photo.model.configuration.ThumbnailSize;
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
        if (size.isCropped()) {
            builder = builder.crop(Positions.CENTER);
        }
        builder = builder.size(size.getWidth(), size.getHeight());

        try {
            builder.toFile(target.toString());
            log.debug("Create thumbnail at path : " + target.toString());
        } catch (IOException ex) {
            log.error("Unable to write file {}, reason: {}", target.toString(), ex.getMessage());
        }
    }

    @Override
    public void thumbnail(Path source, Path target, ThumbnailSize size, boolean async) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
