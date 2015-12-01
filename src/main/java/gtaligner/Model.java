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

import gtaligner.io.Messages;
import gtaligner.math.CharMap;
import gtaligner.math.MutableDouble;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Provides a value for every character and feature
 *
 * @author rafa
 */
public class Model {

    private Map<Character, FeatureVector> features;

    public Model() {
        features = new HashMap<>();
        //Feature.select(Feature.WEIGHT);
        /*
         double lambda = 1.0 / Feature.values().length;
         for (Feature feature : Feature.values()) {
         feature.lambda = lambda;
         }
         */
    }

    public Model(Set<Character> chars, double value) {
        this();
        for (char c : chars) {
            FeatureVector vector = new FeatureVector(value);
            features.put(c, vector);
        }
    }

    public Model(Set<Character> chars, FeatureVector defaultVector) {
        this();
        for (char c : chars) {
            FeatureVector vector = new FeatureVector(defaultVector);
            features.put(c, vector);
        }
    }

    public Model(Set<Character> chars,
            EnumMap<Feature, Double> lambdas) {
        this();
        for (char c : chars) {
            FeatureVector vector = new FeatureVector(100);
            features.put(c, vector);
        }
        for (Feature feature : Feature.values()) {
            feature.lambda = lambdas.get(feature);
        }
    }

    public Model(Model other) {
        this();
        for (Map.Entry<Character, FeatureVector> entry : other.features.entrySet()) {
            Character c = entry.getKey();
            FeatureVector vector = new FeatureVector(entry.getValue());
            this.features.put(c, vector);
        }
    }

    /**
     * Apply this model to a specific character
     *
     * @param c a character
     * @param feature a particular feature
     * @return the feature value for c in this Model
     */
    public double getValue(char c, Feature feature) {
        return features.get(c).getValue(feature);
    }

    /**
     * Apply this model to a particular string
     *
     * @param s a string
     * @param feature a particular feature
     * @return the sum of values of the feature for every character in the
     * string, according to this Model
     */
    public double getValue(String s, Feature feature) {
        double result = 0;

        for (char c : s.toCharArray()) {
            result += features.get(c).getValue(feature);
        }

        return result;
    }

    /**
     *
     * @param c a character
     * @return the FeatureVector for c according to this Model
     */
    public FeatureVector getFeatures(char c) {
        return features.get(c);
    }

    /**
     * Apply this model to a particular string
     *
     * @param s a string
     * @return the sum of features assigned to every character in the string,
     * according to this Model.
     */
    public FeatureVector getFeatures(String s) {
        FeatureVector vector = new FeatureVector();

        for (char c : s.toCharArray()) {
            vector.add(getFeatures(c));
        }

        return vector;
    }

    public void addToValue(char c, Feature feature, double value) {
        features.get(c);
    }

    /**
     * Average error per character.
     *
     * @param sample a TextSample containing TextLines and line features
     * @return the average error per character when the features for very line
     * are compared with the values provided by the model.
     */
    public double errorPerChar(TextSample sample) {
        double err = 0;
        int numchar = 0;

        for (int n = 0; n < sample.size(); ++n) {
            TextLine line = sample.getLine(n);
            String content = line.getContent();
            double expectedValue = sample.getFeatures(n).mixture();
            double value = getFeatures(content).mixture();

            err += Math.abs(expectedValue - value);
            numchar += line.length();
        }

        return (err / numchar);
    }

    private double square(double x) {
        return x * x;
    }

    /**
     * Average quadratic error (per character).
     *
     * @param sample a TextSample containing TextLines and line features
     * @return the average quadratic error per character when the features for
     * very line are compared with the values provided by the model.
     */
    public double qerrorPerChar(TextSample sample) {
        double err = 0;
        int numchar = 0;

        for (int n = 0; n < sample.size(); ++n) {
            TextLine line = sample.getLine(n);
            String content = line.getContent();
            double expectedValue = sample.getFeatures(n).mixture();
            double value = getFeatures(content).mixture();

            err += square(expectedValue - value);
            numchar += line.length();
        }

        return Math.sqrt(err * sample.size()) / numchar;
    }

    private void addDeltas(CharMap map, Feature feature) {
        for (Map.Entry<Character, MutableDouble> entry : map.entrySet()) {
            features.get(entry.getKey()).get(feature).add(entry.getValue().toDouble());
        }
    }

    /**
     * Single iteration when training with uniform distribution among all
     * characters in TextLine.
     *
     * @param sample the TextSample sued for training
     * @param feature the text feature to be modeled
     */
    public void stepU(TextSample sample, Feature feature) {
        CharMap deltas = new CharMap();

        for (int n = 0; n < sample.size(); ++n) {
            TextLine line = sample.getLine(n);
            String content = line.getContent();
            double expectedValue = sample.getFeatures(n).mixture();
            double value = getFeatures(content).mixture();
            double charDelta = (expectedValue - value) / line.length();

            for (Character c : line.getChars()) {
                deltas.addToValue(c, charDelta / sample.getNumber(c));
            }
        }

        addDeltas(deltas, feature);
    }

    /**
     * Single iteration when training with proportional (linear) distribution
     *
     * @param sample the TextSample sued for training
     * @param feature the text feature to be modeled
     */
    public void stepL(TextSample sample, Feature feature) {
        CharMap deltas = new CharMap();

        for (int n = 0; n < sample.size(); ++n) {
            TextLine line = sample.getLine(n);
            String content = line.getContent();
            double expectedValue = sample.getFeatures(n).mixture();
            double value = getFeatures(content).mixture();
            double lineDelta = expectedValue - value;
            double factor = lineDelta / value;

            for (Character c : line.getChars()) {
                double charDelta = factor * getFeatures(c).mixture();
                deltas.addToValue(c, charDelta / sample.getNumber(c));
            }
        }

        addDeltas(deltas, feature);
    }

    /**
     * Single iteration when training with random variations
     *
     * @param sample the TextSample sued for training
     * @param feature the text feature to be modeled
     * @param radius the maximal (uniform distribution) variation for every
     * parameter.
     */
    public void stepR(TextSample sample, Feature feature, double radius) {
        Model altmodel = new Model();
        //new CharMap(model.keySet(), -0.5 * radius, 0.5 * radius);

        for (Map.Entry<Character, FeatureVector> entry : features.entrySet()) {
            Character c = entry.getKey();
            FeatureVector vector = new FeatureVector(entry.getValue());
            vector.randomize(radius);
            altmodel.features.put(c, vector);
        }

        if (altmodel.errorPerChar(sample) < this.errorPerChar(sample)) {
            features = altmodel.features;
        }
    }

    /**
     *
     * @param sample the TextSample used for training
     * @param feature
     * @param method
     * @param numiter
     * @return average error per character at every iteration
     */
    public double[] train(TextSample sample, Feature feature,
            TrainingMethod method, int numiter) {
        double[] errors = new double[numiter + 1];

        for (int n = 0; n < numiter; ++n) {
            errors[n] = errorPerChar(sample);
            switch (method) {
                case UNIFORM:
                    stepU(sample, feature);
                    break;
                case LINEAR:
                    stepL(sample, feature);
                    break;
                case RANDOM:
                    stepR(sample, feature, 100);
                    break;
            }
            Messages.info(n + " " + errors[n]);
        }
        errors[numiter] = errorPerChar(sample);

        return errors;
    }

    /**
     * Single iteration when training with proportional (linear) distribution
     *
     * @param sample the TextSample used for training
     */
    public void stepL(TextSample sample) {
        Model altmodel = new Model(features.keySet(), 0);

        for (int n = 0; n < sample.size(); ++n) {
            TextLine line = sample.getLine(n);
            String content = line.getContent();
            FeatureVector expected = sample.getFeatures(n);
            FeatureVector computed = getFeatures(content);
            for (Character c : content.toCharArray()) {
                int total = sample.getNumber(c);
                FeatureVector vector = this.features.get(c);
                FeatureVector altvector = altmodel.features.get(c);
                FeatureVector delta = new FeatureVector();
                for (Feature feature : Feature.values()) {
                    double value = vector.getValue(feature)
                            * expected.getValue(feature)
                            / computed.getValue(feature)
                            / total;
                    delta.put(feature, value);
                }
                altvector.add(delta);
            }
        }
        // Finally, update all FeatureVectors
        this.features = altmodel.features;
    }

    public double[] train(TextSample sample, int numiter) {
        double[] errors = new double[numiter + 1];

        for (int n = 0; n < numiter; ++n) {
            errors[n] = errorPerChar(sample);
            stepL(sample);
            Messages.info(n + " " + errors[n]);
        }
        errors[numiter] = errorPerChar(sample);

        return errors;
    }

    /**
     * Create a CSV representation
     *
     * @param separator the column separator
     * @return the content in CSV format
     */
    public String toCSV(char separator, String format) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<Character, FeatureVector> entry : features.entrySet()) {         
            builder.append("'")
                    .append(entry.getKey())
                    .append("'")
                    .append(separator)
                    .append(entry.getValue().toString(format))
                    .append('\n');
        }

        return builder.toString();
    }

}
