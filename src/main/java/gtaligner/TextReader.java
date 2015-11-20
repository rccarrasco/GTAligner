/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gtaligner;

import java.util.List;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

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
            Logger.getLogger(TextReader.class.getName()).log(Level.SEVERE, null, ex);
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
