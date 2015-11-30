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

import gtaligner.math.MutableDouble;
import static java.lang.Math.random;
import java.util.EnumMap;

/**
 *
 * @author rafa
 */
public class FeatureVector extends EnumMap<Feature, MutableDouble> {

    public FeatureVector() {
        super(Feature.class);
    }

    public FeatureVector(FeatureVector other) {
        super(other);
    }

    public FeatureVector(double value) {
        this();
        for (Feature feature : Feature.values()) {
            put(feature, new MutableDouble(value));
        }
    }

    /**
     *
     * @param feature a feature
     * @return the value of this feature
     */
    public double getValue(Feature feature) {
        return get(feature).getValue();
    }

    /**
     *
     * @param feature a feature
     * @param value the value for the feature
     * @return the previous value associated to this feature
     */
    public MutableDouble put(Feature feature, Double value) {
        return put(feature, new MutableDouble(value));
    }

    /**
     * @return the result of adding the features with the weights given by the
     * lambda coefficients.
     */
    public double mixture() {
        double result = 0;

        for (Feature feature : Feature.values()) {
            result += feature.lambda * get(feature).getValue();
        }

        return result;
    }

    /**
     * Add a random noise to the values stored in this FeatureVector
     *
     * @param radius a maximal rate for the variation of every single feature.
     */
    public void randomize(double radius) {
        for (MutableDouble value : values()) {
            double factor = radius * (random() - 0.5);
            value.add(factor * value.getValue());
        }
    }

    /**
     * Add two FeatureVectors
     *
     * @param other another FEatureVector
     */
    public void add(FeatureVector other) {
        for (Feature feature : Feature.values()) {
            if (this.containsKey(feature)) {
                this.get(feature).add(other.getValue(feature));
            } else {
                put(feature, other.getValue(feature));
            }
        }
    }

    @Override
    public String toString() {
        return this.values().toString();
    }
}
