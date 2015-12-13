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

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author carrasco@ua.es
 */
public class BooleanMatrixTest {

    int height = 2;
    int width = 3;
    BooleanMatrix instance;

    public BooleanMatrixTest() {
        instance = new BooleanMatrix(width, height);
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                boolean val = (i >= j);
                instance.set(i, j, val);
            }
        }
    }

    /**
     * Test of set method, of class BooleanMatrix.
     */
    @Test
    public void testSet() {
        System.out.println("set");

        assertEquals(instance.getByte(0), 25);
        assertEquals(instance.bitcount(),3);

    }

    /**
     * Test of get method, of class BooleanMatrix.
     */
    @Test
    public void testGet() {
        System.out.println("get");
         for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                assertEquals(instance.get(i, j), (i >=j));
            }
        }
    }

}
