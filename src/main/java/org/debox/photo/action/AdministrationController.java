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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.debox.photo.dao.*;
import org.debox.photo.job.SyncJob;
import org.debox.photo.model.Album;
import org.debox.photo.model.Configuration;
import org.debox.photo.model.Token;
import org.debox.photo.util.StringUtils;
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.render.Render;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class AdministrationController extends WebMotionController {

    private static final Logger logger = LoggerFactory.getLogger(AdministrationController.class);
    protected SyncJob syncJob;
    protected static ConfigurationDao configurationDao = new ConfigurationDao();
    protected static AlbumDao albumDao = new AlbumDao();
    protected static PhotoDao photoDao = new PhotoDao();
    protected static TokenDao tokenDao = new TokenDao();

    public Render authenticate(String username, String password) {
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        Subject currentUser = SecurityUtils.getSubject();
        try {
            currentUser.login(token);

        } catch (UnknownAccountException | IncorrectCredentialsException e) {
            logger.error(e.getMessage(), e);
            return renderError(HttpServletResponse.SC_UNAUTHORIZED, "");

        } catch (AuthenticationException e) {
            logger.error(e.getMessage(), e);
            return renderError(HttpServletResponse.SC_UNAUTHORIZED, "");
        }

        return renderJSON(currentUser.getPrincipal());
    }

    public Render logout() {
        try {
            Subject subject = SecurityUtils.getSubject();
            subject.logout();

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return renderJSON(true);
    }

    public Render getSyncProgress() throws SQLException {
        if (syncJob == null) {
            return renderStatus(404);
        }
        return renderJSON(getSyncData());
    }

    public Render createToken(String label) throws SQLException {
        Subject currentUser = SecurityUtils.getSubject();
        if (!currentUser.isAuthenticated()) {
            return renderError(HttpServletResponse.SC_FORBIDDEN, "User must be logged in.");
        }

        Token token = new Token();
        token.setId(StringUtils.randomUUID());
        token.setLabel(label);

        tokenDao.save(token);

        return renderJSON(token);
    }

    public Render editConfiguration(String title, String sourceDirectory, String targetDirectory, Boolean force) throws IOException, SQLException {
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
        configurationDao.save(configuration);

        return renderJSON("configuration", configuration.get());
    }

    public Render synchronize(Boolean force) {
        try {
            Configuration configuration = configurationDao.get();
            
            String strSource = configuration.get(Configuration.Key.SOURCE_PATH);
            String strTarget = configuration.get(Configuration.Key.TARGET_PATH);
            
            if (StringUtils.isEmpty(strSource) || StringUtils.isEmpty(strTarget)) {
                return renderError(HttpServletResponse.SC_CONFLICT, "Work paths are not defined.");
            }
            
            Path source = Paths.get(strSource);
            Path target = Paths.get(strTarget);

            if (force == null) {
                force = false;
            }
            
            if (syncJob == null) {
                syncJob = new SyncJob(source, target, force);
                syncJob.process();

            } else if (!syncJob.getSource().equals(source) || !syncJob.getTarget().equals(target)) {
                logger.warn("Aborting sync between {} and {}", syncJob.getSource(), syncJob.getTarget());
                syncJob.abort();
                syncJob.setSource(source);
                syncJob.setTarget(target);
                syncJob.setForceThumbnailsRegeneration(force);
                syncJob.process();

            } else if (!syncJob.isTerminated()) {
                logger.warn("Cannot launch process, it is already running");
            } else {
                syncJob.setForceThumbnailsRegeneration(force);
                syncJob.process();
            }

            return renderStatus(HttpServletResponse.SC_OK);

        } catch (SQLException | IOException ex) {
            logger.error(ex.getMessage(), ex);
            return renderError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to synchronize directories");
        }
    }

    public Render getData() throws SQLException {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            return renderError(HttpServletResponse.SC_FORBIDDEN, "");
        }

        if (syncJob != null && !syncJob.isTerminated()) {
            Map<String, Long> sync = getSyncData();
            return renderJSON(
                    "username", subject.getPrincipal(),
                    "configuration", configurationDao.get().get(),
                    "albums", albumDao.getAlbums(),
                    "tokens", tokenDao.getAll(),
                    "sync", sync);
        }

        return renderJSON(
                "username", subject.getPrincipal(),
                "configuration", configurationDao.get().get(),
                "albums", albumDao.getAlbums(),
                "tokens", tokenDao.getAll());
    }

    public Render getToken(String id) throws SQLException {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            return renderError(HttpServletResponse.SC_FORBIDDEN, "");
        }

        Token token = tokenDao.getById(id);
        if (token == null) {
            return renderError(HttpServletResponse.SC_NOT_FOUND, "");
        }

        return renderJSON(
                "albums", albumDao.getAlbums(),
                "token", token);
    }

    public Render editToken(String id, String label, List<String> albums) throws SQLException {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            return renderError(HttpServletResponse.SC_FORBIDDEN, "");
        }

        Token token = tokenDao.getById(id);
        if (token == null) {
            return renderError(HttpServletResponse.SC_NOT_FOUND, "");
        }

        token.setLabel(label);
        for (Object albumId : albums) {
            Album album = new Album();
            album.setId((String) albumId);
            token.getAlbums().add(album);
        }

        tokenDao.save(token);

        return renderJSON(token);
    }

    public Render editCredentials(String username, String oldPassword, String password, String confirm) {
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        Subject currentUser = SecurityUtils.getSubject();

        // TODO

        UserDao userDao = new UserDao();
        userDao.getCredentialsMatcher().doCredentialsMatch(token, null);

        return renderStatus(HttpServletResponse.SC_OK);
    }

    protected Map<String, Long> getSyncData() throws SQLException {
        long total = photoDao.getPhotosCount();
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
