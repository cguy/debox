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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.im4java.process.OutputConsumer;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class StringOutputConsumer implements OutputConsumer {
    
    protected String output = null;

    public String getOutput() {
        return output;
    }
    
    /**
     * Read command output and save in an internal field.
     * @see org.im4java.process.OutputConsumer#consumeOutput(java.io.InputStream)
     */
    @Override
    public void consumeOutput(InputStream pInputStream) throws IOException {
        InputStreamReader isr = new InputStreamReader(pInputStream);
        try (BufferedReader reader = new BufferedReader(isr)) {
            String line;
            if ((line = reader.readLine()) != null) {
                output = line;
            }
        }
    }
    
}