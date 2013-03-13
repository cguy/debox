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
package org.debox.photo.model.configuration;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public enum ThumbnailSize {
    
    SQUARE ("s_", 140, 140, true),
    LARGE ("l_", 1920, 1200),
    ORIGINAL;
    
    protected String prefix;
    protected int width;
    protected int height;
    protected boolean cropped;
    
    public static ThumbnailSize byName(String name) {
        for (ThumbnailSize size : ThumbnailSize.values()) {
            if (size.name().toLowerCase().equals(name)) {
                return size;
            }
        }
        return null;
    }
    
    private ThumbnailSize() {
        this(null, -1, -1, false);
    }
    
    private ThumbnailSize(String prefix, int width, int height) {
        this(prefix, width, height, false);
    }
    
    private ThumbnailSize(String prefix, int width, int height, boolean cropped) {
        this.prefix = prefix;
        this.width = width;
        this.height = height;
        this.cropped = cropped;
    }
    
    public String getLowerCaseName() {
        return this.name().toLowerCase();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isCropped() {
        return cropped;
    }

    public String getPrefix() {
        return prefix;
    }
    
    public static class Comparator implements java.util.Comparator<ThumbnailSize> {

        @Override
        public int compare(ThumbnailSize o1, ThumbnailSize o2) {
            long size1 = o1.getWidth() * o1.getHeight();
            long size2 = o2.getWidth() * o2.getHeight();
            
            if (size1 == size2) {
                return 0;
            } else if (size1 < size2) {
                return 1;
            }
            return -1;
        }
        
    }
    
}
