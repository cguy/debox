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

import java.io.File;
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
import org.debox.photo.model.user.User;
import org.debox.photo.server.ApplicationContext;
import org.debox.photo.server.renderer.JacksonRenderJsonImpl;
import org.debox.photo.util.FileUtils;
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
    
    // TODO [cguy:2012-11-28] Handle error cases (in these case, delete original file, thumbnails, rollback DB transactions)
    public Render upload(UploadFile photo, String albumId) throws IOException, SQLException {
        // Get existing album
        Album album = albumDao.getAlbum(albumId);
        if (album == null) {
            return renderError(HttpURLConnection.HTTP_NOT_FOUND, "Album identifier " + albumId + " is not corresponding with any existing album.");
        }
        
        Path originalFile = Paths.get(photo.getFile().getAbsolutePath());
        
        Photo addedPhoto = new Photo();
        addedPhoto.setAlbumId(albumId);
        addedPhoto.setFilename(photo.getName());
        addedPhoto.setTitle(photo.getName());
        addedPhoto.setDate(ImageUtils.getShootingDate(originalFile));
        addedPhoto.setId(StringUtils.randomUUID());
        addedPhoto.setRelativePath(album.getRelativePath());
        
        Path targetFile = Paths.get(ImageUtils.getSourcePath(addedPhoto));
        
        logger.debug("Copy {} to {}", originalFile, targetFile);
        Files.move(originalFile, targetFile);
        
        String thumbnailPath = ImageUtils.getThumbnailPath(addedPhoto, ThumbnailSize.LARGE);
        if (!ImageUtils.thumbnail(targetFile.toString(), thumbnailPath, ThumbnailSize.LARGE)) {
            FileUtils.deleteQuietly(targetFile.toFile());
            return renderError(HttpURLConnection.HTTP_INTERNAL_ERROR, "Unable to create large thumbnail for photo " + photo.getName());
        }
        
        String squarePath = ImageUtils.getThumbnailPath(addedPhoto, ThumbnailSize.SQUARE);
        if (!ImageUtils.thumbnail(targetFile.toString(), squarePath.toString(), ThumbnailSize.SQUARE)) {
            FileUtils.deleteQuietly(new File(thumbnailPath));
            FileUtils.deleteQuietly(targetFile.toFile());
            return renderError(HttpURLConnection.HTTP_INTERNAL_ERROR, "Unable to create square thumbnail for photo " + photo.getName());
        }
        
        try {
            photoDao.save(addedPhoto); // Handle photo count increment for album
            
            album = albumDao.getAlbum(albumId); // Get refreshed version on the album modified in photoDao.save method
            boolean edited = false;
            Date date = ImageUtils.getShootingDate(targetFile);
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
        } catch (SQLException ex) {
            FileUtils.deleteQuietly(new File(thumbnailPath));
            FileUtils.deleteQuietly(new File(squarePath));
            FileUtils.deleteQuietly(targetFile.toFile());
            throw ex;
        }
        
        Photo result = photoDao.getPhoto(addedPhoto.getId());
        HashMap<String, Object> metaData = new HashMap<>(4);
        metaData.put("name", photo.getName());
        metaData.put("size", photo.getSize());
        metaData.put("url", result.getUrl());
        metaData.put("thumbnail_url", result.getThumbnailUrl());

        HashMap<String, Object> resultMetadata = new HashMap<>(1);
        resultMetadata.put(null, new Object[] {metaData});
        
        return new JacksonRenderJsonImpl(resultMetadata);
    }

    public Render synchronize(String mode, boolean forceCheckDates) throws SQLException {
        SynchronizationMode syncMode = SynchronizationMode.valueOf(StringUtils.upperCase(mode));
        if (syncMode == null) {
            return renderError(HttpURLConnection.HTTP_INTERNAL_ERROR, "Unable to handle mode: " + mode);
        }

        String strSource = ImageUtils.getAlbumsBasePath();
        String strTarget = ImageUtils.getThumbnailsBasePath();
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
        User user = SessionUtils.getUser(SecurityUtils.getSubject());
        
        if (syncJob != null && !syncJob.isTerminated()) {
            Map<String, Long> sync = getSyncData();
            return renderJSON(
                    "username", username,
                    "configuration", ApplicationContext.getInstance().getOverallConfiguration().get(),
                    "albums", albumDao.getAllAlbums(),
                    "tokens", tokenDao.getAll(user.getId()),
                    "sync", sync);
        }

        return renderJSON(
                "username", username,
                "configuration", ApplicationContext.getInstance().getOverallConfiguration().get(),
                "albums", albumDao.getAllAlbums(),
                "tokens", tokenDao.getAll(user.getId()));
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
