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
import gtaligner.io.TextReader;
import gtaligner.math.CharMap;
import gtaligner.math.CharCounter;
import java.util.EnumMap;
import java.util.List;
import java.util.Set;

/**
 * A sample of TextLines and the integer features associated to every TextLine
 *
 * @author rafa
 */
public class TextSample {

    int size;
    TextLine[] lines;
    EnumMap<Feature, int[]> features;
    CharCounter charstats;

    /**
     * Create a TextSample from a list of input files
     *
     * @param filenames list of filenames containing lines of text
     */
    public TextSample(List<String> filenames) {
        size = filenames.size();
        lines = new TextLine[size];
        features = new EnumMap<>(Feature.class);
        features.put(Feature.SHADOW, new int[size]);
        features.put(Feature.WEIGHT, new int[size]);
        charstats = new CharCounter();

        for (int n = 0; n < size; ++n) {
            String name = filenames.get(n);
            BWImage image = new BWImage(name);
            String basename = name.substring(0, name.lastIndexOf('.'));
            String text = TextReader.readFile(basename + ".txt");
            TextLine line = new TextLine(text);

            lines[n] = line;
            features.get(Feature.SHADOW)[n] = image.shadow();
            features.get(Feature.WEIGHT)[n] = image.weight();
            charstats.increment(line.toCharArray());
        }
    }

     /**
     * Create a TextSample from an array of input files
     *
     * @param filenames array of filenames containing lines of text
     */
    public TextSample(String[] filenames) {
        this(java.util.Arrays.asList(filenames));
    }
    
    /**
     *
     * @return all text lines in this sample
     */
    public TextLine[] getLines() {
        return lines;
    }

    /**
     *
     * @return all characters in this TextSample
     */
    public Set<Character> getChars() {
        return charstats.keySet();
    }

    /**
     *
     * @return character statistics in text
     */
    public CharCounter charStats() {
        return charstats;
    }

    /**
     * Average weight error (per character).
     *
     * @param model a CharMap modeling a character feature
     * @param feature the feature with respect to which the error rate is
     * computed
     * @return the average error per character when the feature for very line is
     * compared with the values provided by the model.
     */
    public double errorPerChar(CharMap model, Feature feature) {
        double err = 0;
        int numchar = 0;

        for (int n = 0; n < size; ++n) {
            TextLine line = lines[n];
            double expectedValue = features.get(feature)[n];
            double value = model.getValue(line.getContent());

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
     * @param model a CharMap modeling a character feature
     * @param feature the feature with respect to which the error rate is
     * computed
     * @return the average error per character when real line weights are
     * compared with weights provided by the model.
     */
    public double qerrorPerChar(CharMap model, Feature feature) {
        double err = 0;
        int numchar = 0;

        for (int n = 0; n < size; ++n) {
            TextLine line = lines[n];
            double expectedValue = features.get(feature)[n];
            double value = model.getValue(line.getContent());

            err += square(expectedValue - value);
            numchar += line.length();
        }

        return Math.sqrt(err * size) / numchar;
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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (TextLine line : lines) {
            builder.append(line.toString()).append("\n");
        }

        return builder.toString();
    }
}
