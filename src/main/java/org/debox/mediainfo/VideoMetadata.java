package org.debox.mediainfo;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class VideoMetadata {
    
    private static final Logger log = LoggerFactory.getLogger(VideoMetadata.class);
    
    protected GeneralMetadata generalMetadata = new GeneralMetadata();
    protected VideoTrackMetadata videoTrackMetadata = new VideoTrackMetadata();
    protected AudioTrackMetadata audioTrackMetadata = new AudioTrackMetadata();
    
    protected class GeneralMetadata {
        protected String filename;
        protected String format;
        protected String formatProfile;
        protected String codecId;
        protected String fileSize;
        protected String duration;
        protected String encodedDate;
    }
    
    protected class VideoTrackMetadata {
        protected String width;
        protected String height;
        protected String aspectRatio;
    }
    
    protected class AudioTrackMetadata {
        protected String bitrate;
        protected String frequency;
    }
    
    public String getFilename() {
        return generalMetadata.filename;
    }

    public void setFilename(String filename) {
        this.generalMetadata.filename = filename;
    }

    public String getFormat() {
        return generalMetadata.format;
    }

    public void setFormat(String format) {
        this.generalMetadata.format = format;
    }

    public String getFormatProfile() {
        return generalMetadata.formatProfile;
    }

    public void setFormatProfile(String formatProfile) {
        this.generalMetadata.formatProfile = formatProfile;
    }

    public String getCodecId() {
        return generalMetadata.codecId;
    }

    public void setCodecId(String codecId) {
        this.generalMetadata.codecId = codecId;
    }

    public String getFileSize() {
        return generalMetadata.fileSize;
    }

    public void setFileSize(String fileSize) {
        this.generalMetadata.fileSize = fileSize;
    }

    public String getDuration() {
        return generalMetadata.duration;
    }

    public void setDuration(String duration) {
        this.generalMetadata.duration = duration;
    }

    public Date getEncodedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = StringUtils.substringAfter(generalMetadata.encodedDate, " ");
        Date date = null;
        if (str != null) {
            try {
                date = sdf.parse(str);
            } catch (ParseException ex) {
                log.error("Unable to parse date: {}", str);
            }
        }
        return date;
    }

    public void setEncodedDate(String encodedDate) {
        this.generalMetadata.encodedDate = encodedDate;
    }
    
    public String getWidth() {
        return this.videoTrackMetadata.width;
    }

    public void setWidth(String width) {
        this.videoTrackMetadata.width = width;
    }

    public String getHeight() {
        return videoTrackMetadata.height;
    }

    public void setHeight(String height) {
        this.videoTrackMetadata.height = height;
    }

    public String getAspectRatio() {
        return videoTrackMetadata.aspectRatio;
    }

    public void setAspectRatio(String aspectRatio) {
        this.videoTrackMetadata.aspectRatio = aspectRatio;
    }
    
    public String getBitrate() {
        return audioTrackMetadata.bitrate;
    }

    public void setBitrate(String bitrate) {
        this.audioTrackMetadata.bitrate = bitrate;
    }

    public String getFrequency() {
        return audioTrackMetadata.frequency;
    }

    public void setFrequency(String frequency) {
        this.audioTrackMetadata.frequency = frequency;
    }
    
}
