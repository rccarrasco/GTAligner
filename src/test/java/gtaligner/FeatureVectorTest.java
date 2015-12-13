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

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author carrasco@ua.es
 */
public class FeatureVectorTest {

    @Test
    public void FeatureVectorTest() {
        FeatureVector instance = new FeatureVector();
        FeatureVector copy = new FeatureVector(instance);
        copy.put(Feature.GAUGE, 1.0);
        assertEquals(1, copy.size());
        assertEquals(0, instance.size());
    }

    @Test
    public void toStringTest() {
        FeatureVector instance = new FeatureVector(1);
        String result = instance.toString(' ', "%.1f");
        int length = Feature.values().length;
        String expected = String.format("%0" + length + "d", 0)
                .replaceAll("0", "1.0 ").trim();
        assertEquals(expected, result);
    }
}
