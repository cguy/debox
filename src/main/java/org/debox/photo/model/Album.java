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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import static org.debox.photo.util.StringUtils.WHITESPACE;
import org.debox.photo.util.i18n.I18nUtils;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class Album implements Comparable<Album> {

    protected String id;
    protected String name;
    protected String description;
    protected Date beginDate;
    protected Date endDate;
    protected int photosCount;
    protected int totalPhotosCount;
    protected int videosCount;
    protected int totalVideosCount;
    protected String relativePath;
    protected String parentId;
    protected String coverUrl;
    protected boolean publicAlbum;
    protected boolean downloadable;
    protected String ownerId;
    protected int subAlbumsCount;
    
    protected boolean hasSeveralTotalPhotos() {
        return this.totalPhotosCount > 1;
    }
    
    protected boolean hasSeveralTotalVideos() {
        return this.totalVideosCount > 1;
    }
    
    protected boolean hasMedias() {
        return hasSeveralTotalPhotos() || hasSeveralTotalVideos();
    }
    
    public String getInformation() {
        StringBuilder builder = new StringBuilder();
        
        if (photosCount > 0) {
            builder.append(totalPhotosCount);
            builder.append(WHITESPACE);
            if (photosCount > 1) {
                builder.append(I18nUtils.get("common.photos"));
            } else {
                builder.append(I18nUtils.get("common.photo"));
            }
            
            if (videosCount > 0) {
                builder.append(WHITESPACE);
                builder.append(I18nUtils.get("common.and"));
            }
        }
        if (videosCount > 0) {
            builder.append(WHITESPACE);
            builder.append(totalVideosCount);
            builder.append(WHITESPACE);
            if (videosCount > 1) {
                builder.append(I18nUtils.get("common.videos"));
            } else {
                builder.append(I18nUtils.get("common.video"));
            }
        }
        if (totalPhotosCount > 0 || totalVideosCount > 0)  {
            builder.append(WHITESPACE);
            if (getStrBeginDate().equals(getStrEndDate())) {
                builder.append(I18nUtils.get("album.on_date"));
                builder.append(WHITESPACE);
                builder.append(getStrBeginDate());
            } else {
                builder.append(I18nUtils.get("album.from_date"));
                builder.append(WHITESPACE);
                builder.append(getStrBeginDate());
                builder.append(WHITESPACE);
                builder.append(I18nUtils.get("album.to_date"));
                builder.append(WHITESPACE);
                builder.append(getStrEndDate());
            }
        } else {
            builder.append(I18nUtils.get("common.noPhotos"));
        }
        
        return builder.toString();
    }
    
    public String getSmallSizeDownloadUrl() {
        return getDownloadUrl() + "/min";
    }
    
    public String getDownloadUrl() {
        return "/download/album/" + id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

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

    public String getStrBeginDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
        if (this.beginDate != null) {
            return sdf.format(this.beginDate);
        }
        return null;
    }

    public String getStrEndDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
        if (this.endDate != null) {
            return sdf.format(this.endDate);
        }
        return null;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    public boolean isPublicAlbum() {
        return publicAlbum;
    }

    public void setPublic(boolean isPublic) {
        this.publicAlbum = isPublic;
    }

    public int getVideosCount() {
        return videosCount;
    }

    public void setVideosCount(int videosCount) {
        this.videosCount = videosCount;
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

    public void setTotalPhotosCount(int count) {
        this.totalPhotosCount = count;
    }

    public void setTotalVideosCount(int count) {
        this.totalVideosCount = count;
    }

    public int getTotalPhotosCount() {
        return totalPhotosCount;
    }

    public int getTotalVideosCount() {
        return totalVideosCount;
    }
    
}
