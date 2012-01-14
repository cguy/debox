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
package org.debox.photo;

import java.io.File;
import java.util.Comparator;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class FileNameComparator implements Comparator<File> {

    @Override
    public int compare(File file, File anotherFile) {
        if (file.isDirectory() && anotherFile.isFile()) {
            return -1;
        } else if (file.isFile() && anotherFile.isDirectory()) {
            return 1;
        }
        return file.getName().compareTo(anotherFile.getName());
    }
    
}
