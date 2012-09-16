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
package org.debox.photo.service;

import org.debox.photo.model.configuration.ThumbnailSize;
import org.debox.photo.model.configuration.SynchronizationMode;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.shiro.SecurityUtils;
import org.debox.imaging.ImageUtils;
import org.debox.photo.dao.AlbumDao;
import org.debox.photo.dao.TokenDao;
import org.debox.photo.job.SyncJob;
import org.debox.photo.model.*;
import org.debox.photo.server.ApplicationContext;
import org.debox.photo.server.renderer.JacksonRenderJsonImpl;
import org.debox.photo.util.SessionUtils;
import org.debox.photo.util.StringUtils;
import org.debux.webmotion.server.call.FileProgressListener;
import org.debux.webmotion.server.call.UploadFile;
import org.debux.webmotion.server.render.Render;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class AdministrationService extends DeboxService {

    private static final Logger logger = LoggerFactory.getLogger(AdministrationService.class);
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
    
    public Render upload(UploadFile photo, String albumId) throws IOException, SQLException {
        // Get existing album
        Album album = albumDao.getAlbum(albumId);
        if (album == null) {
            return renderError(HttpURLConnection.HTTP_NOT_FOUND, "Album identifier " + albumId + " is not corresponding with any existing album.");
        }
        
        Configuration configuration = ApplicationContext.getInstance().getConfiguration();
        
        String basePath = configuration.get(Configuration.Key.SOURCE_PATH);
        Path targetFile = Paths.get(basePath + album.getRelativePath(), photo.getName());
        Path originalFile = Paths.get(photo.getFile().getAbsolutePath());
        
        logger.debug("Copy {} to {}", originalFile, targetFile);
        Files.move(originalFile, targetFile);
        
        Photo addedPhoto = new Photo();
        addedPhoto.setAlbumId(albumId);
        addedPhoto.setFilename(photo.getName());
        addedPhoto.setTitle(photo.getName());
        addedPhoto.setDate(ImageUtils.getShootingDate(targetFile));
        addedPhoto.setId(StringUtils.randomUUID());
        addedPhoto.setRelativePath(album.getRelativePath());
        
        photoDao.save(addedPhoto); // Handle photo count increment for album
        
        String thumbnailPath = ImageUtils.getTargetPath(configuration.get(Configuration.Key.TARGET_PATH), addedPhoto, ThumbnailSize.LARGE);
        ImageUtils.thumbnail(targetFile.toString(), thumbnailPath, ThumbnailSize.LARGE);
        
        thumbnailPath = ImageUtils.getTargetPath(configuration.get(Configuration.Key.TARGET_PATH), addedPhoto, ThumbnailSize.SQUARE);
        ImageUtils.thumbnail(targetFile.toString(), thumbnailPath.toString(), ThumbnailSize.SQUARE);
        
        Photo result = photoDao.getPhoto(addedPhoto.getId());
        Date date = ImageUtils.getShootingDate(targetFile);
        album = albumDao.getAlbum(albumId); // Get refreshed version on the album modified in photoDao.save method
        boolean edited = false;
        if (album.getBeginDate() == null || album.getBeginDate().after(date)) {
            album.setBeginDate(date);
            edited = true;
        }
        if (album.getEndDate() == null || album.getEndDate().before(date)) {
            album.setEndDate(date);
            edited = true;
        }
        if (edited) {
            albumDao.save(album);
        }
        
        final HashMap<String, Object> metaData = new HashMap<>();
        if (photo != null) {
            metaData.put("name", photo.getName());
            metaData.put("size", photo.getSize());
            metaData.put("url", result.getUrl());
            metaData.put("thumbnail_url", result.getThumbnailUrl());
        }

        HashMap<String, Object> resultMetadata = new HashMap<>();
        resultMetadata.put(null, new Object[]{metaData});
        
        return new JacksonRenderJsonImpl(resultMetadata);
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
                syncJob.setOwnerId(SessionUtils.getUser(SecurityUtils.getSubject()).getId());

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

        return renderStatus(HttpURLConnection.HTTP_NO_CONTENT);
    }

    public Render cancelSynchronization() {
        if (syncJob != null) {
            syncJob.abort();
            return renderStatus(HttpURLConnection.HTTP_NO_CONTENT);
        }
        return renderError(HttpURLConnection.HTTP_NOT_FOUND, "Error during cancel.");
    }

    public Render getData() throws SQLException {
        String username = HomeService.getUsername();
        
        if (syncJob != null && !syncJob.isTerminated()) {
            Map<String, Long> sync = getSyncData();
            return renderJSON(
                    "username", username,
                    "configuration", ApplicationContext.getInstance().getConfiguration().get(),
                    "albums", albumDao.getAllAlbums(),
                    "tokens", tokenDao.getAll(),
                    "sync", sync);
        }

        return renderJSON(
                "username", username,
                "configuration", ApplicationContext.getInstance().getConfiguration().get(),
                "albums", albumDao.getAllAlbums(),
                "tokens", tokenDao.getAll());
    }

    public Render getUploadProgress(FileProgressListener listener) {
        return renderJSON(listener);
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

}
