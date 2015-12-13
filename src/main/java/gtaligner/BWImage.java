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
 * @author carrasco@ua.es
 */
public class BWImage extends BufferedImage {

    private int weight;
    private int[] projection; // cummulative values of vertical projections
    private int[] skyline;    // max y-value of dark pixels
    private int[] baseline;   // min y-value of dar pixels
    private int[] profile;    // number of dark pixles with a white right-neighbour

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

        projection = new int[width + 1];
        skyline = new int[width];
        baseline = new int[width];
        profile = new int[width];

        for (int x = 0; x < width; ++x) {
            projection[x + 1] = projection[x];
            skyline[x] = -1;
            baseline[x] = -1;
            for (int y = 0; y < height; ++y) {
                if (isBlack(x, y)) {
                    ++weight;
                    ++projection[x + 1];
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
     * @param xlow the lower x-value
     * @param xhigh the higher x-value
     * @return the number of dark pixels in the sub-image made of columns between
     * xlow and xhigh
     */
    public int weight(int xlow, int xhigh) {
        return projection[xhigh] - projection[xlow];
    }

    /**
     *
     * @return Number of columns in this image containing some dark pixels
     */
    public int shadow() {
        int value = 0;
        for (int x = 0; x < getWidth(); ++x) {
            if (projection[x + 1] > projection[x]) {
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
        int value = projection[getWidth()] > projection[getWidth() - 1] ? 1 : 0;

        for (int x = 1; x < getWidth() - 1; ++x) {
            if (projection[x + 1] > projection[x] && projection[x + 2] == projection[x + 1]) {
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
