package org.debox.photo.util;

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

import com.github.mxab.thymeleaf.extras.dataattribute.dialect.DataAttributeDialect;
import java.io.File;
import java.net.URI;
import java.net.URL;
import javax.servlet.ServletContext;
import org.debox.photo.util.i18n.CustomMessageResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class TemplateUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(TemplateUtils.class);
    
    protected static String templatesDirectory;
    public static String getTemplatesDirectory(ServletContext servletContext) throws Exception {
        if (templatesDirectory == null) {
            URL templatesDirectoryUrl = TemplateUtils.class.getClassLoader().getResource("../templates");
            if (templatesDirectoryUrl == null) {
                templatesDirectoryUrl = servletContext.getResource("/WEB-INF/templates");
            }
            URI templatesURI = templatesDirectoryUrl.toURI();
            templatesDirectory = new File(templatesURI).getAbsolutePath() + File.separatorChar;
        }
        return templatesDirectory;
    }
    
    protected static TemplateEngine engine = null;
    public static TemplateEngine getTemplateEngine(ServletContext servletContext) throws Exception {
//        if (engine == null) {
            TemplateResolver resolver = new FileTemplateResolver();
            resolver.setCharacterEncoding("UTF-8");
            resolver.setTemplateMode("HTML5");
            resolver.setPrefix(TemplateUtils.getTemplatesDirectory(servletContext));
            resolver.setSuffix(".tpl");
            
            TemplateEngine templateEngine = new TemplateEngine();
            templateEngine.setTemplateResolver(resolver);
            templateEngine.setMessageResolver(new CustomMessageResolver());
            templateEngine.addDialect(new DataAttributeDialect());
            
            engine = templateEngine;
//        }
        return engine;
    }

}
