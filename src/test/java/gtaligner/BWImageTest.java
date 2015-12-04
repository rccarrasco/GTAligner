/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gtaligner;

import java.io.File;
import java.io.IOException;
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
    public void testWeight() throws URISyntaxException, IOException {
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
     * Test of shadow method, of class BImage.
     *
     * @throws java.net.URISyntaxException
     */
    @Test
    public void testShadow() throws URISyntaxException, IOException {
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

    /**
     * Test of gauge method, of class BImage.
     *
     * @throws java.net.URISyntaxException
     */
    @Test
    public void testGauge() throws URISyntaxException, IOException {
        System.out.println("gauge");
        URL resourceUrl = getClass().getResource("/chars/n.jpeg");
        File file = new File(resourceUrl.toURI());

        BWImage instance = new BWImage(file);
        int expResult = 557;
        int result = instance.gauge();
        assertEquals(expResult, result);

        resourceUrl = getClass().getResource("/lines/sample1.jpeg");
        file = new File(resourceUrl.toURI());
        instance = new BWImage(file);
        expResult = 26296;
        result = instance.gauge();
        assertEquals(expResult, result);
    }

    /**
     * Test of profile method, of class BImage.
     *
     * @throws java.net.URISyntaxException
     */
    @Test
    public void testProfileE() throws URISyntaxException, IOException {
        System.out.println("gauge");
        URL resourceUrl = getClass().getResource("/chars/n.jpeg");
        File file = new File(resourceUrl.toURI());

        BWImage instance = new BWImage(file);
        int expResult = 60;
        int result = instance.profileE();
        assertEquals(expResult, result);

        resourceUrl = getClass().getResource("/lines/sample1.jpeg");
        file = new File(resourceUrl.toURI());
        instance = new BWImage(file);
        expResult = 2255;
        result = instance.profileE();
        assertEquals(expResult, result);
    }

    @Test
    public void testBwcols() throws URISyntaxException, IOException {
        System.out.println("bwcols");
        URL resourceUrl = getClass().getResource("/chars/n.jpeg");
        File file = new File(resourceUrl.toURI());

        BWImage instance = new BWImage(file);
        int expResult = 1;
        int result = instance.bwcols();
        assertEquals(expResult, result);
        
        resourceUrl = getClass().getResource("/lines/sample1.jpeg");
        file = new File(resourceUrl.toURI());
        instance = new BWImage(file);
        expResult = 46;
        result = instance.bwcols();
        assertEquals(expResult, result);
    }

}
