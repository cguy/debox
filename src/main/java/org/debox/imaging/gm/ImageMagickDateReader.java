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
package org.debox.imaging.gm;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.debox.imaging.DateReader;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.im4java.core.IdentifyCmd;
import org.im4java.core.ImageCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class ImageMagickDateReader implements DateReader {
    
    private static final Logger log = LoggerFactory.getLogger(ImageMagickDateReader.class);

    @Override
    public Date getShootingDate(Path path) {
        Date date = null;
        try {
            IMOperation op = new IMOperation();
            op.format("%[exif:DateTimeOriginal]");
            op.addImage(path.toString());

            ImageCommand cmd = new IdentifyCmd();
            StringOutputConsumer output = new StringOutputConsumer();
            cmd.setOutputConsumer(output);
            cmd.run(op);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd hh:mm:ss");
            String strDate = output.getOutput();
            date = dateFormat.parse(strDate);

        } catch (ParseException | IOException | InterruptedException | IM4JavaException ex) {
            log.warn("Unable to get DateTime property for path \"" + path.toString() + "\", reason: " + ex.getMessage());
        }
        return date;
    }
    
}
