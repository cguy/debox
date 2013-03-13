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
package org.debox.photo.model.comment;

import java.text.SimpleDateFormat;
import org.debox.photo.model.user.User;
import java.util.Date;
import org.debox.photo.util.SessionUtils;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class Comment<T> {
    
    protected String id;
    protected User user;
    protected String content;
    protected Date date;
    protected T media;

    public T getMedia() {
        return media;
    }

    public void setMedia(T media) {
        this.media = media;
    }
    
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    
    public String getStrDate() {
        if (this.date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
        return sdf.format(this.date);
    }
    
    public boolean isDeletable() {
        boolean isAdministrator = SessionUtils.isAdministrator();
        boolean canDelete = getUser() != null && getUser().getId().equals(SessionUtils.getUserId());
        return isAdministrator || canDelete;
    }

}
