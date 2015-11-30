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

/**
 *
 * @author rafa
 */
public enum Feature {

    WEIGHT, SHADOW, GAUGE, PROFILE_E;

    /**
     * The mixture coefficients
     */
    public double lambda = 1.0;

    /**
     *
     * @param mainFeature the only feature to be considered
     */
    public static void select(Feature mainFeature) {
        for (Feature feature : Feature.values()) {
            feature.lambda = (feature == mainFeature) ? 1 : 0;
        }
    }
}
