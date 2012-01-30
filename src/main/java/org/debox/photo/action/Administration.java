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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.debox.photo.dao.ApplicationConfigurationDao;
import org.debox.photo.dao.MediaDao;
import org.debox.photo.job.SyncJob;
import org.debox.photo.model.Album;
import org.debox.photo.model.Configuration;
import org.debox.photo.model.ApplicationContext;
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.render.Render;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class Administration extends WebMotionController {

    private static final Logger logger = LoggerFactory.getLogger(Administration.class);
    protected SyncJob syncJob;
    protected MediaDao mediaDao = new MediaDao();
    protected ApplicationConfigurationDao configurationDao = new ApplicationConfigurationDao();

    public Render getSyncProgress() throws SQLException {
        if (syncJob == null) {
            return renderStatus(404);
        }
        return renderJSON(getSyncData());
    }

    public Render editConfiguration(String sourceDirectory, String targetDirectory) throws IOException, SQLException {
        Path source = Paths.get(sourceDirectory);
        Path target = Paths.get(targetDirectory);

        boolean error = false;
        if (!Files.isDirectory(source) || !Files.exists(source)) {
            getContext().addErrorMessage("source", "source.error");
            error = true;
        }

        if (source.equals(target)) {
            getContext().addErrorMessage("path", "paths.equals");
            error = true;
        }

        if (error) {
            return renderStatus(500);
        }

        getContext().addInfoMessage("success", "configuration.edit.success");

        if (syncJob == null) {
            syncJob = new SyncJob(source, target);
            syncJob.process();

        } else if (!syncJob.getSource().equals(source) || !syncJob.getTarget().equals(target)) {
            logger.warn("Aborting sync between {} and {}", syncJob.getSource(), syncJob.getTarget());
            syncJob.abort();
            syncJob.setSource(source);
            syncJob.setTarget(target);
            syncJob.process();

        } else if (!syncJob.isTerminated()) {
            logger.warn("Cannot launch process, it is already running");
        } else {
            syncJob.process();
        }

        Configuration configuration = new Configuration();
        configuration.set(Configuration.Key.SOURCE_PATH, sourceDirectory);
        configuration.set(Configuration.Key.TARGET_PATH, targetDirectory);
        configurationDao.save(configuration);

        return renderJSON("configuration", configuration);
    }

    public Render getData() throws SQLException {
        if (syncJob != null && !syncJob.isTerminated()) {
            Map<String, Long> sync = getSyncData();
            return renderJSON(
                    "configuration", configurationDao.get().get(),
                    "albums", mediaDao.getAllAlbums(),
                    "sync", sync);
        }

        return renderJSON(
                "configuration", configurationDao.get().get(),
                "albums", mediaDao.getAllAlbums());
    }

    protected Map<String, Long> getSyncData() throws SQLException {
        long total = mediaDao.getPhotosCount();
        long current = syncJob.getTerminatedProcessesCount();
        Map<String, Long> sync = new HashMap<>();
        sync.put("total", total);
        sync.put("current", current);
        if (total == 0L) {
            sync.put("percent", 0L);
        } else {
            sync.put("percent", Integer.valueOf(Math.round(current * 100 / total)).longValue());
        }
        return sync;
    }
}
