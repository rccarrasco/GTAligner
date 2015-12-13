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

import java.util.Set;
import java.util.TreeSet;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author carrasco@ua.es
 */
public class TextLineTest {
    
    public TextLineTest() {
    }

    /**
     * Test of getContent method, of class TextLine.
     */
    @Test
    public void testGetContent() {
        System.out.println("getContent");
        TextLine instance = new TextLine("abba");
        String expResult = "abba";
        String result = instance.getContent();
        assertEquals(expResult, result);
    }

    /**
     * Test of getChars method, of class TextLine.
     */
    @Test
    public void testGetChars() {
        System.out.println("getChars");
        TextLine instance = new TextLine("abba");
        Set<Character> expResult = new TreeSet<>();
        expResult.add('a');
        expResult.add('b');
        Set<Character> result = instance.getChars();
        assertEquals(expResult, result);
    }
}
