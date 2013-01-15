package org.debox.imaging;

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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class DefaultFileDateReader implements DateReader {

    private static final Logger logger = LoggerFactory.getLogger(DefaultFileDateReader.class);

    @Override
    public Date getShootingDate(Path path) {
        Date date;
        try {
            FileTime lastModifiedTime = Files.getLastModifiedTime(path);
            date = new Date(lastModifiedTime.toMillis());

        } catch (IOException ex) {
            logger.error("Error while getting the last modification date of {}, reason: {}", path, ex.getMessage());
            date = new Date();
        }
        return date;
    }
}
