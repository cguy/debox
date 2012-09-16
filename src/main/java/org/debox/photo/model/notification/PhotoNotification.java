package org.debox.photo.model.notification;

import org.debox.photo.model.Photo;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class PhotoNotification extends Notification {
    
    protected Photo photo;

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }
    
}
