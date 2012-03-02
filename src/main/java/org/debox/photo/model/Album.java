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

import java.util.Date;
import java.util.Objects;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class Album implements Comparable<Album> {

    public enum Visibility {
        PUBLIC,
        PRIVATE
    }
    
    protected String id;
    protected String name;
    protected Date date;
    protected int photosCount;
    protected String relativePath;
    protected String parentId;
    protected Visibility visibility;
    protected String coverUrl;
    protected boolean downloadable;

    public boolean isDownloadable() {
        return downloadable;
    }

    public void setDownloadable(boolean downloadable) {
        this.downloadable = downloadable;
    }
    
    public int getPhotosCount() {
        return photosCount;
    }

    public void setPhotosCount(int photosCount) {
        this.photosCount = photosCount;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }
    
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }
    
    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        } 
        if (object instanceof Album) {
            Album album = (Album) object; 
            return Objects.equals(this.relativePath, album.getRelativePath());
        } 
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.relativePath);
    }

    @Override
    public int compareTo(Album album) {
        return this.getRelativePath().compareToIgnoreCase(album.getRelativePath());
    }
}
