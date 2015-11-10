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
 * A line of text where spaces are not considered
 *
 * @author rafa
 */
public class TextLine {
    String text;
    int weight;
    Set<Character> chars;

    private Set<Character> getChars(String s) {
        Set<Character> set = new HashSet<>();
        for (int n = 0; n < s.length(); ++n) {
            set.add(s.charAt(n));
        }
        return set;
    }

    public TextLine(String text, int weight) {
        this.text = text.replaceAll("\\p{Space}", "");
        this.weight = weight;
        this.chars = getChars(this.text);
    }

    public String getText() {
        return text;
    }

    public int getWeight() {
        return weight;
    }

    public Set<Character> getChars() {
        return chars;
    }
    
    public int length() {
        return text.length();
    }

    public char charAt(int pos) {
        return text.charAt(pos);
    }

}
