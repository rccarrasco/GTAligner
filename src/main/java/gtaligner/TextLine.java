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

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * A line of text with normalized whitespace and an integer weight (for example,
 * the number of dark pixels in this line).
 *
 * @author rafa
 */
public class TextLine {

    String content;
    int weight;
    Set<Character> chars;

    /**
     * Get the set of characters used in this line
     *
     * @param s a string
     * @return the set of characters used in this line
     */
    private Set<Character> getChars(String s) {
        Set<Character> set = new HashSet<>();

        for (int n = 0; n < s.length(); ++n) {
            set.add(s.charAt(n));
        }
        return set;
    }

    /**
     * Create a TextLine with this content and weight
     *
     * @param content the textual content
     * @param weight the associated weight
     */
    public TextLine(String content, int weight) {
        this.content = content.trim().replaceAll("\\p{Space}+", " ");
        this.weight = weight;
        this.chars = getChars(content);
    }

    /**
     * Create a TextLine with the text in file and the number of dark pixels as
     * weight
     *
     * @param file a file containing the transcription of the image
     * @param img the textual image
     * @param threshold which the lower limit for a dark pixel (a gray level
     * between 0 and 1)
     */
    public TextLine(File file, BImage img, double threshold) {
        content = TextReader.read(file);
        weight = img.weight(threshold);
        chars = getChars(content);
    }

    /**
     *
     * @return the textual content in this TextLine
     */
    public String getContent() {
        return content;
    }

    /**
     *
     * @return the weight associated to this TextLine
     */
    public int getWeight() {
        return weight;
    }

    /**
     *
     * @return the set of characters contained in this TextLine
     */
    public Set<Character> getChars() {
        return chars;
    }

    /**
     *
     * @return the number of characters in this TextLine
     */
    public int length() {
        return content.length();
    }

    /**
     *
     * @param pos a position
     * @return the character at the specified position
     */
    public char charAt(int pos) {
        return content.charAt(pos);
    }

    /**
     * @return a textual representation
     */
    @Override
    public String toString() {
        return content + " " + weight;
    }

}
