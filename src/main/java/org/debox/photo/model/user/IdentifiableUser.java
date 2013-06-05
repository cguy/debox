package org.debox.photo.model.user;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.debox.photo.model.Role;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public abstract class IdentifiableUser extends User {
    
    protected Role role;
    
    protected String firstName;
    protected String lastName;
    protected String avatar;
    
    protected List<ThirdPartyAccount> thirdPartyAccounts;

    protected Set<String> photosSubscriptions;
    protected Set<String> albumssSubscriptions;
    
    public String getToken() {
        return null;
    }

    public Set<String> getPhotosSubscriptions() {
        return photosSubscriptions;
    }

    public void setPhotosSubscriptions(Set<String> photosSubscriptions) {
        this.photosSubscriptions = photosSubscriptions;
    }

    public Set<String> getAlbumssSubscriptions() {
        return albumssSubscriptions;
    }

    public void setAlbumssSubscriptions(Set<String> albumssSubscriptions) {
        this.albumssSubscriptions = albumssSubscriptions;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
    
    public List<ThirdPartyAccount> getThirdPartyAccounts() {
        if (thirdPartyAccounts == null) {
            thirdPartyAccounts = new ArrayList<>();
        }
        return thirdPartyAccounts;
    }

    public void setThirdPartyAccounts(List<ThirdPartyAccount> thirdPartyAccounts) {
        this.thirdPartyAccounts = thirdPartyAccounts;
    }
    
}
