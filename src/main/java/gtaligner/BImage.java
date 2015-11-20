/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gtaligner;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * An extended BufferedImage
 *
 * @author rafa
 */
public class BImage {

    BufferedImage img = null;

    /**
     * Read image from file
     *
     * @param file an image file
     */
    public BImage(File file) {

        try {
            img = ImageIO.read(file);
        } catch (IOException ex) {
            Logger.getLogger(BImage.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Read image from file
     *
     * @param filename the name of the image file 
     */
    public BImage(String filename) {
        this(new File(filename));
    }

    /**
     * @return gray level obtained by adding R, G and B components: 0 is minimum
     * and 765 = 3 * 255 maximum.
     */
    private int darkness(int rgb) {
        Color c = new Color(rgb);
        return 765 - c.getRed() - c.getGreen() - c.getBlue();
    }

    /**
     *
     * @param threshold a gray level between 0 and 1
     * @return the number of pixels in this image with a gray level above the
     * specified threshold
     */
    public int weight(double threshold) {
        int w = 0;
        for (int x = 0; x < img.getWidth(); ++x) {
            for (int y = 0; y < img.getHeight(); ++y) {
                int rgb = img.getRGB(x, y);
                double dark = darkness(rgb) / 765.0;
                if (dark > threshold) {
                    ++w;
                }
            }
        }
        return w;
    }

    public static void main(String[] args) {
        for (String arg : args) {
            File file = new File(arg);
            BImage image = new BImage(file);
            System.out.println(arg + "=" + image.weight(0.5));
        }
    }
}
