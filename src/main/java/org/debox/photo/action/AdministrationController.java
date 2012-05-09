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
import java.net.HttpURLConnection;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.shiro.SecurityUtils;
import org.debox.photo.dao.AlbumDao;
import org.debox.photo.dao.TokenDao;
import org.debox.photo.job.SyncJob;
import org.debox.photo.model.*;
import org.debox.photo.server.ApplicationContext;
import org.debox.photo.util.FileUtils;
import org.debox.photo.util.StringUtils;
import org.debux.webmotion.server.call.FileProgressListener;
import org.debux.webmotion.server.call.UploadFile;
import org.debux.webmotion.server.render.Render;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class AdministrationController extends DeboxController {

    private static final Logger logger = LoggerFactory.getLogger(AdministrationController.class);
    protected SyncJob syncJob;
    protected static AlbumDao albumDao = new AlbumDao();
    protected static TokenDao tokenDao = new TokenDao();
    protected ExecutorService threadPool = Executors.newSingleThreadExecutor();

    public Render getSyncProgress() throws SQLException {
        if (syncJob == null) {
            return renderStatus(404);
        }
        return renderJSON(getSyncData());
    }

    public Render synchronize(String mode, boolean forceCheckDates) {
        SynchronizationMode syncMode = SynchronizationMode.valueOf(StringUtils.upperCase(mode));
        if (syncMode == null) {
            return renderError(HttpURLConnection.HTTP_INTERNAL_ERROR, "Unable to handle mode: " + mode);
        }

        Configuration configuration = ApplicationContext.getInstance().getConfiguration();

        String strSource = configuration.get(Configuration.Key.SOURCE_PATH);
        String strTarget = configuration.get(Configuration.Key.TARGET_PATH);

        if (StringUtils.isEmpty(strSource) || StringUtils.isEmpty(strTarget)) {
            return renderError(HttpURLConnection.HTTP_CONFLICT, "Work paths are not defined.");
        }

        Path source = Paths.get(strSource);
        Path target = Paths.get(strTarget);

        if (syncJob != null && !syncJob.isTerminated()) {
            logger.warn("Cannot launch process, it is already running");
        } else {
            if (syncJob == null) {
                syncJob = new SyncJob(source, target, syncMode, forceCheckDates);

            } else if (!syncJob.getSource().equals(source) || !syncJob.getTarget().equals(target)) {
                logger.warn("Aborting sync between {} and {}", syncJob.getSource(), syncJob.getTarget());
                syncJob.abort();
                syncJob.setSource(source);
                syncJob.setTarget(target);
                syncJob.setMode(syncMode);
                syncJob.setForceCheckDates(forceCheckDates);

            } else {
                syncJob.setMode(syncMode);
                syncJob.setForceCheckDates(forceCheckDates);
            }

            threadPool.execute(syncJob);
        }

        return renderStatus(HttpURLConnection.HTTP_OK);
    }

    public Render cancelSynchronization() {
        if (syncJob != null) {
            syncJob.abort();
            return renderStatus(HttpURLConnection.HTTP_OK);
        }
        return renderError(HttpURLConnection.HTTP_NOT_FOUND, "Error during cancel.");
    }

    public Render getData() throws SQLException {
        String username = ((User) SecurityUtils.getSubject().getPrincipal()).getUsername();
        if (syncJob != null && !syncJob.isTerminated()) {
            Map<String, Long> sync = getSyncData();
            return renderJSON(
                    "username", username,
                    "configuration", ApplicationContext.getInstance().getConfiguration().get(),
                    "albums", albumDao.getAlbums(),
                    "tokens", tokenDao.getAll(),
                    "sync", sync);
        }

        return renderJSON(
                "username", username,
                "configuration", ApplicationContext.getInstance().getConfiguration().get(),
                "albums", albumDao.getAlbums(),
                "tokens", tokenDao.getAll());
    }

    protected Map<String, Long> getSyncData() throws SQLException {
        long total = syncJob.getNumberToProcess();
        long current = syncJob.getNumberProcessed();
        Map<String, Long> sync = new HashMap<>();
        sync.put("total", total);
        sync.put("current", current);
        if (total == 0L && syncJob.isTerminated()) {
            sync.put("percent", 100L);
        } else if (total == 0L && !syncJob.isTerminated()) {
            sync.put("percent", 0L);
        } else {
            sync.put("percent", Double.valueOf(Math.floor(current * 100 / total)).longValue());
        }
        return sync;
    }

    public Render getUploadProgress(FileProgressListener listener) {
        return renderJSON(listener);
    }

    public Render handleThumbnailsArchive(String albumId, UploadFile file) {
        Album album = null;
        try {
            album = albumDao.getAlbum(albumId);
        } catch (SQLException ex) {
            logger.error("Unable to get album from database", ex);
        }
        if (album == null) {
            renderError(HttpURLConnection.HTTP_NOT_FOUND, "Album not found");
        }
        
        if (file == null) {
            return renderError(HttpURLConnection.HTTP_INTERNAL_ERROR, "File must not be null.");
        }

        Configuration configuration = ApplicationContext.getInstance().getConfiguration();
        String targetPath = configuration.get(Configuration.Key.TARGET_PATH) + album.getRelativePath();

        try {
            FileUtils.unzipArchiveToDirectory(file.getFile().getAbsolutePath(), targetPath);
        } catch (IOException ex) {
            logger.error("Unable to extract archive", ex);
        }

        try {
            List<Photo> photos = photoDao.getPhotos(albumId);
            File target = new File(targetPath);
            for (File current : target.listFiles()) {
                if (current.isDirectory()) {
                    continue;
                }
                boolean ok = false;
                for (Photo photo : photos) {
                    for (ThumbnailSize size : ThumbnailSize.values()) {
                        String thumbnailName = size.getPrefix() + photo.getName();
                        if (thumbnailName.equals(current.getName())) {
                            ok = true;
                        }
                    }
                }
                if (!ok) {
                    current.delete();
                }
            }
        } catch (SQLException ex) {
            logger.error("Unable to clean target directory", ex);
        }

        // Force reload uploadFrame
        return renderContent(null, null);
    }

}
