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
package org.debox.photo.dao.thirdparty;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.RememberMeAuthenticationToken;
import org.scribe.model.Verifier;

public class ThirdPartyTokenWrapper implements AuthenticationToken, RememberMeAuthenticationToken {

    private static final long serialVersionUID = 1L;
    private Verifier code;

    public ThirdPartyTokenWrapper(Verifier code) {
        this.code = code;
    }

    @Override
    public Object getPrincipal() {
        return null;// not known - facebook does the login
    }

    @Override
    public Object getCredentials() {
        return null;// credentials handled by facebook - we don't need them
    }

    public Verifier getCode() {
        return code;
    }

    public void setCode(Verifier code) {
        this.code = code;
    }

    @Override
    public boolean isRememberMe() {
        return true;
    }
    
}
