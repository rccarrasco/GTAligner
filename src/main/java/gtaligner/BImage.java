/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gtaligner;

import gtaligner.io.TextReader;
import gtaligner.math.BooleanMatrix;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
                if (darkness(rgb) > 765 * threshold) {
                    ++w;
                }
            }
        }
        return w;
    }

    private void flood(BooleanMatrix matrix, double threshold, int x, int y) {
        matrix.set(x, y, true);

        for (int i = Math.max(x - 1, 0); i < Math.min(x + 2, img.getWidth()); ++i) {
            for (int j = Math.max(y - 1, 0); j < Math.min(y + 2, img.getHeight()); ++j) {
                int rgb = img.getRGB(i, j);
                if (darkness(rgb) > 765 * threshold
                        && !matrix.get(i, j)) {
                    flood(matrix, threshold, i, j);
                }
            }
        }
    }

    /**
     * Compute the number of clusters in this image
     *
     * @param threshold
     * @return
     */
    public int clusters(double threshold) {
        int num = 0;
        BooleanMatrix matrix = new BooleanMatrix(img.getHeight(), img.getWidth());

        for (int x = 0; x < img.getWidth(); ++x) {
            for (int y = 0; y < img.getHeight(); ++y) {
                int rgb = img.getRGB(x, y);
                if (darkness(rgb) > 765 * threshold
                        && !matrix.get(x, y)) {
                    ++num;
                    flood(matrix, threshold, x, y);
                }
            }
        }
        return num;
    }

    private double average(int[] values) {
        int sum = 0;
        for (int n = 0; n < values.length; ++n) {
            sum += values[n];
        }
        return sum / (double) values.length;
    }

    public int vprojection(int x, double threshold) {
        int value = 0;

        for (int y = 0; y < img.getHeight(); ++y) {
            int rgb = img.getRGB(x, y);
            if (darkness(rgb) > 765 * threshold) {
                ++value;
            }
        }
        return value;
    }

    private int[] vprojections(double threshold) {
        int[] values = new int[img.getWidth()];

        for (int x = 0; x < img.getWidth(); ++x) {
            values[x] = vprojection(x, threshold);
            System.err.println(x + " " + values[x]);
        }
        return values;
    }

    /**
     * 
     * @param threshold
     * @return Number of columns in this image containing some pixels with darkness above threshold 
     */
    public int footprint(double threshold) {
        int[] values = new int[img.getWidth()];
        int total = 0;
        int w = 0;

        for (int x = 0; x < img.getWidth(); ++x) {
            values[x] = vprojection(x, threshold);
            total += values[x];
        }

        for (int x = 0; x < img.getWidth(); ++x) {
            // lower bound must be paremererized!!!
           // if (values[x] * img.getWidth() > 0.2 * total) {
            if (values[x] > 4) {
                ++w;
            }
        }
        return w;
    }

    public void split(int num) {
        int[] plot = vprojections(0.5);
        double av = average(plot);
        ArrayList<Integer> gaps = new ArrayList<>();

        int r = 0;
        for (int n = 0; n < plot.length; ++n) {
            if (plot[n] < 0.5 * av) {
                r += (av - plot[n]);
            } else if (r > 0) {
                //System.out.println(n + ", " + r);
                gaps.add(r);
                r = 0;
            }
        }

    }

    public static void main(String[] args) {
        for (String arg : args) {
            File ifile = new File(arg);
            BImage image = new BImage(ifile);
            double threshold = 0.5;
            int num = image.clusters(threshold);
            int weight = image.weight(threshold);
            int width = image.footprint(threshold);

            System.out.print(arg + "= [" + num + ","
                    + weight + "," 
                    + width + "," +
                    + image.img.getWidth() + "]");

            String basename = arg.substring(0, arg.lastIndexOf('.'));
            File tfile = new File(basename + ".txt");
            if (tfile.exists()) {
                String text = TextReader.read(tfile)
                        .replaceAll("\\p{Space}", "");
                System.out.print(" " + num / (double) text.length());
            }
            System.out.println();
            image.split(10);
        }
    }
}
