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

import gtaligner.io.TextReader;
import gtaligner.math.CharCounter;
import java.io.File;
import java.util.List;
import java.util.Set;

/**
 * A sample of TextLines and the integer features associated to every TextLine
 *
 * @author rafa
 */
public class TextSample {

    private int size;
    private TextLine[] lines;
    private FeatureVector[] features;
    private CharCounter charstats;

    /**
     * Create a TextSample from a list of input files
     *
     * @param files list of files containing lines of text
     */
    public TextSample(List<File> files) {
        size = files.size();
        lines = new TextLine[size];
        features = new FeatureVector[size];
        charstats = new CharCounter();

        for (int n = 0; n < size; ++n) {
            File file = files.get(n);
            BWImage image = new BWImage(file);
            String path = file.getAbsolutePath();
            String dir = path.substring(0, path.lastIndexOf('.'));
            String text = TextReader.read(new File(dir + ".txt"));
            TextLine line = new TextLine(text);

            lines[n] = line;
            features[n] = image.getFeatures();
            charstats.increment(line.toCharArray());
        }
    }

    /**
     * Create a TextSample from an array of input files
     *
     * @param files array of files containing lines of text
     */
    public TextSample(File[] files) {
        this(java.util.Arrays.asList(files));
    }

    public TextSample(TextLine[] lines, FeatureVector[] features) {
        this.size = lines.length; 
        this.lines = lines;
        this.features = features;
        this.charstats = new CharCounter();

        for (TextLine line : lines) {
            charstats.increment(line.toCharArray());
        }
    }

    /**
     *
     * @return the number of lines in this sample
     */
    public int size() {
        return size;
    }

    /**
     *
     * @return all text lines in this sample
     */
    public TextLine[] getLines() {
        return lines;
    }

    /**
     *
     * @param n a line number
     * @return the n-th TextLine in this TextSample
     */
    public TextLine getLine(int n) {
        return lines[n];
    }

    /**
     *
     * @param n a line number
     * @return the FeatureVector of the n-th TextLine
     */
    public FeatureVector getFeatures(int n) {
        return features[n];
    }

    /**
     *
     * @param c a character
     * @return the number of characters identical to c in this TextSample
     */
    public int getNumber(char c) {
        return charstats.getNumber(c);
    }

    /**
     *
     * @return all characters in this TextSample
     */
    public Set<Character> getChars() {
        return charstats.keySet();
    }

    /**
     *
     * @return character statistics in text
     */
    public CharCounter charStats() {
        return charstats;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (TextLine line : lines) {
            builder.append(line.toString()).append("\n");
        }

        return builder.toString();
    }
}
