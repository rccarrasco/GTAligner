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
package gtaligner.math;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author rafa
 */
public class CharCounter extends HashMap<Character, MutableInt> {

    public void setNumber(Character c, int value) {
        get(c).setValue(value);
    }

    public int getNumber(Character c) {
        if (containsKey(c)) {
            return get(c).getValue();
        } else {
            return 0;
        }
    }

    public void add(Character c, int value) {
        if (containsKey(c)) {
            get(c).add(value);
        } else {
            put(c, new MutableInt(value));
        }
    }
    
    public void increment(Character c) {
        add(c, 1);
    }
    
     /**
     * Create a CSV representation
     *
     * @param separator the column separator
     * @return the content in CSV format
     */
    public String toCSV(char separator) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<Character, MutableInt> entry : entrySet()) {
            builder.append("'")
                    .append(entry.getKey())
                    .append("'")
                    .append(separator)
                    .append(entry.getValue().getValue())
                    .append('\n');
        }

        return builder.toString();
    }
    
}
