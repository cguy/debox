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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.debox.photo.dao.AlbumDao;
import org.debox.photo.dao.TokenDao;
import org.debox.photo.dao.UserDao;
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
    protected static UserDao userDao = new UserDao();

    public Render authenticate(String username, String password) {
        Subject currentUser = SecurityUtils.getSubject();

        // Authenticating user must be a guest
        if (currentUser.isAuthenticated()) {
            return renderStatus(HttpURLConnection.HTTP_FORBIDDEN);
        }

        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        try {
            currentUser.login(token);

        } catch (UnknownAccountException | IncorrectCredentialsException e) {
            logger.error(e.getMessage(), e);
            return renderError(HttpURLConnection.HTTP_UNAUTHORIZED, "");

        } catch (AuthenticationException e) {
            logger.error(e.getMessage(), e);
            return renderError(HttpURLConnection.HTTP_UNAUTHORIZED, "");
        }

        User user = (User) currentUser.getPrincipal();
        return renderJSON(user.getUsername());
    }

    public Render editCredentials(String id, String username, String oldPassword, String password, String confirm) {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            return renderError(HttpURLConnection.HTTP_FORBIDDEN, "");
        }

        try {
            User user = (User) subject.getPrincipal();

            boolean oldCredentialsChecked = userDao.checkCredentials(user.getUsername(), oldPassword);
            if (!oldCredentialsChecked) {
                return renderError(HttpURLConnection.HTTP_INTERNAL_ERROR, null);
            }

            user.setUsername(username);
            user.setPassword(password);

            userDao.save(user);

            return renderJSON("username", username);

        } catch (SQLException ex) {
            logger.error(ex.getMessage(), ex);
            return renderError(HttpURLConnection.HTTP_INTERNAL_ERROR, null);
        }
    }

    public Render logout() {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            return renderStatus(HttpURLConnection.HTTP_FORBIDDEN);
        }

        try {
            subject.logout();

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return renderJSON(true);
    }

    public Render getSyncProgress() throws SQLException {
        if (!SecurityUtils.getSubject().isAuthenticated()) {
            return renderStatus(HttpURLConnection.HTTP_FORBIDDEN);
        }

        if (syncJob == null) {
            return renderStatus(404);
        }
        return renderJSON(getSyncData());
    }

    public Render editConfiguration(String title, String sourceDirectory, String targetDirectory) throws IOException, SQLException {
        if (!SecurityUtils.getSubject().isAuthenticated()) {
            return renderStatus(HttpURLConnection.HTTP_FORBIDDEN);
        }

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

        return renderJSON("configuration", configuration.get());
    }

    public Render synchronize(String mode) {
        if (!SecurityUtils.getSubject().isAuthenticated()) {
            return renderStatus(HttpURLConnection.HTTP_FORBIDDEN);
        }

        SynchronizationMode syncMode = SynchronizationMode.valueOf(StringUtils.upperCase(mode));
        if (syncMode == null) {
            return renderError(HttpURLConnection.HTTP_INTERNAL_ERROR, "Unable to handle mode: " + mode);
        }

        try {
            Configuration configuration = ApplicationContext.getInstance().getConfiguration();

            String strSource = configuration.get(Configuration.Key.SOURCE_PATH);
            String strTarget = configuration.get(Configuration.Key.TARGET_PATH);

            if (StringUtils.isEmpty(strSource) || StringUtils.isEmpty(strTarget)) {
                return renderError(HttpURLConnection.HTTP_CONFLICT, "Work paths are not defined.");
            }

            Path source = Paths.get(strSource);
            Path target = Paths.get(strTarget);

            if (syncJob == null) {
                syncJob = new SyncJob(source, target, syncMode);
                syncJob.process();

            } else if (!syncJob.getSource().equals(source) || !syncJob.getTarget().equals(target)) {
                logger.warn("Aborting sync between {} and {}", syncJob.getSource(), syncJob.getTarget());
                syncJob.abort();
                syncJob.setSource(source);
                syncJob.setTarget(target);
                syncJob.setMode(syncMode);
                syncJob.process();

            } else if (!syncJob.isTerminated()) {
                logger.warn("Cannot launch process, it is already running");
            } else {
                syncJob.setMode(syncMode);
                syncJob.process();
            }

            return renderStatus(HttpURLConnection.HTTP_OK);

        } catch (SQLException | IOException ex) {
            logger.error(ex.getMessage(), ex);
            return renderError(HttpURLConnection.HTTP_INTERNAL_ERROR, "Unable to synchronize directories");
        }
    }

    public Render cancelSynchronization() {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            return renderError(HttpURLConnection.HTTP_FORBIDDEN, "");
        }

        if (syncJob != null) {
            syncJob.abort();
            return renderStatus(HttpURLConnection.HTTP_OK);
        }
        return renderError(HttpURLConnection.HTTP_NOT_FOUND, "Error during cancel.");
    }

    public Render getData() throws SQLException {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            return renderError(HttpURLConnection.HTTP_FORBIDDEN, "");
        }

        String username = ((User) subject.getPrincipal()).getUsername();

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
        long total = syncJob.getPhotosToProcess();
        long current = syncJob.getTerminatedProcessesCount();
        Map<String, Long> sync = new HashMap<>();
        sync.put("total", total);
        sync.put("current", current);
        if (total == 0L) {
            sync.put("percent", 100L);
        } else {
            sync.put("percent", Double.valueOf(Math.floor(current * 100 / total)).longValue());
        }
        return sync;
    }

    public Render getUploadProgress(FileProgressListener listener) {
        return renderJSON(listener);
    }

    public Render handleThumbnailsArchive(String albumId, UploadFile file) {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            return renderError(HttpURLConnection.HTTP_FORBIDDEN, "");
        }

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
