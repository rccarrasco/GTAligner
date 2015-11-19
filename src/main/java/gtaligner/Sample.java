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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A sample of TextLines (weighted text lines)
 *
 * @author rafa
 */
public class Sample {

    List<TextLine> lines;

    /**
     * Copy constructor
     *
     * @param lines
     */
    public Sample(List<TextLine> lines) {
        this.lines = lines;
    }

    /**
     * Read text in one file
     *
     * @param file
     * @return
     * @throws IOException
     */
    private String readText(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        StringBuilder builder = new StringBuilder();

        while (reader.ready()) {
            builder.append(reader.readLine()).append("\n");
        }

        return builder.toString();
    }

    public Sample(String[] filenames) throws IOException {
        lines = new ArrayList<>();

        for (String name : filenames) {
            File imageFile = new File(name);
            BImage bimage = new BImage(imageFile);
            String basename = name.substring(0, name.lastIndexOf('.'));
            File textFile = new File(basename + ".txt");
            String text = readText(textFile);

            lines.add(new TextLine(text, bimage.weight()));
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
     * @return character statistics in text
     */
    public Map<Character, Integer> charStats() {
        Map<Character, Integer> map = new HashMap<>();
        for (TextLine line : lines) {
            String text = line.getText();
            for (int n = 0; n < text.length(); ++n) {
                Character c = text.charAt(n);
                if (map.containsKey(c)) {
                    map.put(c, map.get(c) + 1);
                } else {
                    map.put(c, 1);
                }

            }
        }
        return map;
    }

    /**
     * Average weight error (per character).
     *
     * @param model a WeightModel
     * @return the average per-character error when real line weights are
     * compared with weights provided by the model.
     */
    public double errorPerChar(WeightModel model) {
        double err = 0;
        int numchar = 0;

        for (TextLine line : lines) {
            String text = line.getText();

            err += Math.abs(line.getWeight() - model.weight(text));
            numchar += line.length();
        }

        return (err / numchar);
    }

    private double square(double x) {
        return x * x;
    }

    /**
     * Average quadratic weight error (per character).
     *
     * @param model a WeightModel
     * @return the average per-character error when real line weights are
     * compared with weights provided by the model.
     */
    public double qerrorPerChar(WeightModel model) {
        double err = 0;
        int numchar = 0;

        for (TextLine line : lines) {
            String text = line.getText();

            err += square(line.getWeight() - model.weight(text));
            numchar += line.length();
        }

        return Math.sqrt(err * lines.size()) / numchar;
    }

    /**
     * Single iteration training with uniform distribution among all characters
     * in TextLine.
     *
     * @param model the model to be optimized
     */
    public void stepU(WeightModel model) {
        WeightModel deltas = new WeightModel();
        double error = 0;

        for (TextLine line : lines) {
            String text = line.getText();
            double lineDelta = line.getWeight() - model.weight(text);
            double charDelta = lineDelta / (lines.size() * line.length());

            for (Character c : line.getChars()) {
                deltas.addToWeight(c, charDelta);
            }
        }

        model.addToWeight(deltas);
    }

    /**
     * Single iteration training with proportional (linear) distribution
     *
     * @param model
     */
    public void stepL(WeightModel model) {
        WeightModel deltas = new WeightModel();
        double error = 0;

        for (TextLine line : lines) {
            String text = line.getText();
            double lineDelta = line.getWeight() - model.weight(text);

            for (Character c : line.getChars()) {
                double charDelta
                        = (lineDelta * model.weight(c))
                        / (lines.size() * model.weight(text));

                deltas.addToWeight(c, charDelta);
            }
        }
        model.addToWeight(deltas);
    }

    /**
     * Single iteration training with random variations
     *
     * @param model
     */
    public void stepR(WeightModel model) {
        WeightModel altmodel = new WeightModel();

        for (Character c : model.getChars()) {
            double value = model.weight(c) + RandomGenerator.random(40) - 20;
            altmodel.setWeight(c, value);
        }
        if (errorPerChar(altmodel) < errorPerChar(model)) {
            model.weights = altmodel.weights; // Ugly
        }

    }

    /**
     *
     * @param model
     * @param method
     * @param numiter
     * @return average error per character at every iteration
     */
    public double[] train(WeightModel model, TrainingMethod method, int numiter) {
        double[] errors = new double[numiter + 1];

        for (int n = 0; n < numiter; ++n) {
            errors[n] = errorPerChar(model);
            switch (method) {
                case UNIFORM:
                    stepU(model);
                case LINEAR:
                    stepL(model);
                case RANDOM:
                    stepR(model);
            }
        }
        errors[numiter] = errorPerChar(model);
        return errors;
    }

    private List<TextLine> readFile(File file) throws IOException {
        List<TextLine> list = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(file));

        while (reader.ready()) {
            String text = reader.readLine();
            int weight = Integer.parseInt(reader.readLine().trim());
            TextLine line = new TextLine(text, weight);
            list.add(line);
        }

        return list;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (TextLine line : lines) {
            builder.append(line.source).append(line.weight).append("\n");
        }

        return builder.toString();
    }
}
