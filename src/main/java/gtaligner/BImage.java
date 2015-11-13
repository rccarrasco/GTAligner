/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gtaligner;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 *
 * @author rafa
 */
public class BImage {

    BufferedImage img = null;

    public BImage(File file) {

        try {
            img = ImageIO.read(file);
        } catch (java.io.IOException e) {
            System.err.println(e);
        }
    }

    /**
     * @return gray level obtained by adding R, G and B components: 0 is minimum
     * and 765 = 3 * 255 maximum.
     */
    private int darkness(int rgb) {
        Color c = new Color(rgb);
        return 765 - c.getRed() - c.getGreen() - c.getBlue();
    }

    public int weight() {
        int w = 0;
        for (int x = 0; x < img.getWidth(); ++x) {
            for (int y = 0; y < img.getHeight(); ++y) {
                int rgb = img.getRGB(x, y);
                int dark = darkness(rgb);
                if (dark > 300) {
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
            System.out.println(arg + "=" + image.weight());
        }
    }
}
