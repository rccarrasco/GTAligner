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

    private final Map<Character, FeatureVector> features;

    public Model() {
        features = new HashMap<>();

        double lambda = 1.0 / Feature.values().length;
        for (Feature feature : Feature.values()) {
            feature.lambda = lambda;
        }
    }

    public Model(Set<Character> chars, FeatureVector defaultVector) {
        this();
        for (char c : chars) {
            FeatureVector vector = new FeatureVector(defaultVector);
            features.put(c, vector);
        }
    }

    public Model(Set<Character> chars, FeatureVector defaultVector,
            EnumMap<Feature, Double> lambdas) {
        this(chars, defaultVector);
        for (Feature feature : Feature.values()) {
            feature.lambda = lambdas.get(feature);
        }
    }

    /**
     * Apply this model to a specific character
     *
     * @param c a character
     * @param feature a particular feature
     * @return the feature value for c in this Model
     */
    public double evaluate(char c, Feature feature) {
        return features.get(c).get(feature);
    }

    /**
     * Apply this model to a specific character
     *
     * @param c a character
     * @return the value assigned to c by this Model
     */
    public double evaluate(char c) {
        double val;
        if (this.features.containsKey(c)) {
            val = features.get(c).mixture();
        } else {
            val = 0;
        }

        return val;
    }

    /**
     * Apply this model to a particular string
     *
     * @param s a string
     * @param feature a particular feature
     * @return the value assigned to c by this Model
     */
    public double evaluate(String s, Feature feature) {
        double result = 0;

        for (char c : s.toCharArray()) {
            result += evaluate(c, feature);
        }

        return result;
    }

    /**
     * Apply this model to a particular string
     *
     * @param s a string
     * @return the value assigned to c by this Model
     */
    public double evaluate(String s) {
        double result = 0;

        for (char c : s.toCharArray()) {
            result += evaluate(c);
        }

        return result;
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
            double expectedValue = sample.getMixture(n);
            double value = evaluate(content);

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
            double expectedValue = sample.getMixture(n);
            double value = evaluate(content);

            err += square(expectedValue - value);
            numchar += line.length();
        }

        return Math.sqrt(err * sample.size()) / numchar;
    }

    /**
     * Single iteration when training with uniform distribution among all
     * characters in TextLine.
     *
     * @param model the model to be trained
     * @param feature the text feature to be modeled
     */
    public void stepU(CharMap model, Feature feature) {
        CharMap deltas = new CharMap();

        for (int n = 0; n < size; ++n) {
            TextLine line = lines[n];
            double expectedValue = features.get(feature)[n];
            double value = model.getValue(line.getContent());
            double charDelta = (expectedValue - value) / line.length();

            for (Character c : line.getChars()) {
                deltas.addToValue(c, charDelta / charstats.getNumber(c));
            }
        }

        model.addToValues(deltas);
    }

    /**
     * Single iteration when training with proportional (linear) distribution
     *
     * @param model the model to be trained
     * @param feature the text feature to be modeled
     */
    public void stepL(CharMap model, Feature feature) {
        CharMap deltas = new CharMap();

        for (int n = 0; n < size; ++n) {
            TextLine line = lines[n];
            double expectedValue = features.get(feature)[n];
            double value = model.getValue(line.getContent());

            double lineDelta = expectedValue - value;
            double factor = lineDelta / value;

            for (Character c : line.getChars()) {
                double charDelta = factor * model.getValue(c);
                deltas.addToValue(c, charDelta / charstats.getNumber(c));
            }
        }

        model.addToValues(deltas);
    }

    /**
     * Single iteration when training with random variations
     *
     * @param model the current model to be trained
     * @param feature the text feature to be modeled
     * @param radius the maximal (uniform distribution) variation for every
     * parameter.
     */
    public void stepR(CharMap model, Feature feature, double radius) {
        CharMap altmodel = new CharMap(model);
        //new CharMap(model.keySet(), -0.5 * radius, 0.5 * radius);

        altmodel.randomize(0.05);
        if (errorPerChar(altmodel, feature) < errorPerChar(model, feature)) {
            model.putAll(altmodel);
        }
    }

    /**
     *
     * @param model
     * @param feature
     * @param method
     * @param numiter
     * @return average error per character at every iteration
     */
    public double[] train(CharMap model, Feature feature,
            TrainingMethod method, int numiter) {
        double[] errors = new double[numiter + 1];

        for (int n = 0; n < numiter; ++n) {
            errors[n] = errorPerChar(model, feature);
            switch (method) {
                case UNIFORM:
                    stepU(model, feature);
                    break;
                case LINEAR:
                    stepL(model, feature);
                    break;
                case RANDOM:
                    stepR(model, feature, 100);
                    break;
            }
            Messages.info(n + " " + errors[n]);
        }
        errors[numiter] = errorPerChar(model, feature);

        return errors;
    }

}
