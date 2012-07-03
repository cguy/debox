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

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Enumeration;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class FileUtils extends org.apache.commons.io.FileUtils {

    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);
    /**
     * Default permissions for created directories and files, corresponding with 775 digit value.
     */
    public static final FileAttribute PERMISSIONS = PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwxrwx---"));
    
    public static void createDirectories(Path path) throws IOException {
        try {
            Files.createDirectories(path, PERMISSIONS);
        } catch (UnsupportedOperationException ex) {
            logger.error("Cannot create directories, reason: " + ex.getMessage() + ", trying without specified permissions...");
            try {
                Files.createDirectories(path);
            } catch (UnsupportedOperationException ex1) {
                logger.error("Cannot create directories (without any specified permissions), reason: " + ex.getMessage());
                throw ex1;
            }
        }
    }

    public static long getSize(Path directoryPath, Map<String, String> names) throws IOException {
        File directoryFile = directoryPath.toFile();
        File[] files = directoryFile.listFiles();
        long size = 0;

        for (File file : files) {
            if (file.isDirectory()) {
                continue;
            }
            size += file.length();
        }

        return size;
    }

    public static void zipDirectoryContent(OutputStream outputStream, Path directoryPath, Map<String, String> names) throws IOException {
        File directoryFile = directoryPath.toFile();
        File[] files = directoryFile.listFiles();

        try (ZipOutputStream zos = new ZipOutputStream(outputStream)) {
            for (File file : files) {
                if (file.isDirectory()) {
                    continue;
                }
                String fileName = file.getName();
                if (names != null) {
                    fileName = names.get(file.getName());
                }

                // If current file isn't in names collection, skip it
                if (fileName != null) {
                    try (FileInputStream fis = new FileInputStream(file)) {
                        zos.putNextEntry(new ZipEntry(fileName));
                        byte[] byteArray = IOUtils.toByteArray(fis, Files.size(Paths.get(file.getAbsolutePath())));
                        zos.write(byteArray);
                        zos.closeEntry();
                    }
                }
            }
            zos.flush();
        }
    }

}
