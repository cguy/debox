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
package org.debox.photo.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class ApplicationContext {
    
    public static final File target = new File("/home/cguy/public_app");
    
    protected static List<Album> albums = new ArrayList<Album>();

    public static List<Album> getAlbums() {
        return albums;
    }

    public static void setAlbums(List<Album> albums) {
        ApplicationContext.albums = albums;
    }
    
    public static void addAlbum(Album album) {
        albums.add(album);
    }

    public static Album getAlbum(String album) {
        for (Album current : albums) {
            if (current.getName().equals(album)) {
                return current;
            }
        }
        return null;
    }
    
}
