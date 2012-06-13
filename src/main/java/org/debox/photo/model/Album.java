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

    protected String id;
    protected String name;
    protected Date beginDate;
    protected Date endDate;
    protected int photosCount;
    protected String relativePath;
    protected String parentId;
    protected String coverUrl;
    protected boolean isPublic;
    protected boolean downloadable;
    protected int subAlbumsCount;

    public int getSubAlbumsCount() {
        return subAlbumsCount;
    }

    public void setSubAlbumsCount(int subAlbumsCount) {
        this.subAlbumsCount = subAlbumsCount;
    }
    
    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
    
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

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
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

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
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
    
    public boolean hasParent() {
        return this.parentId != null;
    }
    
    public boolean isSubAlbum(Album target) {
        return target != null && this.getRelativePath().startsWith(target.getRelativePath());
    }
    
}
