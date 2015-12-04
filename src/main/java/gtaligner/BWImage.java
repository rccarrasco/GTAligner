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
import javax.imageio.ImageIO;

/**
 * An extended BufferedImage
 *
 * @author rafa
 */
public class BWImage extends BufferedImage {

    private int weight;
    private int[] projection;
    private int[] skyline;
    private int[] baseline;
    private int[] profile;

    /**
     * Basic constructor
     *
     * @param image an image
     */
    BWImage(BufferedImage image) {
        super(image.getWidth(), image.getHeight(),
                BufferedImage.TYPE_BYTE_BINARY);
        Graphics2D g = createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();

        int width = getWidth();
        int height = getHeight();

        projection = new int[width];
        skyline = new int[width];
        baseline = new int[width];
        profile = new int[width];

        for (int x = 0; x < width; ++x) {
            skyline[x] = -1;
            baseline[x] = -1;
            for (int y = 0; y < height; ++y) {
                if (isBlack(x, y)) {
                    ++weight;
                    ++projection[x];
                    skyline[x] = y;
                    if (baseline[x] < 0) {
                        baseline[x] = y;
                    }
                    if (x + 1 == width || luminosity(x + 1, y) > 0) {
                        ++profile[x];
                    }
                }
            }
        }
    }

    /**
     * Read image from file
     *
     * @param file an image file
     * @throws java.io.IOException
     */
    public BWImage(File file) throws IOException {
        this(ImageIO.read(file));
    }

    /**
     * Read image from file
     *
     * @param filename the name of the image file
     * @throws java.io.IOException
     */
    public BWImage(String filename) throws IOException {
        this(new File(filename));
    }

    public final int getAlpha(int x, int y) {
        return (getRGB(x, y) >> 24) & 0xFF;
    }

    public final int getRed(int x, int y) {
        return (getRGB(x, y) >> 16) & 0xFF;
    }

    public final int getGreen(int x, int y) {
        return (getRGB(x, y) >> 8) & 0xFF;
    }

    public final int getBlue(int x, int y) {
        return getRGB(x, y) & 0xFF;
    }

    public final int luminosity(int x, int y) {
        return getRed(x, y) + getGreen(x, y) + getBlue(x, y);
    }

    /**
     *
     * @param x column number
     * @param y row number
     * @return true if the pixel at position (x,y) is a black pixel
     */
    public final boolean isBlack(int x, int y) {
        return luminosity(x, y) == 0;
    }

    /**
     *
     * @return the number of dark pixels in this image
     */
    public int weight() {
        return weight;
    }

    /**
     *
     * @return Number of columns in this image containing some dark pixels
     */
    public int shadow() {
        int value = 0;
        for (int x = 0; x < getWidth(); ++x) {
            if (projection[x] > 0) {
                ++value;
            }
        }
        return value;
    }

    /**
     *
     * @return Number of rows expanded by the image (added for every column)
     */
    public int gauge() {
        int value = 0;
        for (int x = 0; x < getWidth(); ++x) {
            value += (skyline[x] - baseline[x]);
        }
        return value;
    }

    /**
     *
     * @return number of black pixels with a white east-neighbor
     */
    public int profileE() {
        int value = 0;

        for (int x = 0; x < getWidth(); ++x) {
            value += profile[x];
        }

        return value;
    }

    public int bwcols() {
        int value = projection[getWidth() - 1] > 0 ? 1 : 0;

        for (int x = 1; x < getWidth(); ++x) {
            if (projection[x - 1] > 0 && projection[x] == 0) {
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

        for (int i = Math.max(x - 1, 0); i < Math.min(x + 2, getWidth()); ++i) {
            for (int j = Math.max(y - 1, 0); j < Math.min(y + 2, getHeight()); ++j) {
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
        BooleanMatrix matrix = new BooleanMatrix(getHeight(), getWidth());

        for (int x = 0; x < getWidth(); ++x) {
            for (int y = 0; y < getHeight(); ++y) {
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

        int total = 0;
        int matching = 0;

        for (String arg : args) {
            try {
                File file = new File(arg);
                BWImage image = new BWImage(file);
                String path = file.getAbsolutePath();
                String dir = path.substring(0, path.lastIndexOf('.'));
                String text = TextReader.read(new File(dir + ".txt"));
                TextLine line = new TextLine(text);

                int ngaps = image.bwcols();
                int nchars = line.textsize();

                System.out.println(arg
                        + "= ["
                        + ngaps + ","
                        + nchars + "]");
                ++total;
                if (ngaps == nchars) {
                    ++matching;
                }
            } catch (IOException ex) {
                Messages.warning("Could not open " + arg);
            }
        }
        System.out.println(matching + " out of " + total);
    }
}
