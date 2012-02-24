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
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.IOUtils;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class FileUtils {

    public static byte[] zipDirectoryContent(Path directoryPath, Map<String, String> names) throws IOException {
        File directoryFile = directoryPath.toFile();
        File[] files = directoryFile.listFiles();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
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
                        zos.write(IOUtils.toByteArray(fis, Files.size(Paths.get(file.getAbsolutePath()))));
                        zos.closeEntry();
                    }
                }
            }
            zos.flush();
            baos.flush();
        }
        baos.close();
        return baos.toByteArray();
    }
}
