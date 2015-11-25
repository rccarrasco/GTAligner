/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gtaligner.io;

import java.util.List;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

/**
 *
 * @author rafa
 */
public class TextReader {

    static Charset defaultEncoding = Charset.defaultCharset();

    public static String read(File file, Charset encoding) {
        StringBuilder builder = new StringBuilder();
        try {
            List<String> lines = Files.readAllLines(file.toPath(), encoding);

            for (String line : lines) {
                builder.append(line).append('\n');
            }

            return builder.toString();
        } catch (IOException ex) {
            Messages.severe("Could not read " + file.getAbsolutePath());
        }

        return builder.toString();
    }

    public static String read(File file) {
        return read(file, defaultEncoding);
    }
    
    /**
     *
     * @param filename
     * @return
     */
    public static String readFile(String filename) {
        return read(new File(filename), defaultEncoding);
    }
    
}
