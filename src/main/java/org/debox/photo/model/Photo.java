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

import org.debox.photo.model.configuration.ThumbnailSize;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class Photo extends Media {

    protected static final String type = "photo";
    protected static final boolean isPhoto = true;
    
    protected String url;
    
    public void computeAccessUrl(String token) {
        String baseUrl = "photos/" + this.getId() + ".jpg";
        char separator = '?'; 
        if (token != null) {
            baseUrl += "?token=" + token;
            separator = '&';
        }
        this.setThumbnailUrl(baseUrl + separator + "size=" + ThumbnailSize.SQUARE.getLowerCaseName());
        this.setUrl(baseUrl + separator + "size=" + ThumbnailSize.LARGE.getLowerCaseName());
    }
    
    public boolean isPhoto() {
        return isPhoto;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
}
