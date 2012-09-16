package org.debox.photo.model.notification;

import org.debox.photo.model.Album;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class AlbumNotification extends Notification {
    
    protected Album album;

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }
    
}
