package org.debox.photo.util.i18n;

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

import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.Arguments;
import org.thymeleaf.messageresolver.MessageResolution;
import org.thymeleaf.messageresolver.StandardMessageResolver;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class CustomMessageResolver extends StandardMessageResolver {

    private static final Logger log = LoggerFactory.getLogger(CustomMessageResolver.class);

    @Override
    protected void initializeSpecific() {
        I18nUtils.getBundle(Locale.getDefault().getLanguage());
    }

    @Override
    public MessageResolution resolveMessage(final Arguments arguments, final String key, final Object[] messageParameters) {
        MessageResolution messageResolution = super.resolveMessage(arguments, key, messageParameters);
        if (messageResolution != null) {
            return messageResolution;
        }
        
        Locale locale = arguments.getContext().getLocale();
        String language = locale.getLanguage();
        String message = I18nUtils.getProperty(key, language);
        
        return new MessageResolution(message);
    }
    
}
