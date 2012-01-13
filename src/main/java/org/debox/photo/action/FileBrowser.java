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

import com.thebuzzmedia.exiftool.ExifTool;
import com.thebuzzmedia.exiftool.ExifTool.Tag;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import javax.imageio.ImageIO;
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.render.Render;
import org.imgscalr.Scalr;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class FileBrowser extends WebMotionController {

    protected static final File source = new File("/home/cguy/public");
    protected static final File dest = new File("/home/cguy/public_app");

    public Render init() throws IOException {
        copyDirectories(source, dest);

        return renderStatus(200);
    }
    
    private static final int NONE = 0;
private static final int HORIZONTAL = 1;
private static final int VERTICAL = 2;
private static final int[][] OPERATIONS = new int[][] {
        new int[] {  0, NONE},
        new int[] {  0, HORIZONTAL},
        new int[] {180, NONE},
        new int[] {  0, VERTICAL},
        new int[] { 90, HORIZONTAL},
        new int[] { 90, NONE},
        new int[] {-90, HORIZONTAL},
        new int[] {-90, NONE},
        };

public static BufferedImage rotateByExif(BufferedImage image) {
//    try {
//        int index = Integer.parseInt(image.getImageAttribute("EXIF:Orientation")) - 1;
//        int degrees = OPERATIONS[index][0];
//        if (degrees != 0)
//            image = image.rotateImage(degrees);
//        switch (OPERATIONS[index][1]) {
//            case HORIZONTAL:
//                image = image.flopImage();
//                break;
//            case VERTICAL:
//                image = image.flipImage();
//        }
//    }
//    catch (NumberFormatException exc) {}
//    catch (NullPointerException exc) {}
//    return image;
    return null;
}
    
    protected void copyDirectories(File source, File destParent) throws IOException {
        if (source.isDirectory()) {
            File target = new File(destParent, source.getName());
            if (!target.exists()) {
                target.mkdirs();
            }
            for (File file : source.listFiles()) {
                copyDirectories(new File(source, file.getName()), target);
            }
        } else {
            System.out.println(">>>" + source.getAbsolutePath());
            BufferedImage image = ImageIO.read(source);
            ExifTool exifTool = new ExifTool();
            Map<Tag, String> imageMeta = exifTool.getImageMeta(source, Tag.ORIENTATION);
            
            System.out.println(Arrays.toString(imageMeta.values().toArray()));
            
//            BufferedImage thumbnail = Scalr.resize(image, Scalr.Method.SPEED, Scalr.Mode.AUTOMATIC, 150, Scalr.OP_ANTIALIAS);
            BufferedImage thumbnail = Scalr.resize(image, 150, 150);
            File imageFile = new File(destParent, source.getName());
            ImageIO.write(thumbnail, "jpg", imageFile);
        }
    }

    public Render index() throws IOException {
        return renderView("index.jsp");
    }

    public Render picture(String name) {
        return null;
    }
}
