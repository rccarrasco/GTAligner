/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gtaligner;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author rafa
 */
public class SampleTest {
    
    public SampleTest() {
    }

    
     /**
     * Test of train method, of class Sample.
     */
    @Test
    public void testTrain() {
        System.out.println("train");
        
        String[] texts = {"hola amigo", "adiós amigo", "la miga mola"};
        int[] weights = {45, 50, 60};

        List<TextLine> list = new ArrayList<>();
        for (int n = 0; n < texts.length; ++n) {
            TextLine line = new TextLine(texts[n], weights[n]);
            list.add(line);
        }
        
        Sample instance = new Sample(list);
        int numiter = 4;
        WeightModel model = new WeightModel();
        
        double[] result = instance.train(model, TrainingMethod.LINEAR, numiter);
        double[] expResult = {4.3, 1.6, 1.0, 0.5, 0.3};
        
        assertEquals(result.length, numiter + 1);
        assertArrayEquals(expResult, result, 0.1);
    }
    
    /**
     * Test of errorPerChar method, of class Sample.
     */
    @Test
    public void testErrorPerChar() {
        System.out.println("errorPerChar");
        WeightModel model = null;
        Sample instance = null;
        double expResult = 0.0;
        /**
        double result = instance.errorPerChar(model);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
        * */
    }

    
   
    
}
