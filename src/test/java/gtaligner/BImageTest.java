/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gtaligner;

import java.io.File;
import java.net.URL;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author rafa
 */
public class BImageTest {

    public BImageTest() {
    }

    /**
     * Test of weight method, of class BImage.
     */
    @Test
    public void testWeight() {
        System.out.println("weight");

        URL url = this.getClass().getResource("/samples/p00-r2-00.jpeg");
        File file = new File(url.getFile());

        BImage instance = new BImage(file);
        int expResult = 17052;
        int result = instance.weight(0.5);
        assertEquals(expResult, result);

    }

}
