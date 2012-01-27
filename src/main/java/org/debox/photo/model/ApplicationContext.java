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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class ApplicationContext {
    
    private static final Logger logger = LoggerFactory.getLogger(ApplicationContext.class);
    
    protected static File source;
    protected static File target;
    
    protected static List<Album> albums = new ArrayList<Album>();

    public static File getSource() {
        return source;
    }

    public static void setSource(File source) {
        ApplicationContext.source = source;
    }

    public static File getTarget() {
        return target;
    }

    public static void setTarget(File target) {
        ApplicationContext.target = target;
    }

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
            logger.info("Compare \"{}\" to \"{}\"", current.getName(), album);
            if (current.getName().equals(album)) {
                return current;
            }
        }
        return null;
    }
    
}
