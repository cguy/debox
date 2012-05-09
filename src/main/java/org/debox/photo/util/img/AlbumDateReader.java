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
package org.debox.photo.util.img;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;
import org.apache.commons.lang3.tuple.Pair;
import org.debox.photo.model.Album;
import org.debox.photo.model.Photo;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class AlbumDateReader extends RecursiveTask<Pair<List<Album>, List<Photo>>> {
    protected ArrayList<PhotoDateReader> inProgress;

    List<Album> albums;
    List<Photo> photos;
    String basePath;
    boolean forceCheckDates;
    
    public AlbumDateReader(String basePath, List<Album> albums, List<Photo> photos, boolean forceCheckDates) {
        this.albums = albums;
        this.photos = photos;
        this.basePath = basePath;
        this.forceCheckDates = forceCheckDates;
    }
    
    public boolean isPhotoInAlbum(Photo photo, Album album) {
        return photo.getRelativePath().startsWith(album.getRelativePath());
    }
    
    public long getNumbertoProcess() {
        if (inProgress == null) {
            return 0;
        }
        return inProgress.size();
    }
    
    public long getNumberProcessed() {
        if (inProgress == null) {
            return 0;
        }
        long result = 0;
        for (PhotoDateReader task : inProgress) {
            if (task.isDone()) {
                result++;
            }
        }
        return result;
    }
    
    public boolean isTerminated() {
        if (inProgress == null) {
            return true;
        }
        for (PhotoDateReader task : inProgress) {
            if (!task.isDone()) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected Pair<List<Album>, List<Photo>> compute() {
        inProgress = new ArrayList<>();
        for (Photo photo : photos) {
            if (forceCheckDates || photo.getDate() == null) {
                PhotoDateReader photoDateDetector = new PhotoDateReader(basePath, photo);
                inProgress.add(photoDateDetector);
                photoDateDetector.fork();
            }
        }
        
        // Reset old saved dates
        if (forceCheckDates) {
            for (Album album : albums) {
                album.setBeginDate(null);
                album.setEndDate(null);
            }
        }

        for (PhotoDateReader detector : inProgress) {
            Photo photo = detector.join();
            for (Album album : albums) {
                if (isPhotoInAlbum(photo, album)) {
                    if (album.getBeginDate() == null || photo.getDate().before(album.getBeginDate())) {
                        album.setBeginDate(photo.getDate());
                    }
                    if (album.getEndDate() == null || photo.getDate().after(album.getEndDate())) {
                        album.setEndDate(photo.getDate());
                    }
                }
            }
            for (Photo current : photos) {
                if (photo.equals(current)) {
                    current.setDate(photo.getDate());
                    break;
                }
            }
        }

        return Pair.of(albums, photos);
    }
}
