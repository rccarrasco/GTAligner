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

/**
 *
 * @author rafa
 */
public class MutableInt {

    private int value;

    public MutableInt(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void increment() {
        value++;
    }

    public void decrement() {
        value--;
    }

    public void add(int operand) {
        this.value += operand;
    }

    public Integer toInteger() {
        return value;
    }
    
    @Override
     public boolean equals(Object obj) {
        if (obj instanceof MutableInt) {
            return value == ((MutableInt) obj).value;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return value;
    }

   
    public int compareTo(Object obj) {
        MutableInt other = (MutableInt) obj;
        int anotherVal = other.value;
        return value < anotherVal ? -1 : (value == anotherVal ? 0 : 1);
    }
}
