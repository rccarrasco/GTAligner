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
 * @author carrasco@ua.es
 */
public class MutableDouble {

    private double value;

    public MutableDouble(double value) {
        this.value = value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public boolean isNaN() {
        return Double.isNaN(value);
    }

    public boolean isInfinite() {
        return Double.isInfinite(value);
    }

    public Double toDouble() {
        return value;
    }

    public void add(double term) {
        this.value += term;
    }

    public void multiply(double factor) {
        this.value *= factor;
    }
    
    @Override
    public String toString() {
        return Double.toString(value);
    }
}
