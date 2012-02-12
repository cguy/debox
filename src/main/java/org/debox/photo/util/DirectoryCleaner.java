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

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class DirectoryCleaner implements FileVisitor<Path> {

    private static final Logger logger = LoggerFactory.getLogger(DirectoryCleaner.class);
    protected Path directoryToEmpty;

    public DirectoryCleaner(Path directoryToEmpty) {
        this.directoryToEmpty = directoryToEmpty;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        // Nothing to do
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        try {
            // Delete each visited file
            Files.delete(file);

        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        // Nothing to do
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        try {
            // Delete each visited directory after deleted all contained files
            // We just want to empty directory, not delete it
            if (!directoryToEmpty.equals(dir)) {
                Files.delete(dir);
            }
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
        return FileVisitResult.CONTINUE;
    }
}
