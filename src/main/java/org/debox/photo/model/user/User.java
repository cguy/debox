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
package org.debox.photo.model.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.debox.photo.model.Role;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public abstract class User implements Serializable {

    protected String id;
    
    protected Role role;
    
    protected String firstName;
    protected String lastName;
    protected String avatar;
    
    protected List<ThirdPartyAccount> thirdPartyAccounts;

    protected Set<String> photosSubscriptions;
    protected Set<String> albumssSubscriptions;

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
    
    public String getThirdPartyAccess(String provider) {
        for (ThirdPartyAccount account : thirdPartyAccounts) {
            if (provider.equals(account.getProviderId())) {
                return account.getToken();
            }
        }
        return null;
    }
    
    public void addThirdPartyAccount(ThirdPartyAccount account) {
        getThirdPartyAccounts().add(account);
    }
    
    public void removeThirdPartyAccount(ThirdPartyAccount account) {
        getThirdPartyAccounts().remove(account);
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
