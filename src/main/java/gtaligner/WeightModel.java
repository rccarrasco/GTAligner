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

import java.util.HashMap;
import java.util.Map;

/**
 * Provides a weight (a real value) for every character
 *
 * @author rafa
 */
public final class WeightModel {

    Map<Character, Double> weights;

    public WeightModel() {
        weights = new HashMap<>();
    }

    /**
     * Create the initial  model for a sample
     * @param sample a Sample of TextLines
     * @param value the initial value for all weights
     */
    public WeightModel(Sample sample, double value) {
        weights = new HashMap<>();
        for (TextLine line : sample.getLines()) {
            for (Character c : line.getChars()) {
                setWeight(c, value);
            }
        }
    }

    /**
     *
     * @param c a character
     * @return its weight according to this model
     */
    public double weight(Character c) {
        if (weights.containsKey(c)) {
            return weights.get(c);
        } else {
            return 0;
        }
    }

    /**
     *
     * @param s a string of characters
     * @return its weight according to this model, obtained by adding the
     * weights of every character in the string.
     */
    public double weight(String s) {
        double value = 0;

        for (int n = 0; n < s.length(); ++n) {
            value += weight(s.charAt(n));
        }

        return value;
    }

    /**
     * Set weight
     *
     * @param c a character
     * @param value set the weight of c to value
     */
    public void setWeight(Character c, double value) {
        weights.put(c, value);
    }

    /**
     * Modify weight
     *
     * @param c a character
     * @param delta the value to be added to the weight of c
     */
    public void addToWeight(Character c, double delta) {
        weights.put(c, weight(c) + delta);
    }

    /**
     * Modify weights
     *
     * @param deltas values to be added to
     */
    public void addToWeight(WeightModel deltas) {
        for (Map.Entry<Character, Double> entry : deltas.weights.entrySet()) {
            addToWeight(entry.getKey(), entry.getValue());
        }

    }

    /**
     *
     * @return a string representation of the model
     */
    @Override
    public String toString() {
        return weights.toString();
    }

}
