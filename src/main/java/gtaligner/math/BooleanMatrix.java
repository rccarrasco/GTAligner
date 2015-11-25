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

/**
 *
 * @author rafa
 */
public class BooleanMatrix {

    int width;
    int height;
    byte[] data;

    public BooleanMatrix(int width, int height) {
        this.width = width;
        this.height = height;
        data = new byte[(7 + width * height) / 8];

    }

    public void set(int i, int j, boolean val) {
        int pos = i * width + j;

        if (val) {
            data[pos / 8] |= 1 << pos % 8;
        } else {
            data[pos / 8] &= ~(1 << pos % 8);
        }
    }

    public boolean get(int i, int j) {
        int pos = i * width + j;
        return (data[pos / 8] >> pos % 8 & 1) == 1;
    }

    public int bitcount() {
        int num = 0;

        for (byte b : data) {
            num += Integer.bitCount(b);
        }

        return num;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                if (get(i, j)) {
                    builder.append('1');
                } else {
                    builder.append('0');
                }
            }
            builder.append('\n');
        }
        return builder.toString();
    }

}
