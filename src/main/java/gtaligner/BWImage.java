/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gtaligner;

import gtaligner.io.Messages;
import gtaligner.io.TextReader;
import gtaligner.math.BooleanMatrix;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

/**
 * An extended BufferedImage
 *
 * @author rafa
 */
public class BWImage {

    BufferedImage img = null;

    /**
     * Read image from file
     *
     * @param file an image file
     */
    public BWImage(File file) {

        try {
            Graphics2D graphics;
            BufferedImage source = ImageIO.read(file);

            img = new BufferedImage(source.getWidth(), source.getHeight(),
                    BufferedImage.TYPE_BYTE_BINARY);
            graphics = img.createGraphics();
            graphics.drawImage(source, 0, 0, null);
        } catch (IOException ex) {
            Messages.severe("Could nor open " + file);
        }

    }

    /**
     * Read image from file
     *
     * @param filename the name of the image file
     */
    public BWImage(String filename) {
        this(new File(filename));
    }

    public int getAlpha(int x, int y) {
        return (img.getRGB(x, y) >> 24) & 0xFF;
    }

    public int getRed(int x, int y) {
        return (img.getRGB(x, y) >> 16) & 0xFF;
    }

    public int getGreen(int x, int y) {
        return (img.getRGB(x, y) >> 8) & 0xFF;
    }

    public int getBlue(int x, int y) {
        return img.getRGB(x, y) & 0xFF;
    }

    public int luminosity(int x, int y) {
        return getRed(x, y) + getGreen(x, y) + getBlue(x, y);
    }

    private boolean isBlack(int x, int y) {
        return luminosity(x, y) == 0;
    }

    /**
     *
     * @return the number of dark pixels in this image
     */
    public int weight() {
        int w = 0;
        for (int x = 0; x < img.getWidth(); ++x) {
            for (int y = 0; y < img.getHeight(); ++y) {
                if (isBlack(x, y)) {
                    ++w;
                }
            }
        }
        return w;
    }

    private void flood(BooleanMatrix matrix, int x, int y) {
        matrix.set(x, y, true);

        for (int i = Math.max(x - 1, 0); i < Math.min(x + 2, img.getWidth()); ++i) {
            for (int j = Math.max(y - 1, 0); j < Math.min(y + 2, img.getHeight()); ++j) {
                if (isBlack(i, j) && !matrix.get(i, j)) {
                    flood(matrix, i, j);
                }
            }
        }
    }

    /**
     * Compute the number of clusters in this image
     *
     * @return
     */
    public int clusters() {
        int num = 0;
        BooleanMatrix matrix = new BooleanMatrix(img.getHeight(), img.getWidth());

        for (int x = 0; x < img.getWidth(); ++x) {
            for (int y = 0; y < img.getHeight(); ++y) {
                if (isBlack(x, y)
                        && !matrix.get(x, y)) {
                    ++num;
                    flood(matrix, x, y);
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

    public int vprojection(int x) {
        int value = 0;

        for (int y = 0; y < img.getHeight(); ++y) {
            int rgb = img.getRGB(x, y);
            if (isBlack(x, y)) {
                ++value;
            }
        }
        return value;
    }

    private int[] vprojections() {
        int[] values = new int[img.getWidth()];

        for (int x = 0; x < img.getWidth(); ++x) {
            values[x] = vprojection(x);
            //System.err.println(x + " " + values[x]);
        }
        return values;
    }

    /**
     *
     * @return Number of columns in this image containing some dark pixels
     */
    public int shadow() {
        int[] values = new int[img.getWidth()];
        int total = 0;
        int w = 0;

        for (int x = 0; x < img.getWidth(); ++x) {
            values[x] = vprojection(x);
            total += values[x];
        }

        for (int x = 0; x < img.getWidth(); ++x) {
            // lower bound must be paremererized!!!
            // if (values[x] * img.getWidth() > 0.2 * total) {
            if (values[x] > 2) {
                ++w;
            }
        }
        return w;
    }

    public void split(int num) {
        int[] plot = vprojections();
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
            BWImage image = new BWImage(ifile);
            int num = image.clusters();
            int weight = image.weight();
            int width = image.shadow();

            System.out.print(arg + "= [" + num + ","
                    + weight + ","
                    + width + ","
                    + +image.img.getWidth() + "]");

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
