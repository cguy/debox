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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import javax.imageio.ImageIO;
import org.apache.commons.lang.StringUtils;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.formats.jpeg.JpegImageMetadata;
import org.apache.sanselan.formats.tiff.TiffField;
import org.apache.sanselan.formats.tiff.constants.TiffConstants;
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.render.Render;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class FileBrowser extends WebMotionController {
    
    private static final Logger logger = LoggerFactory.getLogger(FileBrowser.class);

    protected static final File source = new File("/home/cguy/public");
    protected static final File dest = new File("/home/cguy/public_app");

    public Render init() throws IOException {
        copyDirectories(source, dest);
        return renderStatus(200);
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
            BufferedImage thumbnail = Scalr.resize(image, 150);

            try {
                JpegImageMetadata metadata = (JpegImageMetadata) Sanselan.getMetadata(source);
                TiffField field = metadata.findEXIFValue(TiffConstants.TIFF_TAG_ORIENTATION);

                int orientation = field.getIntValue();
                if (orientation == TiffConstants.ORIENTATION_VALUE_ROTATE_90_CW) {
                    thumbnail = Scalr.rotate(thumbnail, Scalr.Rotation.CW_90);
                } else if (orientation == TiffConstants.ORIENTATION_VALUE_ROTATE_180) {
                    thumbnail = Scalr.rotate(thumbnail, Scalr.Rotation.CW_180);
                } else if (orientation == TiffConstants.ORIENTATION_VALUE_ROTATE_270_CW) {
                    thumbnail = Scalr.rotate(thumbnail, Scalr.Rotation.CW_270);
                }
            } catch (ImageReadException ex) {
                ex.printStackTrace();
            }

            File imageFile = new File(target.getParentFile(), source.getName());
            ImageIO.write(thumbnail, "jpg", imageFile);
        }
    }

    public Render index(String id) throws IOException {
        System.out.println(id);
        
        File directory = source;
        if (!StringUtils.isEmpty(id)) {
            id = URLDecoder.decode(id, "UTF-8");
            directory = new File(source, id);
            if (!directory.exists()) {
                return renderStatus(HttpURLConnection.HTTP_NOT_FOUND);
            }
        }
        
        String url = StringUtils.replace(getContext().getRequest().getPathInfo(), "albums", "deploy/photos");
        System.out.println(url);
        
        return renderView("index.jsp", "list", directory.listFiles(), "url", url);
    }

    public Render picture(String album, String name) throws FileNotFoundException {
        File directory = new File(dest, album + File.separatorChar + name);
        logger.info(directory.getAbsolutePath());
        
        return renderStream(new FileInputStream(directory), "image/jpeg");
    }
}
