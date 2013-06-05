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

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.shiro.SecurityUtils;
import org.debox.connector.api.exception.AuthenticationProviderException;
import org.debox.imaging.ImageUtils;
import org.debox.photo.dao.AlbumDao;
import org.debox.photo.dao.CommentDao;
import org.debox.photo.dao.PermissionDao;
import org.debox.photo.dao.UserDao;
import org.debox.photo.dao.VideoDao;
import org.debox.photo.job.RegenerateThumbnailsJob;
import org.debox.photo.model.Album;
import org.debox.photo.model.comment.Comment;
import org.debox.photo.model.Datable;
import org.debox.photo.model.Media;
import org.debox.photo.model.user.Contact;
import org.debox.photo.model.Photo;
import org.debox.photo.model.Provider;
import org.debox.photo.model.user.ThirdPartyAccount;
import org.debox.photo.model.configuration.ThumbnailSize;
import org.debox.photo.model.Video;
import org.debox.photo.model.user.AnonymousUser;
import org.debox.photo.model.user.DeboxPermission;
import org.debox.photo.server.renderer.ZipDownloadRenderer;
import org.debox.photo.thirdparty.ServiceUtil;
import org.debox.photo.util.DatableComparator;
import org.debox.photo.util.SessionUtils;
import org.debox.photo.util.StringUtils;
import org.debux.webmotion.server.render.Render;
import org.debux.webmotion.server.render.RenderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class AlbumService extends DeboxService {

    private static final Logger logger = LoggerFactory.getLogger(AlbumService.class);
    
    protected static AlbumDao albumDao = new AlbumDao();
    protected static CommentDao commentDao = new CommentDao();
    protected static VideoDao videoDao = new VideoDao();
    protected static UserDao userDao = new UserDao();
    protected static PermissionDao permissionDao = new PermissionDao();
    
    
    protected RegenerateThumbnailsJob regenerateThumbnailsJob;
    protected ExecutorService threadPool = Executors.newSingleThreadExecutor();
    
    public Render createAlbum(String albumName, String parentId) throws SQLException {
        if (StringUtils.isEmpty(albumName)) {
            return renderError(HttpURLConnection.HTTP_INTERNAL_ERROR, "The name of the album is mandatory.");
        }
        Album album = new Album();
        album.setId(StringUtils.randomUUID());
        album.setName(albumName);
        album.setPublic(false);
        album.setPhotosCount(0);
        album.setDownloadable(false);
        album.setOwnerId(SessionUtils.getUserId());
        
        if (StringUtils.isEmpty(parentId)) {
            album.setRelativePath(File.separatorChar + album.getName());
            
        } else {
            Album parent = albumDao.getAlbum(parentId);
            if (parent == null) {
                return renderError(HttpURLConnection.HTTP_INTERNAL_ERROR, "There is not any album with id " + parentId);
            }
            album.setParentId(parentId);
            album.setRelativePath(parent.getRelativePath() + File.separatorChar + album.getName());
        }
        
        Album existingAtPath = albumDao.getAlbumByPath(album.getRelativePath());
        if (existingAtPath != null) {
            return renderError(HttpURLConnection.HTTP_INTERNAL_ERROR, "There is already an album at path (" + album.getRelativePath() + ")");
        }
        
        String[] paths = {ImageUtils.getAlbumsBasePath(album.getOwnerId()), ImageUtils.getThumbnailsBasePath(album.getOwnerId())};
        for (String path : paths) {
            File targetDirectory = new File(path + album.getRelativePath());
            if (!targetDirectory.exists() && !targetDirectory.mkdirs()) {
                return renderError(HttpURLConnection.HTTP_INTERNAL_ERROR, "Error during directory creation (" + targetDirectory.getAbsolutePath() + ")");
            }
        }
        
        albumDao.save(album);
        return renderJSON(album);
    }
    
    public Render deleteAlbum(String albumId) throws SQLException {
        Album album = albumDao.getAlbum(albumId);
        if (album == null) {
            return renderError(HttpURLConnection.HTTP_NOT_FOUND, "There is not any album with id " + albumId);
        }
        
        String originalDirectory = ImageUtils.getSourcePath(album);
        String workingDirectory = ImageUtils.getTargetPath(album);
        if (!FileUtils.deleteQuietly(new File(workingDirectory)) || !FileUtils.deleteQuietly(new File(originalDirectory))) {
            return renderError(HttpURLConnection.HTTP_INTERNAL_ERROR, "Unable to delete directories from file system.");
        }

        albumDao.delete(album);
        return renderStatus(HttpURLConnection.HTTP_NO_CONTENT);
    }
    
    public Render getAlbums(String parentId, String criteria) throws SQLException {
        return renderJSON("albums", albums(parentId, criteria));
    }
    
    protected List<Album> albums(String parentId) throws SQLException {
        return albums(parentId, null);
    }
    
    protected List<Album> albums(String parentId, String criteria) throws SQLException {
        List<Album> albums;
        String userId = SessionUtils.getUserId();
        if ("all".equals(criteria)) {
            albums = albumDao.getAllAlbums(userId);
        } else {
            albums = albumDao.getAlbums(userId, parentId);
        }
        return albums;
    }

    public Render getAlbum(String albumId) throws IOException, SQLException, IllegalArgumentException, IOException, AuthenticationProviderException {

        Album album = albumDao.getAlbum(albumId);
        if (album == null) {
            return renderStatus(HttpURLConnection.HTTP_NOT_FOUND);
        }

        if (!SecurityUtils.getSubject().isPermitted(new DeboxPermission("album", "read", albumId)) && !album.isPublicAlbum()) {
            return renderStatus(HttpURLConnection.HTTP_FORBIDDEN);
        }
        
        List<Contact> contacts = new ArrayList<>();
        List<Contact> tokens = new ArrayList<>();
        
        if (SecurityUtils.getSubject().isPermitted(new DeboxPermission("album", "write", albumId))) {
            
            List<Pair<AnonymousUser, List<Album>>> anonymousUsers = userDao.getAllAnonymousUsersByCreator(SessionUtils.getUserId());
            List<AnonymousUser> authorizedUsers = userDao.getAllAnonymousUsersWithAccessToAlbum(album);
            tokens.addAll(convertAnonymousUsers(anonymousUsers));
            
            for (Contact anonymousUser : tokens) {
                for (AnonymousUser authorizedUser : authorizedUsers) {
                    if (anonymousUser.getId().equals(authorizedUser.getId())) {
                        anonymousUser.setAuthorized(true);
                        break;
                    }
                }
            }
                
            List<ThirdPartyAccount> accounts = userDao.getThirdPartyAccounts(SessionUtils.getUser());
            for (ThirdPartyAccount account : accounts) {
                String thirdPartyToken = account.getToken();
                if (thirdPartyToken != null) {
                    switch (account.getProviderId()) {
                        case "facebook":
                            DefaultFacebookClient client = new DefaultFacebookClient(thirdPartyToken);
                            Connection<com.restfb.types.User> myFriends = client.fetchConnection("me/friends", com.restfb.types.User.class);
                            contacts.addAll(convert(myFriends.getData()));
                            break;
                    }
                }
            }
            Collections.sort(contacts);
            
            List<ThirdPartyAccount> authorized = userDao.getAuthorizedThirdPartyAccounts(album);
            
            for (Contact contact : contacts) {
                for (ThirdPartyAccount account : authorized) {
                    if (contact.getProvider().getId().equals(account.getProviderId()) && contact.getId().equals(account.getProviderAccountId())) {
                        contact.setAuthorized(true);
                        break;
                    }
                }
            }
        }
        
        List<Album> subAlbums = albums(album.getId());
        List<Photo> photos = photoDao.getPhotos(albumId, SessionUtils.getUserId());
        List<Video> videos = videoDao.getVideos(albumId, SessionUtils.getUserId());
        List<Datable> medias = new ArrayList<>(photos.size() + videos.size());
        medias.addAll(photos);
        medias.addAll(videos);
        
        Collections.sort(medias, new DatableComparator());
        
        Album parent = albumDao.getAlbum(album.getParentId());
        List<Comment> comments = null;
        if (SecurityUtils.getSubject().isPermitted(new DeboxPermission("album", "read", albumId)) && !SessionUtils.isAnonymousUser()) {
            comments = commentDao.getByAlbum(album.getId());
        }
        
        return renderJSON("album", album, "albumParent", parent,
                "subAlbums", subAlbums, "medias", medias,
                "regeneration", getRegenerationData(), "contacts", contacts, "comments", comments, "tokens", tokens);
    }
    
    public List<Contact> convert(List<com.restfb.types.User> list) {
        if (list == null) {
            return null;
        }
        List<Contact> result = new ArrayList<>(list.size());
        for (com.restfb.types.User user : list) {
            Contact contact = new Contact();
            contact.setId(user.getId());
            contact.setProvider(ServiceUtil.getProvider("facebook"));
            contact.setName(user.getName());
            result.add(contact);
        }
        return result;
    }
    
    public List<Contact> convertAnonymousUsers(List<Pair<AnonymousUser, List<Album>>> list) {
        if (list == null) {
            return null;
        }
        List<Contact> result = new ArrayList<>(list.size());
        for (Pair<AnonymousUser, List<Album>> info : list) {
            Contact contact = new Contact();
            contact.setId(info.getKey().getId());
            contact.setName(info.getKey().getLabel());
            result.add(contact);
        }
        return result;
    }
    
    public Render editAlbum(String albumId, String name, String description, String visibility, String downloadable, List<String> authorizedTokens) throws SQLException, IOException, IllegalArgumentException, AuthenticationProviderException {
        if (!SecurityUtils.getSubject().isPermitted(new DeboxPermission("album", "write", albumId))) {
            return renderStatus(HttpURLConnection.HTTP_FORBIDDEN);
        }
        
        Album album = albumDao.getAlbum(albumId);
        if (album == null) {
            return renderStatus(HttpURLConnection.HTTP_NOT_FOUND);
        }
        if (name != null) {
            album.setName(name);
        }
        if (description != null) {
            album.setDescription(description);
        }
        if (visibility != null) {
            album.setPublic(Boolean.parseBoolean(visibility));
        }
        if (downloadable != null) {
            album.setDownloadable(Boolean.parseBoolean(downloadable));
        }

        albumDao.save(album);
        
        Set<String> users = new HashSet<>(); 
        if (authorizedTokens != null) {
            // Ensure that each id is corresponding with existing user
            for (String thirdPartyId : authorizedTokens) {
                String providerId = StringUtils.substringBefore(thirdPartyId, "-");
                Provider provider = ServiceUtil.getProvider(providerId);
                
                if (provider == null) {
                    AnonymousUser user = userDao.getAnonymousUser(thirdPartyId);
                    if (user != null) {
                        users.add(user.getId());
                    }
                } else {
                    String providerAccountId = StringUtils.substringAfter(thirdPartyId, "-");
                    ThirdPartyAccount account = userDao.getUser(provider.getId(), providerAccountId);
                    if (account == null) {
                        account = new ThirdPartyAccount(provider, providerAccountId, null);
                        
                        // Create new user (linked to third party provider) in database
                        userDao.save(account, userDao.getRole("user"));
                    }
                    users.add(account.getId());
                }
            }
        }

        Set<String> oldAuthorizedUserIdsRef = permissionDao.getAuthorizedUsers(new DeboxPermission("album", "read", album.getId()));

        // Get users which have been unauthorized to read current album 
        Set<String> toUnauthorizeUsers = new HashSet<>(oldAuthorizedUserIdsRef);
        toUnauthorizeUsers.removeAll(users);

        // Get new users which have been authorized to read current album 
        Set<String> toAuthorizeUsers = new HashSet<>(users);
        toAuthorizeUsers.removeAll(oldAuthorizedUserIdsRef);

        for (String toUnauthorizeUser : toUnauthorizeUsers) {
            permissionDao.deleteReadPermission(toUnauthorizeUser, album);
        }

        for (String toAuthorizeUser : toAuthorizeUsers) {
            permissionDao.saveReadPermission(toAuthorizeUser, album);
        }
        
        permissionDao.savePermission(album);
        return getAlbum(albumId);
    }
    
    public Render setAlbumCover(String albumId, String objectId) throws SQLException, IOException {
        boolean emptyId = StringUtils.isEmpty(objectId);
        boolean isPhoto = !emptyId && photoDao.getPhoto(objectId) != null;
        boolean isVideo = !emptyId && videoDao.getVideo(objectId) != null;
        boolean isSubAlbum = !emptyId && objectId.startsWith("a.") && albumDao.getAlbumCover(objectId.substring(2)) != null;
        
        if (!isPhoto && !isVideo && !isSubAlbum) {
            return renderError(HttpURLConnection.HTTP_INTERNAL_ERROR, "The objectId parameter must correspond with a valid media.");
        }
        
        String id;
        if (objectId.startsWith("a.")) {
            Media photo = albumDao.getAlbumCover(objectId.substring(2));
            id = photo.getId();
        } else {
            id = objectId;
        }
        
        photoDao.saveThumbnailGenerationTime("a." + albumId, ThumbnailSize.SQUARE, new Date().getTime());
        albumDao.setAlbumCover(albumId, id);
        return renderStatus(HttpURLConnection.HTTP_NO_CONTENT);
    }

    public Render getAlbumCover(String token, String albumId) throws SQLException, IOException {
        albumId = StringUtils.substringBeforeLast(albumId, "-cover.jpg");
        
        Album album = albumDao.getAlbum(albumId);
        if (album == null) {
            return renderError(HttpURLConnection.HTTP_NOT_FOUND, "");
        }
        
        Media media = null;
        if (SecurityUtils.getSubject().isPermitted(new DeboxPermission("album", "read", albumId)) || album.isPublicAlbum()) {
            media = albumDao.getAlbumCover(albumId);
        }

        if (media == null) {
            return renderRedirect("/img/default_album.png");
        }
        
        FileInputStream fis = null;
        try {
            fis = ImageUtils.getStream(media, ThumbnailSize.SQUARE);
            
        } catch (Exception ex) {
            logger.error("Unable to get stream", ex);
        }
        if (fis == null) {
            logger.error("Errror, stream is null for the photo " + media.getFilename());
            return renderRedirect("/img/default_album.png");
        }
        RenderStatus status = handleLastModifiedHeader(album);
        if (status.getCode() == HttpURLConnection.HTTP_NOT_MODIFIED) {
            return status;
        }
        return renderStream(fis, "image/jpeg");
    }

    public Render download(String albumId, boolean resized) throws SQLException {
        if (resized) {
            albumId = StringUtils.substringBefore(albumId, "-min.zip");
        } else {
            albumId = StringUtils.substringBefore(albumId, ".zip");
        }
        
        Album album = albumDao.getAlbum(albumId);
        if (album == null) {
            return renderStatus(HttpURLConnection.HTTP_NOT_FOUND);
        }

        boolean userCanDownload = SecurityUtils.getSubject().isPermitted("album:download:" + albumId);
        boolean isPublicAndDownloadableAlbum = album.isPublicAlbum() && album.isDownloadable();
        if (!userCanDownload && !isPublicAndDownloadableAlbum) {
            return renderStatus(HttpURLConnection.HTTP_FORBIDDEN);
        }

        if (resized) {
            List<Photo> photos = photoDao.getPhotos(album.getId());
            Map<String, String> names = new HashMap<>(photos.size());
            for (Photo photo : photos) {
                names.put(ThumbnailSize.LARGE.getPrefix() + photo.getFilename(), ThumbnailSize.LARGE.getPrefix() + photo.getFilename());
            }
            return new ZipDownloadRenderer(ImageUtils.getTargetPath(album), album.getName(), names);
        }
        return new ZipDownloadRenderer(ImageUtils.getSourcePath(album), album.getName());
    }

    public Render regenerateThumbnails(String albumId) throws SQLException {
        Album album = albumDao.getAlbum(albumId);
        if (album == null) {
            return renderStatus(HttpURLConnection.HTTP_NOT_FOUND);
        }

        String strSource = ImageUtils.getSourcePath(album);
        String strTarget = ImageUtils.getTargetPath(album);
        Path source = Paths.get(strSource);
        Path target = Paths.get(strTarget);

        if (regenerateThumbnailsJob != null && !regenerateThumbnailsJob.isTerminated()) {
            logger.warn("Cannot launch process, it is already running");
        } else {
            if (regenerateThumbnailsJob == null) {
                regenerateThumbnailsJob = new RegenerateThumbnailsJob(source, target);

            } else if (!regenerateThumbnailsJob.getSource().equals(source) || !regenerateThumbnailsJob.getTarget().equals(target)) {
                logger.warn("Aborting sync between {} and {}", regenerateThumbnailsJob.getSource(), regenerateThumbnailsJob.getTarget());
                regenerateThumbnailsJob.abort();
                regenerateThumbnailsJob.setSource(source);
                regenerateThumbnailsJob.setTarget(target);
            }

            threadPool.execute(regenerateThumbnailsJob);
        }

        return renderStatus(HttpURLConnection.HTTP_NO_CONTENT);
    }
    
    public Render getRegenerationProgress() throws SQLException {
        if (regenerateThumbnailsJob == null) {
            return renderStatus(404);
        }
        return renderJSON(getRegenerationData());
    }
    
    protected Map<String, Long> getRegenerationData() throws SQLException {
        if (regenerateThumbnailsJob == null) {
            return null;
        }
        
        long total = regenerateThumbnailsJob.getNumberToProcess();
        long current = regenerateThumbnailsJob.getNumberProcessed();
        Map<String, Long> regeneration = new HashMap<>();
        regeneration.put("total", total);
        regeneration.put("current", current);
        if (total == 0L && regenerateThumbnailsJob.isTerminated()) {
            regeneration.put("percent", 100L);
            regenerateThumbnailsJob = null;
        } else if (total == 0L && !regenerateThumbnailsJob.isTerminated()) {
            regeneration.put("percent", 0L);
        } else {
            Long percent = Double.valueOf(Math.floor(current * 100 / total)).longValue();
            regeneration.put("percent", percent);
            if (percent == 100L) {
                regenerateThumbnailsJob = null;
            }
        }
        return regeneration;
    }

}
