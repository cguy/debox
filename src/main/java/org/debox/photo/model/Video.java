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
import java.util.Objects;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class Video extends Media {
    
    protected static final String type = "video";
    protected final boolean video = true;
    
    protected boolean supportsOgg;
    protected boolean supportsH264;
    protected boolean supportsWebM;
    protected boolean hasThumbnail;
    
    protected String oggUrl;
    protected String h264Url;
    protected String webmUrl;
    
    protected String squareThumbnailUrl;
    
    public void computeAccessUrl(String token) {
        String baseUrl = "video/" + this.getId();
        String suffix = StringUtils.EMPTY;
        char separator = '?';
        if (token != null) {
            separator = '&';
            suffix = "?token=" + token;
        }
        if (supportsOgg) {
            setOggUrl(baseUrl + ".ogv" + suffix);
        }
        if (supportsH264) {
            setH264Url(baseUrl + ".mp4" + suffix);
        }
        if (supportsWebM) {
            setWebmUrl(baseUrl + ".webm" + suffix);
        }
        if (hasThumbnail) {
            setThumbnailUrl(baseUrl + ".jpg" + suffix + separator + "size=large");
            setSquareThumbnailUrl(baseUrl + ".jpg" + suffix + separator + "size=square");
        }
    }

    public String getSquareThumbnailUrl() {
        return squareThumbnailUrl;
    }

    public void setSquareThumbnailUrl(String squareThumbnailUrl) {
        this.squareThumbnailUrl = squareThumbnailUrl;
    }
    
    public boolean isVideo() {
        return video;
    }
    
    public String getOggFilename() {
        return this.filename + ".ogv";
    }
    
    public String getH264Filename() {
        return this.filename + ".mp4";
    }
    
    public String getWebMFilename() {
        return this.filename + ".webm";
    }

    public boolean supportsOgg() {
        return supportsOgg;
    }

    public void setSupportsOgg(boolean supportsOgv) {
        this.supportsOgg = supportsOgv;
    }

    public boolean supportsH264() {
        return supportsH264;
    }

    public void setSupportsH264(boolean supportsH264) {
        this.supportsH264 = supportsH264;
    }

    public boolean supportsWebM() {
        return supportsWebM;
    }

    public void setSupportsWebM(boolean supportsWebM) {
        this.supportsWebM = supportsWebM;
    }

    public boolean hasThumbnail() {
        return hasThumbnail;
    }

    public void setThumbnail(boolean hasThumbnail) {
        this.hasThumbnail = hasThumbnail;
    }
    
    public String getOggUrl() {
        return oggUrl;
    }

    public void setOggUrl(String oggUrl) {
        this.oggUrl = oggUrl;
    }

    public String getH264Url() {
        return h264Url;
    }

    public void setH264Url(String h264Url) {
        this.h264Url = h264Url;
    }

    public String getWebmUrl() {
        return webmUrl;
    }

    public void setWebmUrl(String webmUrl) {
        this.webmUrl = webmUrl;
    }
    
    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        } 
        if (object instanceof Video) {
            Video photo = (Video) object;
            String currentFilename = FilenameUtils.removeExtension(this.getFilename());
            String anotherFilename = FilenameUtils.removeExtension(photo.getFilename());
            return Objects.equals(this.relativePath + File.separatorChar + currentFilename, photo.getRelativePath() + File.separatorChar + anotherFilename);
        } 
        return false;
    }

}
