/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gtaligner;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author rafa
 */
public class BWImageTest {

    public BWImageTest() {
    }

    /**
     * Test of weight method, of class BImage.
     *
     * @throws java.net.URISyntaxException
     */
    @Test
    public void testWeight() throws URISyntaxException {
        System.out.println("weight");
        URL resourceUrl = getClass().getResource("/chars/n.jpeg");
        File file = new File(resourceUrl.toURI());
        BWImage instance = new BWImage(file);
        int expResult = 442;
        int result = instance.weight();
        assertEquals(expResult, result);

        resourceUrl = getClass().getResource("/lines/sample1.jpeg");
        file = new File(resourceUrl.toURI());
        instance = new BWImage(file);
        expResult = 17052;
        result = instance.weight();
        assertEquals(expResult, result);
    }

    /**
     * Test of footprint method, of class BImage.
     *
     * @throws java.net.URISyntaxException
     */
    @Test
    public void testShadow() throws URISyntaxException {
        System.out.println("shadow");
        URL resourceUrl = getClass().getResource("/chars/n.jpeg");
        File file = new File(resourceUrl.toURI());

        BWImage instance = new BWImage(file);
        int expResult = 29;
        int result = instance.shadow();
        assertEquals(expResult, result);

        resourceUrl = getClass().getResource("/lines/sample1.jpeg");
        file = new File(resourceUrl.toURI());
        instance = new BWImage(file);
        expResult = 1028;
        result = instance.shadow();
        assertEquals(expResult, result);
    }

}
