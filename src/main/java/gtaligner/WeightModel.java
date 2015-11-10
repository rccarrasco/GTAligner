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
 *
 * @author rafa
 */
public class WeightModel {

    Map<Character, Double> weights;

    public WeightModel() {
        weights = new HashMap<>();
    }

    public double get(Character c) {
        if (weights.containsKey(c)) {
            return weights.get(c);
        } else {
            return 0;
        }
    }
    
    public double get(String s) {
        double weight = 0;
        
        for (int n  =0; n < s.length(); ++n) {
            weight += get(s.charAt(n));
        }
        
        return weight;
    }

    public double put(Character c, double delta) {
        return weights.put(c, delta);
    }

    public double add(Character c, double delta) {
        double previous = get(c);

        weights.put(c, previous + delta);
        return previous;
    }

    public void add(WeightModel deltas) {
        for (Map.Entry<Character, Double> entry : deltas.weights.entrySet()) {
            add(entry.getKey(), entry.getValue());
        }

    }

}
