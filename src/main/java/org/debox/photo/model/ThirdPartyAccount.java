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

import java.util.Objects;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class ThirdPartyAccount extends User {
    
    protected Provider provider;
    protected String providerAccountId;
    protected String token;
    
    protected String username;
    protected String accountUrl;
    protected String avatarUrl;
    
    public ThirdPartyAccount() {
        // Default constructor
    }
    
    public ThirdPartyAccount(Provider provider, String providerAccountId, String token) {
        this.provider = provider;
        this.providerAccountId = providerAccountId;
        this.token = token;
    }
    
    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof ThirdPartyAccount)) {
            return false;
        }
        
        ThirdPartyAccount other = (ThirdPartyAccount) object;
        return Objects.equals(this.id, other.getId())
                && Objects.equals(this.provider, other.getProvider())
                && Objects.equals(this.providerAccountId, other.getProviderAccountId());
    }

    public String getAvatarUrl() {
        return String.format("https://graph.facebook.com/%s/picture?return_ssl_resources=1&type=square", getProviderAccountId());
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAccountUrl() {
        return accountUrl;
    }

    public void setAccountUrl(String accountUrl) {
        this.accountUrl = accountUrl;
    }
    
    public String getProviderAccountId() {
        return providerAccountId;
    }

    public void setProviderAccountId(String providerAccountId) {
        this.providerAccountId = providerAccountId;
    }

    public String getProviderId() {
        return provider.getId();
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
