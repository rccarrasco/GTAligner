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

import java.util.EnumMap;

/**
 *
 * @author rafa
 */
public class FeatureVector extends EnumMap<Feature, Integer> {

    public FeatureVector() {
        super(Feature.class);
    }

    public FeatureVector(FeatureVector other) {
        super(other);
    }

    /**
     * @return the result of adding the features with the weights given by the
     * lambda coefficients.
     */
    public double mixture() {
        double result = 0;

        for (Feature feature : Feature.values()) {
            result += feature.lambda * get(feature);
        }

        return result;
    }
}
