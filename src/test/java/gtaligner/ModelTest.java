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

import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author carrasco@ua.es
 */
public class ModelTest {

    public ModelTest() {
    }

    /**
     * Test of train method, of class Model.
     */
    @Test
    public void testTrain() {
        System.out.println("train");
        TextLine[] lines = new TextLine[2];
        lines[0] = new TextLine("io");
        lines[1] = new TextLine("oo");
        FeatureVector[] features = new FeatureVector[2];
        features[0] = new FeatureVector(666);
        features[1] = new FeatureVector(774);
        TextSample sample = new TextSample(lines, features);
        Model instance = new Model(sample.getChars(), 100);
        instance.train(sample, 1);
        double wi = instance.getFeatures('i').getValue(Feature.WEIGHT);
        double wo = instance.getFeatures('o').getValue(Feature.WEIGHT);
        //System.err.println(instance.getFeatures('i'));
        assertEquals(333, wi, 1e-3);
        assertEquals(369, wo, 1e-3);
        instance.train(sample, 1);
        wi = instance.getFeatures('i').getValue(Feature.WEIGHT);
        wo = instance.getFeatures('o').getValue(Feature.WEIGHT);
        assertEquals(315.92, wi, 1e-2);
        assertEquals(374.69, wo, 1e-2);
    }

    @Test
    public void toStringTest() {
        java.util.Set<Character> set = new java.util.HashSet<>();
        set.add('a');
        set.add('b');
        Model model = new Model(set, 1.0);
        FeatureVector vector = new FeatureVector(1.0);
        String result = model.toString(' ', "%.0f");
        String header = "char "
                + Arrays.asList(Feature.values()).toString().replaceAll("[\\[,\\]]", "");
        String expected = header + "\n"
                + "a " + vector.toString(' ', "%.0f")
                + "\nb " + vector.toString(' ', "%.0f") + "\n";
        System.out.println(result);
        System.out.println(expected);
        assertEquals(expected, result);
    }

}
