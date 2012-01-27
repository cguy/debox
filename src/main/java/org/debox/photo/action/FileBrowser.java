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

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.debox.photo.FileNameComparator;
import org.debox.photo.model.Album;
import org.debox.photo.model.ApplicationContext;
import org.debox.photo.util.ImageUtils;
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.render.Render;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class FileBrowser extends WebMotionController {

    private static final Logger logger = LoggerFactory.getLogger(FileBrowser.class);

    public Render getAlbum(String album) throws IOException {
        album = URLDecoder.decode(album, "UTF-8");
        
        logger.info("Searching \"{}\" album ...", album);
        Album a = ApplicationContext.getAlbum(album);
        if (a == null) {
            return renderStatus(HttpURLConnection.HTTP_NOT_FOUND);
        }
        File directory = new File(a.getSource());

        File[] files = directory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String string) {
                return string.endsWith("jpg");
            }
        });
        Arrays.sort(files, new FileNameComparator());
        
        String url = StringUtils.replace(getContext().getRequest().getPathInfo(), "api/album", "deploy/thumbnail");
        List<String> list = new ArrayList<String>();
        for (File file : files) {
            list.add(url + File.separatorChar + file.getName());
        }
        
        List<String> names = new ArrayList<String>();
        for (File file : files) {
            names.add(file.getName());
        }

        return renderJSON("list", list, "names", names, "url", url, "albumName", album);
    }

    public Render displayPhoto(String album, String photo) throws IOException {
        album = URLDecoder.decode(album, "UTF-8");
        photo = URLDecoder.decode(photo, "UTF-8");
        
        Album a = ApplicationContext.getAlbum(album);
        if (a == null) {
            return renderStatus(HttpURLConnection.HTTP_NOT_FOUND);
        }
        
        File file = new File(a.getSource(), photo);
        if (!file.exists()) {
            return renderStatus(HttpURLConnection.HTTP_ACCEPTED);
        }
        return  renderJSON("album", album, "photo", photo);
    }
    
    public Render index(String layout) throws IOException {
        return renderView("index.jsp");
    }

    public Render getThumbnail(String album, String photo) throws FileNotFoundException {
        Album a = ApplicationContext.getAlbum(album);
        if (a == null) {
            return renderStatus(HttpURLConnection.HTTP_NOT_FOUND);
        }
        File file = new File(a.getTarget() + File.separatorChar + ImageUtils.THUMBNAIL_PREFIX + photo);
        return renderStream(new FileInputStream(file), "image/jpeg");
    }

    public Render getPhoto(String album, String photo) throws FileNotFoundException, IOException {
        Album a = ApplicationContext.getAlbum(album);
        if (a == null) {
            return renderStatus(HttpURLConnection.HTTP_NOT_FOUND);
        }
        File file = new File(a.getTarget() + File.separatorChar + ImageUtils.LARGE_PREFIX + photo);
        return renderStream(new FileInputStream(file), "image/jpeg");
    }
    
    public Render getAlbums() {
        return renderJSON(
            "albums", ApplicationContext.getAlbums()
        );
    }
    
}
