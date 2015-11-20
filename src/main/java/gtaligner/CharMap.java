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

import java.lang.Character.UnicodeBlock;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Maps characters to double values
 *
 * @author rafa
 */
public final class CharMap extends HashMap<Character, Double> {

    static Random random = new Random();

    /**
     * Default constructor
     */
    public CharMap() {
        super();
    }

    /**
     * Create a trivial map
     *
     * @param keys a set of characters
     * @param value the initial value for all characters
     */
    public CharMap(Set<Character> keys, double value) {
        for (Character c : keys) {
            put(c, value);
        }
    }

    /**
     * Create a trivial map
     *
     * @param keys a set of characters
     * @param defaultValue the default value for generic characters
     * @param block a Unicode block, for example, punctuation
     * @param value value for the characters in the specified block
     */
    public CharMap(Set<Character> keys, double defaultValue,
            UnicodeBlock block, double value) {
        for (Character c : keys) {
            if (Character.UnicodeBlock.of(c) == block) {
                put(c, value);
            } else {
                put(c, defaultValue);
            }
        }
    }

    /**
     * Create a radom map for the specified set of characters
     *
     * @param keys the set of characters
     * @param low the lower bound for values (inclusive)
     * @param high the higher bound for values (exclusive)
     */
    public CharMap(Set<Character> keys, double low, double high) {

        for (Character c : keys) {
            put(c, low + (high - low) * random.nextDouble());
        }
    }

    /**
     * Add random uniform noise to the map
     *
     * @param radius the maximum value for the noise
     */
    public void randomize(double radius) {
        for (Character c : keySet()) {
            addToValue(c, radius * random.nextDouble());
        }
    }

    /**
     *
     * @param c a character
     * @return the value associated to this character or zero if not stored
     */
    public double getValue(Character c) {
        if (containsKey(c)) {
            return get(c);
        } else {
            return 0;
        }
    }

    /**
     *
     * @param s a string of characters
     * @return the value associated to this string obtained as the sum of values
     * of the component characters
     */
    public double getValue(String s) {
        double value = 0;

        for (int n = 0; n < s.length(); ++n) {
            value += getValue(s.charAt(n));
        }

        return value;
    }

    /**
     * Set a value for a character
     *
     * @param c a character
     * @param value value to be associated with the specified character
     */
    public void setValue(Character c, double value) {
        put(c, value);
    }

    /**
     * Modify value
     *
     * @param c a character
     * @param delta the value (positive or negative) to be added to the
     * specified character
     */
    public void addToValue(Character c, double delta) {
        put(c, getValue(c) + delta);
    }

    /**
     * Add two maps: the value of characters in both maps is obtained as the
     * addition of their values.
     *
     * @param other another CharMap whose values must be added to this one.
     */
    public void addToValues(CharMap other) {
        for (Map.Entry<Character, Double> entry : other.entrySet()) {
            addToValue(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Create a CSV representation
     * @param separator the column separator
     * @return the content in CSV format
     */
    public String toCSV(char separator) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<Character, Double> entry : entrySet()) {
            builder.append(entry.getKey()).append('\t')
                    .append(entry.getValue())
                    .append('\n');
        }
        
        return builder.toString();
    }

}
