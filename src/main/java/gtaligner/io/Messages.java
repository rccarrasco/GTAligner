/*
 * Copyright (C) 2015 rafa
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package gtaligner.io;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 *
 * @author rafa
 */
public class Messages {

    private static final Logger logger;
    private static File logdir;

    static {
        logger = Logger.getLogger("gtaligner");

        try {
            URI uri = Messages.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI();
            String dir = new File(uri.getPath()).getParent();
            File logfile = new File(dir, "gtaligner.log");
            FileHandler fh = new FileHandler(logfile.getAbsolutePath());

            System.err.println("Log file is " + logfile.getAbsolutePath());
            fh.setFormatter(new LogFormatter());
            logger.setUseParentHandlers(false); // remove console logging
            logger.addHandler(fh);
            logdir = logfile.getParentFile();
        } catch (URISyntaxException | IOException | SecurityException ex) {
            System.err.println("Could not create log file");
        }
    }

    public static void info(String s) {
        logger.info(s);
    }

    public static void warning(String s) {
        logger.warning(s);
    }

    public static void severe(String s) {
        logger.severe(s);
    }

}
