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

import gtaligner.io.TextReader;
import gtaligner.math.CharMap;
import gtaligner.math.CharCounter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * A sample of TextLines (weighted text lines)
 *
 * @author rafa
 */
public class TextSample {

    List<TextLine> lines;
    CharCounter charstats;

    /**
     * Create a TExtSampel from an array of input files
     *
     * @param filenames an array of filenames
     */
    public TextSample(String[] filenames) {
        lines = new ArrayList<>();
        charstats = new CharCounter();

        for (String name : filenames) {
            BImage bimage = new BImage(name);
            String basename = name.substring(0, name.lastIndexOf('.'));
            String text = TextReader.readFile(basename + ".txt");
            TextLine line = new TextLine(text, bimage.weight(0.5));
            String content = line.getContent();

            lines.add(line);
            for (int n = 0; n < content.length(); ++n) {
                Character c = content.charAt(n);
                charstats.increment(c);
            }
        }
    }

    /**
     *
     * @return all text lines in this sample
     */
    public List<TextLine> getLines() {
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
     * @param model a CharMap modeling character weights
     * @return the average error per character when real line weights are
     * compared with weights provided by the model.
     */
    public double errorPerChar(CharMap model) {
        double err = 0;
        int numchar = 0;

        for (TextLine line : lines) {
            String text = line.getContent();

            err += Math.abs(line.getWeight() - model.getValue(text));
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
     * @param model a CharMap modeling character weights
     * @return the average error per character when real line weights are
     * compared with weights provided by the model.
     */
    public double qerrorPerChar(CharMap model) {
        double err = 0;
        int numchar = 0;

        for (TextLine line : lines) {
            String text = line.getContent();

            err += square(line.getWeight() - model.getValue(text));
            numchar += line.length();
        }

        return Math.sqrt(err * lines.size()) / numchar;
    }

    /**
     * Single iteration when training with uniform distribution among all
     * characters in TextLine.
     *
     * @param model the model to be optimized
     */
    public void stepU(CharMap model) {
        CharMap deltas = new CharMap();

        for (TextLine line : lines) {
            String text = line.getContent();
            double lineDelta = line.getWeight() - model.getValue(text);
            double charDelta = lineDelta / line.length();

            for (Character c : line.getChars()) {
                deltas.addToValue(c, charDelta / charstats.getNumber(c));
            }
        }

        model.addToValues(deltas);
    }

    /**
     * Single iteration when training with proportional (linear) distribution
     *
     * @param model
     */
    public void stepL(CharMap model) {
        CharMap deltas = new CharMap();

        for (TextLine line : lines) {
            String text = line.getContent();
            double lineDelta = line.getWeight() - model.getValue(text);
            double factor = lineDelta / model.getValue(text);

            for (Character c : line.getChars()) {
                double charDelta = factor * model.getValue(c);
                double contri = lineDelta * model.getValue(c) / model.getValue(text);

                deltas.addToValue(c, charDelta / charstats.getNumber(c));
            }
        }
        
        model.addToValues(deltas);
    }

    /**
     * Single iteration when training with random variations
     *
     * @param model the current model
     * @param radius the maximal (uniform distribution) variation for every
     * parameter.
     */
    public void stepR(CharMap model, double radius) {
        CharMap altmodel = new CharMap(model.keySet(), -0.5 * radius, 0.5 * radius);

        altmodel.addToValues(model, 1.0);
        if (errorPerChar(altmodel) < errorPerChar(model)) {
            model.putAll(altmodel);
        }

    }

    /**
     *
     * @param model
     * @param method
     * @param numiter
     * @return average error per character at every iteration
     */
    public double[] train(CharMap model, TrainingMethod method, int numiter) {
        double[] errors = new double[numiter + 1];

        for (int n = 0; n < numiter; ++n) {
            errors[n] = errorPerChar(model);
            switch (method) {
                case UNIFORM:
                    stepU(model);
                    break;
                case LINEAR:
                    stepL(model);
                    break;
                case RANDOM:
                    stepR(model, 100);
                    break;
            }
        }
        errors[numiter] = errorPerChar(model);
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
