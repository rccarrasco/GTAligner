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
public class CharCounterTest {
    
    public CharCounterTest() {
    }

    /**
     * Test of increment method, of class CharCounter.
     */
    @Test
    public void testIncrement_Character() {
        System.out.println("increment");
        CharCounter instance = new CharCounter();
        String s = "guadalajara";
        for (int n = 0; n < s.length(); ++n) {
            instance.increment(s.charAt(n));
        }
        
        assertEquals(5, instance.getNumber('a'));
        instance.increment(s.toCharArray());
        assertEquals(10, instance.getNumber('a'));
    }

 
    
}
