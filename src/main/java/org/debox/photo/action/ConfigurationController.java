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
package org.debox.photo.action;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import org.debox.photo.model.Configuration;
import org.debox.photo.server.ApplicationContext;
import org.debox.photo.util.StringUtils;
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.render.Render;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class ConfigurationController extends WebMotionController {

    public Render getConfiguration() {
        Configuration configuration = ApplicationContext.getInstance().getConfiguration();
        return renderJSON(configuration.get());
    }

    public Render editConfiguration(String title, String sourceDirectory, String targetDirectory) throws IOException, SQLException {
        Path source = Paths.get(sourceDirectory);
        Path target = Paths.get(targetDirectory);

        boolean error = false;
        if (!Files.isDirectory(source)) {
            getContext().addErrorMessage("source", "source.notdirectory");
            error = true;
        }

        if (!Files.exists(source)) {
            getContext().addErrorMessage("source", "source.notexist");
            error = true;
        }

        if (source.equals(target)) {
            getContext().addErrorMessage("path", "paths.equals");
            error = true;
        }

        if (StringUtils.isEmpty(title)) {
            getContext().addErrorMessage("name", "isEmpty");
            error = true;
        }

        if (error) {
            return renderStatus(500);
        }

        getContext().addInfoMessage("success", "configuration.edit.success");

        Configuration configuration = new Configuration();
        configuration.set(Configuration.Key.SOURCE_PATH, sourceDirectory);
        configuration.set(Configuration.Key.TARGET_PATH, targetDirectory);
        configuration.set(Configuration.Key.TITLE, title);
        ApplicationContext.getInstance().saveConfiguration(configuration);

        return renderJSON(configuration.get());
    }
    
}
