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

import java.util.HashSet;
import java.util.Set;

/**
 * A line of text with normalized whitespace.
 *
 * @author rafa
 */
public class TextLine {

    String content;
    Set<Character> chars;

    /**
     * Create a TextLine with this content and weight
     *
     * @param content the textual content
     */
    public TextLine(String content) {
        this.content = content.trim().replaceAll("\\p{Space}+", " ");
        this.chars = new HashSet<>();

        for (char c : this.content.toCharArray()) {
            chars.add(c);
        }
    }

    /**
     *
     * @return the textual content in this TextLine
     */
    public String getContent() {
        return content;
    }

    /**
     * Converts this string to a new character array.
     *
     * @return a new character array whose contents are initialized to content
     * of this TextLine
     *
     */
    public char[] toCharArray() {
        return content.toCharArray();
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
        return content;
    }

}
