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

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.commons.lang.StringUtils;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.formats.jpeg.JpegImageMetadata;
import org.apache.sanselan.formats.tiff.TiffField;
import org.apache.sanselan.formats.tiff.constants.TiffConstants;
import org.debox.photo.FileNameComparator;
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.render.Render;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class FileBrowser extends WebMotionController {
    protected static final String LARGE_PREFIX = "1600_";
    protected static final String THUMBNAIL_PREFIX = "th_";

    private static final Logger logger = LoggerFactory.getLogger(FileBrowser.class);
    protected static final File source = new File("/home/cguy/public");
    protected static final File dest = new File("/home/cguy/public_app");

    public Render init() throws IOException {
        copyDirectories(source, dest);
        return renderStatus(200);
    }
    
    protected BufferedImage rotate(File fileToRead) throws IOException {
        BufferedImage image = ImageIO.read(fileToRead);
        return rotate(fileToRead, image);
    }

    protected BufferedImage rotate(File fileToRead, BufferedImage imageToRotate) throws IOException {
        BufferedImage image = imageToRotate;
        try {
            JpegImageMetadata metadata = (JpegImageMetadata) Sanselan.getMetadata(fileToRead);
            TiffField field = metadata.findEXIFValue(TiffConstants.TIFF_TAG_ORIENTATION);

            int orientation = field.getIntValue();
            if (orientation == TiffConstants.ORIENTATION_VALUE_ROTATE_90_CW) {
                image = Scalr.rotate(image, Scalr.Rotation.CW_90);
            } else if (orientation == TiffConstants.ORIENTATION_VALUE_ROTATE_180) {
                image = Scalr.rotate(image, Scalr.Rotation.CW_180);
            } else if (orientation == TiffConstants.ORIENTATION_VALUE_ROTATE_270_CW) {
                image = Scalr.rotate(image, Scalr.Rotation.CW_270);
            }
        } catch (ImageReadException ex) {
            ex.printStackTrace();
        }
        return image;
    }

    protected void copyDirectories(File source, File target) throws IOException {
        if (source.isDirectory()) {
            if (!target.exists()) {
                target.mkdirs();
            }
            for (File file : source.listFiles()) {
                copyDirectories(new File(source, file.getName()), new File(target, file.getName()));
            }
        } else {
            BufferedImage image = ImageIO.read(source);
            
            BufferedImage thumbnail = Scalr.resize(image, 225);
            thumbnail = rotate(source, thumbnail);

            int height = thumbnail.getHeight();
            int width = thumbnail.getWidth();
            int squareSize = width;
            int x = 0;
            int y = 0;
            if (width > height) {
                squareSize = height;
                x = (width - height) / 2;
                y = 0;
            } else if (width < height) {
                squareSize = width;
                x = 0;
                y = (height - width) / 2;
            }

            thumbnail = Scalr.crop(thumbnail, x, y, squareSize, squareSize);

            File imageFile = new File(target.getParentFile(), THUMBNAIL_PREFIX + source.getName());
            ImageIO.write(thumbnail, "jpg", imageFile);
            
            thumbnail = Scalr.resize(image, 1600);
            thumbnail = rotate(source, thumbnail);
            imageFile = new File(target.getParentFile(), LARGE_PREFIX + source.getName());
            ImageIO.write(thumbnail, "jpg", imageFile);
            
            image.flush();
            thumbnail.flush();
        }
    }

    public Render displayAlbum(String album) throws IOException {
        album = URLDecoder.decode(album, "UTF-8");
        File directory = new File(source, album);
        if (!directory.exists()) {
            return renderStatus(HttpURLConnection.HTTP_NOT_FOUND);
        }

        File[] files = directory.listFiles();
        Arrays.sort(files, new FileNameComparator());

        String url = StringUtils.replace(getContext().getRequest().getPathInfo(), "album", "deploy/thumbnail");
        return renderView("album.jsp", "list", files, "url", url, "albumName", album);
    }
    
    public Render getAlbum(String album) throws IOException {
        album = URLDecoder.decode(album, "UTF-8");
        File directory = new File(source, album);
        if (!directory.exists()) {
            return renderStatus(HttpURLConnection.HTTP_NOT_FOUND);
        }

        File[] files = directory.listFiles();
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
        File file = new File(source, album + File.separatorChar + photo);
        if (!file.exists()) {
            return renderStatus(HttpURLConnection.HTTP_ACCEPTED);
        }
        return  renderJSON("album", album, "photo", photo);
    }
    
    public Render displayPhoto2(String album, String photo) throws IOException {
        album = URLDecoder.decode(album, "UTF-8");
        photo = URLDecoder.decode(photo, "UTF-8");
        File file = new File(source, album + File.separatorChar + photo);
        if (!file.exists()) {
            return renderStatus(HttpURLConnection.HTTP_ACCEPTED);
        }
        return renderView("photo.jsp", "album", album, "photo", photo);
    }

    public Render displayAlbums() throws IOException {
        File[] files = source.listFiles();
        Arrays.sort(files, new FileNameComparator());
        return renderView("index.jsp", "list", files);
    }

    public Render getThumbnail(String album, String photo) throws FileNotFoundException {
        File file = new File(dest, album + File.separatorChar + THUMBNAIL_PREFIX +photo);
        return renderStream(new FileInputStream(file), "image/jpeg");
    }

    public Render getPhoto(String album, String photo) throws FileNotFoundException, IOException {
        File file = new File(dest, album + File.separatorChar + LARGE_PREFIX + photo);
        return renderStream(new FileInputStream(file), "image/jpeg");
    }
}
