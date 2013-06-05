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
function createAlbum(album) {
    if (!album) {
        album = {};
    }
    album.hasSeveralPhotos = function() {
        return album.photos && album.photos.length > 1;
    };
    album.hasSeveralTotalPhotos = function() {
        return album.photosCount && album.photosCount > 1;
    };
    album.hasSeveralVideos = function() {
        return album.videos && album.videos.length > 1;
    };
    album.hasSeveralTotalVideos = function() {
        return album.videosCount && album.videosCount > 1;
    };
    album.hasMedias = function() {
        return album.hasSeveralTotalPhotos() || album.hasSeveralTotalVideos();
    };
    
    var beginDate = null;
    if (album.beginDate) {
        beginDate = moment(album.beginDate).startOf("day");
        album.beginDate = beginDate.format("D MMMM YYYY");
    }
                    
    var endDate = null;
    if (album.endDate) {
        endDate = moment(album.endDate).startOf("day");
        album.endDate = endDate.format("D MMMM YYYY");
    }
    
    if (beginDate && endDate) {
        album.isInterval = !beginDate.isSame(endDate);
    }
                    
    album.minDownloadUrl = computeUrl("albums/" + album.id + "-min.zip");
    album.downloadUrl = computeUrl("albums/" + album.id + ".zip");
    
    return album;
}