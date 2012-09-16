package org.debox.photo.model.notification;

import org.debox.photo.model.user.User;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public abstract class Notification {
    
    protected String id;
    protected User source;
    protected String message;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getSource() {
        return source;
    }

    public void setSource(User source) {
        this.source = source;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
}
