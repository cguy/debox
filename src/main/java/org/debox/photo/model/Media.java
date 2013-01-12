package org.debox.photo.model;

/*
 * #%L
 * debox-photos
 * %%
 * Copyright (C) 2012 - 2013 Debox
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

import java.io.File;
import java.util.Date;
import java.util.Objects;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class Media implements Comparable<Media>, Datable {
    
    protected String id;
    protected String filename;
    protected String title;
    protected String relativePath;
    protected String albumId;
    protected Date date;
    protected String ownerId;
    protected String thumbnailUrl;

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        } 
        if (object instanceof Photo) {
            Photo photo = (Photo) object; 
            return Objects.equals(this.relativePath + File.separatorChar + this.getFilename(), photo.getRelativePath() + File.separatorChar + photo.getFilename());
        } 
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.relativePath);
    }

    @Override
    public int compareTo(Media media) {
        return this.getFilename().compareToIgnoreCase(media.getFilename());
    }
    
    @Override
    public Date getDate() {
        return date;
    }
    
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    
    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }
        
}
