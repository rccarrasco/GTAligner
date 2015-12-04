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

    private BufferedImage img;

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
            Messages.severe("Could not open " + file.getAbsolutePath());
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

    public int getWidth() {
        return img.getWidth();
    }

    public int getHeight() {
        return img.getHeight();
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
     * @return the number of dark pixels in the x-th column
     */
    private int weight(int x) {
        int w = 0;

        for (int y = 0; y < img.getHeight(); ++y) {
            if (isBlack(x, y)) {
                ++w;
            }
        }
        return w;
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

    private boolean hasBlack(int x) {
        for (int y = 0; y < img.getHeight(); ++y) {
            if (isBlack(x, y)) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @return Number of columns in this image containing some dark pixels
     */
    public int shadow() {
        int value = 0;
        for (int x = 0; x < img.getWidth(); ++x) {
            if (hasBlack(x)) {
                value += 1;
            }
        }
        return value;
    }

    /**
     * Distance between the highest and the lowest dark pixels in the x-th
     * column
     *
     * @param x a column number
     * @return the difference between the row numbers where the highest and the
     * lowest dark pixels are found in the x-th column
     */
    private int gauge(int x) {
        int low = -1;
        int high = -1;
        for (int y = 0; y < img.getHeight(); ++y) {
            if (isBlack(x, y)) {
                if (low < 0) {
                    low = y;
                }
                high = y;
            }
        }
        return (high - low);
    }

    /**
     *
     * @return Number of rows expanded by the image (added for every column)
     */
    public int gauge() {
        int value = 0;
        for (int x = 0; x < img.getWidth(); ++x) {
            value += gauge(x);
        }
        return value;
    }

    /**
     *
     * @return number of black pixels in the x-th column with a white
     * east-neighbor in column x+1
     */
    private int profileE(int x) {
        int value = 0;
        for (int y = 0; y < img.getHeight(); ++y) {
            if (isBlack(x, y)) {
                if (x + 1 == img.getWidth() || !isBlack(x + 1, y)) {
                    value += 1;
                }
            }
        }

        return value;
    }

    /**
     *
     * @return number of black pixels with a white east-neighbor
     */
    public int profileE() {
        int value = 0;
        
        for (int x = 0; x < img.getWidth(); ++x) {
            value += profileE(x);
        }

        return value;
    }

    public int bwcols() {
        int value = hasBlack(img.getWidth() - 1)? 1: 0;
        
        for (int x = 1; x < img.getWidth(); ++x) {
            if (hasBlack(x - 1) && !hasBlack(x)) {
                value += 1;
            }
        }

        return value;
    }
    
    /**
     *
     * @param feature
     * @return the value of this image feature
     */
    public double getFeature(Feature feature) {
        switch (feature) {
            case WEIGHT:
                return weight();
            case SHADOW:
                return shadow();
            case GAUGE:
                return gauge();
            case PROFILE_E:
                return profileE();
            case BWCOLS:
                return bwcols();
            default:
                throw new UnsupportedOperationException();
        }
    }

    /**
     *
     * @return the FeatureVector containing the values for all image features
     */
    public FeatureVector getFeatures() {
        FeatureVector vector = new FeatureVector();
        for (Feature feature : Feature.values()) {
            vector.put(feature, getFeature(feature));
        }
        
        return vector;
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
     * @return the number of clusters in this image
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
            //image.split(10);
        }
    }
}
