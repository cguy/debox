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

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndLinkImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.io.FileUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.debox.connector.api.exception.AuthenticationProviderException;
import org.debox.connector.google.CustomXMLReader;
import org.debox.imaging.ImageUtils;
import org.debox.photo.dao.AlbumDao;
import org.debox.photo.dao.TokenDao;
import org.debox.photo.dao.UserDao;
import org.debox.photo.job.RegenerateThumbnailsJob;
import org.debox.photo.model.Album;
import org.debox.photo.model.Configuration;
import org.debox.photo.model.Contact;
import org.debox.photo.model.Photo;
import org.debox.photo.model.Provider;
import org.debox.photo.model.ThirdPartyAccount;
import org.debox.photo.model.ThumbnailSize;
import org.debox.photo.model.Token;
import org.debox.photo.model.User;
import org.debox.photo.server.ApplicationContext;
import org.debox.photo.server.renderer.ZipDownloadRenderer;
import org.debox.photo.thirdparty.ServiceUtil;
import org.debox.photo.util.SessionUtils;
import org.debox.photo.util.StringUtils;
import org.debux.webmotion.server.render.Render;
import org.debux.webmotion.server.render.RenderStatus;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class AlbumController extends DeboxController {

    private static final Logger logger = LoggerFactory.getLogger(AlbumController.class);
    
    protected static AlbumDao albumDao = new AlbumDao();
    protected static TokenDao tokenDao = new TokenDao();
    protected RegenerateThumbnailsJob regenerateThumbnailsJob;
    protected ExecutorService threadPool = Executors.newSingleThreadExecutor();
    protected UserDao userDao = new UserDao();
    
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
        album.setOwnerId(SessionUtils.getUser(SecurityUtils.getSubject()).getId());
        
        if (StringUtils.isEmpty(parentId)) {
            album.setRelativePath(parentId);
            album.setRelativePath(File.separatorChar + album.getName());
            
        } else {
            Album parent = albumDao.getAlbum(parentId);
            if (parent == null) {
                return renderError(HttpURLConnection.HTTP_INTERNAL_ERROR, "There is not any album with id " + parentId);
            }
            album.setParentId(parentId);
            album.setRelativePath(parent.getRelativePath() + File.separatorChar + album.getName());
        }
        
        Configuration configuration = ApplicationContext.getInstance().getConfiguration();
        String[] paths = {configuration.get(Configuration.Key.SOURCE_PATH), configuration.get(Configuration.Key.TARGET_PATH)};
        
        Album existingAtPath = albumDao.getAlbumByPath(album.getRelativePath());
        if (existingAtPath != null) {
            return renderError(HttpURLConnection.HTTP_INTERNAL_ERROR, "There is already an album at path (" + album.getRelativePath() + ")");
        }
        
        for (String path : paths) {
            File targetDirectory = new File(path + album.getRelativePath());
            if (!targetDirectory.exists() && !targetDirectory.mkdir()) {
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
        
        Configuration conf = ApplicationContext.getInstance().getConfiguration();
        String originalDirectory = conf.get(Configuration.Key.SOURCE_PATH) + album.getRelativePath();
        String workingDirectory = conf.get(Configuration.Key.TARGET_PATH) + album.getRelativePath();
        
        if (!FileUtils.deleteQuietly(new File(workingDirectory)) || !FileUtils.deleteQuietly(new File(originalDirectory))) {
            return renderError(HttpURLConnection.HTTP_INTERNAL_ERROR, "Unable to delete directories from file system.");
        }

        albumDao.delete(album);
        return renderStatus(HttpURLConnection.HTTP_NO_CONTENT);
    }
    
    public List<Album> albums(String parentId, String token) throws SQLException {
        boolean isAdministrator = SessionUtils.isAdministrator(SecurityUtils.getSubject());
        List<Album> albums;
        if (isAdministrator) {
            albums = albumDao.getAlbums(parentId);
        } else if (SessionUtils.isLogged(SecurityUtils.getSubject())) {
            albums = albumDao.getVisibleAlbumsForLoggedUser(parentId);
        } else {
            albums = albumDao.getVisibleAlbums(token, parentId);
        }
        return albums;
    }

    public Render getAlbums(String parentId, String token, String criteria) throws SQLException {
        boolean isAdministrator = SessionUtils.isAdministrator(SecurityUtils.getSubject());
        List<Album> albums;
        if (isAdministrator && "all".equals(criteria)) {
            albums = albumDao.getAllAlbums();
        } else {
            albums = albums(parentId, token);
        }
        return renderJSON("albums", albums);
    }

    public Render getAlbum(String token, String id) throws IOException, SQLException, IllegalArgumentException, FeedException, IOException, AuthenticationProviderException {
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();
        
        boolean isAdministrator = SessionUtils.isAdministrator(subject);
        Album album;
        if (!isAdministrator && SessionUtils.isLogged(subject)) {
            album = albumDao.getVisibleAlbumForLoggedUser(user.getId(), id);
        } else if (isAdministrator) {
            album = albumDao.getAlbum(id);
        } else {
            album = albumDao.getVisibleAlbum(token, id);
        }
        if (album == null) {
            return renderStatus(HttpURLConnection.HTTP_NOT_FOUND);
        }

        List<Token> tokens = null;
        List<Contact> contacts = new ArrayList<>();
        if (isAdministrator) {
            tokens = tokenDao.getAllTokenWithAccessToAlbum(album);
            List<ThirdPartyAccount> accounts = userDao.getThirdPartyAccounts(user);
            for (ThirdPartyAccount account : accounts) {
                String thirdPartyToken = account.getToken();
                if (thirdPartyToken != null) {
                    switch (account.getProviderId()) {
                        case "facebook":
                            DefaultFacebookClient client = new DefaultFacebookClient(thirdPartyToken);
                            Connection<com.restfb.types.User> myFriends = client.fetchConnection("me/friends", com.restfb.types.User.class);
                            contacts.addAll(convert(myFriends.getData()));
                            break;
                        case "google":
                            URL url = new URL("https://www.google.com/m8/feeds/contacts/default/full?access_token=" + thirdPartyToken);
                            SyndFeedInput input = new SyndFeedInput();
                            
                            do {
                                SyndFeed feed = input.build(new CustomXMLReader(url).getReader());
                                List<SyndLinkImpl> links = feed.getLinks();
                                url = null;
                                if (links != null) {
                                    for (SyndLinkImpl link : links) {
                                        if (link.getRel().equals("next")) {
                                            url = new URL(link.getHref() + "&access_token=" + thirdPartyToken);
                                            break;
                                        }
                                    }
                                }
                                
                                Iterator<SyndEntry> contactsIterator = feed.getEntries().iterator();
                                List<Contact> result = convert(contactsIterator);
                                contacts.addAll(result);
                                
                            } while (url != null);
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
                    }
                }
            }
        }
        
        List<Album> subAlbums = this.albums(album.getId(), token);
        List<Photo> photos = photoDao.getPhotos(id, token);
        Album parent = albumDao.getAlbum(album.getParentId());
        
        return renderJSON("album", album, "albumParent", parent,
                "subAlbums", subAlbums, "photos", photos,
                "regeneration", getRegenerationData(), "tokens", tokens, "contacts", contacts);
    }
    
    private List<Contact> convert(Iterator<SyndEntry> contactsIterator) {
        List<Contact> list = new ArrayList<>();
        while (contactsIterator.hasNext()) {
            SyndEntry entry = contactsIterator.next();
            
            Contact contact = new Contact();
            contact.setName(entry.getTitle());
            contact.setProvider(ServiceUtil.getProvider("google"));
            
            List<Element> elements = (List<Element>) entry.getForeignMarkup();
            for (Element element : elements) {
                String prefix = element.getNamespacePrefix();
                String name = element.getName();
                if ("gd".equals(prefix) && "email".equals(name)) {
                    String mail = element.getAttributeValue("address");
                    contact.setId(mail);
                    if (StringUtils.isEmpty(contact.getName())) {
                        contact.setName(mail);
                    }
                    break;
                }
            }
            if (StringUtils.isNotEmpty(contact.getId())) {
                list.add(contact);
            }
        }
        return list;
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
    
    public Render editAlbum(String albumId, String name, String description, String visibility, boolean downloadable, List<String> authorizedTokens) throws SQLException, IOException, IllegalArgumentException, FeedException, AuthenticationProviderException {
        Album album = albumDao.getAlbum(albumId);
        if (album == null) {
            return renderStatus(HttpURLConnection.HTTP_NOT_FOUND);
        }
        album.setName(name);
        album.setDescription(description);
        album.setPublic(Boolean.parseBoolean(visibility));
        album.setDownloadable(downloadable);

        albumDao.save(album);
        
        if (authorizedTokens != null) {
            List<Token> tokens = tokenDao.getAll();
            for (Token token : tokens) {
                if (authorizedTokens.contains(token.getId())) {
                    addParentAlbumsToToken(album, token);
                } else {
                    removeChildAlbumToToken(album, token);
                }
            }
            tokenDao.saveAll(tokens);
            
            List<ThirdPartyAccount> accounts = new ArrayList<>();
            for (String thirdPartyId : authorizedTokens) {
                String providerId = StringUtils.substringBefore(thirdPartyId, "-");
                Provider provider = ServiceUtil.getProvider(providerId);
                if (provider == null) {
                    continue;
                }
                
                String providerAccountId = StringUtils.substringAfter(thirdPartyId, "-");
                
                ThirdPartyAccount account = userDao.getUser(provider.getId(), providerAccountId);
                if (account == null) {
                    account = new ThirdPartyAccount(provider, providerAccountId, null);
                    userDao.save(account);
                }
                accounts.add(account);
            }
            userDao.saveAccess(accounts, albumId);
        }
        
        return getAlbum(null, album.getId());
    }
    
    protected void addParentAlbumsToToken(Album album, Token token) throws SQLException {
        if (album != null && !token.getAlbums().contains(album)) {
            token.getAlbums().add(album);
            addParentAlbumsToToken(albumDao.getAlbum(album.getParentId()), token);
        }
    }
    
    protected void removeChildAlbumToToken(Album album, Token token) throws SQLException {
        if (album != null && token.getAlbums().contains(album)) {
            List<Album> children = albumDao.getAlbums(album.getId());
            for (Album child : children) {
                removeChildAlbumToToken(child, token);
            }
            token.getAlbums().remove(album);
        }
    }

    public Render setAlbumCover(String albumId, String objectId) throws SQLException, IOException {
        if (StringUtils.isEmpty(objectId) && photoDao.getPhoto(objectId) != null) {
            return renderError(HttpURLConnection.HTTP_INTERNAL_ERROR, "The photoId parameter must correspond with a valid photo.");
        }
        
        String id;
        if (objectId.startsWith("a.")) {
            Photo photo = albumDao.getAlbumCover(objectId.substring(2));
            id = photo.getId();
        } else {
            id = objectId;
        }
        
        photoDao.savePhotoGenerationTime("a." + albumId, ThumbnailSize.SQUARE, new Date().getTime());
        albumDao.setAlbumCover(albumId, id);
        return renderStatus(HttpURLConnection.HTTP_NO_CONTENT);
    }

    public Render getAlbumCover(String token, String albumId) throws SQLException, IOException {
        albumId = StringUtils.substringBeforeLast(albumId, "-cover.jpg");
        
        Photo photo;
        if (SessionUtils.isAdministrator(SecurityUtils.getSubject())) {
            photo = albumDao.getAlbumCover(albumId);
        } else {
            photo = albumDao.getVisibleAlbumCover(token, albumId);
        }

        Album album = albumDao.getAlbum(albumId);
        if (album == null) {
            return renderError(HttpURLConnection.HTTP_NOT_FOUND, "");
        } else if (photo == null) {
            return renderRedirect("/img/default_album.png");
        }
        
        Configuration configuration = ApplicationContext.getInstance().getConfiguration();
        FileInputStream fis = null;
        try {
            fis = ImageUtils.getStream(configuration, photo, ThumbnailSize.SQUARE);
            
        } catch (Exception ex) {
            logger.error("Unable to get stream", ex);
        }
        RenderStatus status = handleLastModifiedHeader(album);
        if (status.getCode() == HttpURLConnection.HTTP_NOT_MODIFIED) {
            return status;
        }
        return renderStream(fis, "image/jpeg");
    }

    public Render download(String token, String albumId, boolean resized) throws SQLException {
        Subject subject = SecurityUtils.getSubject();
        boolean isAdministrator = SessionUtils.isAdministrator(subject);
        
        Album album;
        if (!isAdministrator && SessionUtils.isLogged(subject)) {
            User user = (User) subject.getPrincipal();
            album = albumDao.getVisibleAlbumForLoggedUser(user.getId(), albumId);
        } else if (isAdministrator) {
            album = albumDao.getAlbum(albumId);
        } else {
            album = albumDao.getVisibleAlbum(token, albumId);
        }
        
        if (album == null) {
            return renderStatus(HttpURLConnection.HTTP_NOT_FOUND);

        } else if (!album.isDownloadable() && !isAdministrator) {
            return renderStatus(HttpURLConnection.HTTP_FORBIDDEN);
        }

        Configuration configuration = ApplicationContext.getInstance().getConfiguration();
        if (resized) {
            List<Photo> photos = photoDao.getPhotos(album.getId());
            Map<String, String> names = new HashMap<>(photos.size());
            for (Photo photo : photos) {
                names.put(ThumbnailSize.LARGE.getPrefix() + photo.getFilename(), ThumbnailSize.LARGE.getPrefix() + photo.getFilename());
            }
            return new ZipDownloadRenderer(configuration.get(Configuration.Key.TARGET_PATH) + album.getRelativePath(), album.getName(), names);
        }
        return new ZipDownloadRenderer(configuration.get(Configuration.Key.SOURCE_PATH) + album.getRelativePath(), album.getName());
    }

    public Render regenerateThumbnails(String albumId) throws SQLException {
        Configuration configuration = ApplicationContext.getInstance().getConfiguration();

        Album album = albumDao.getAlbum(albumId);
        if (album == null) {
            return renderStatus(HttpURLConnection.HTTP_NOT_FOUND);
        }

        String strSource = configuration.get(Configuration.Key.SOURCE_PATH) + album.getRelativePath();
        String strTarget = configuration.get(Configuration.Key.TARGET_PATH) + album.getRelativePath();

        if (StringUtils.isEmpty(strSource) || StringUtils.isEmpty(strTarget)) {
            return renderError(HttpURLConnection.HTTP_CONFLICT, "Work paths are not defined.");
        }

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
