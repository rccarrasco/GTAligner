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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author rafa
 */
public class Sample {

    List<TextLine> lines;

    private void initializeModel(WeightModel model, double value) {
        for (TextLine line : lines) {
            String text = line.getText();
            for (int n = 0; n < line.length(); ++n) {
                model.put(line.charAt(n), value);
            }
        }
    }

    private double square(double x) {
        return x * x;
    }

    public double errorPerChar(WeightModel model) {
        double err = 0;
        int numchar = 0;

        for (TextLine line : lines) {
            double linePredictedWeight = model.get(line.getText());
            //System.out.println(line.getWeight() + " pred=" + linePredictedWeight);
            err += Math.abs(line.getWeight() - linePredictedWeight);
            //err += square(line.getWeight() - linePredictedWeight);
            numchar += line.length();
        }

        return (err / numchar);
    }

    /**
     * Single iteration training with uniform distribution among all characters
     * in line
     *
     * @param model
     */
    public void stepU(WeightModel model) {
        WeightModel deltas = new WeightModel();
        double error = 0;

        for (TextLine line : lines) {
            double linePredictedWeight = model.get(line.getText());
            double lineDelta = line.getWeight() - linePredictedWeight;
//            System.out.println("delta=" + lineDelta + " " + line.getText() + " " + line.length());
            double charDelta = lineDelta / (lines.size() * line.length());

            for (Character c : line.getChars()) {
                deltas.add(c, charDelta);
            }
        }
        //System.out.println("err=" + errorPerChar(model));
        //System.out.println("deltas=" + deltas);
        model.add(deltas);
        //System.out.println("err=" + errorPerChar(model));
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
            double linePredictedWeight = model.get(line.getText());
            double lineDelta = line.getWeight() - linePredictedWeight;

            for (Character c : line.getChars()) {
                double charPredictedWeight = model.get(c);
                double charDelta
                        = (lineDelta * charPredictedWeight)
                        / (lines.size() * linePredictedWeight);

                deltas.add(c, charDelta);
            }
        }
        model.add(deltas);
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

        if (method == TrainingMethod.LINEAR) {
            initializeModel(model, 1);
        }

        for (int n = 0; n < numiter; ++n) {
            //System.out.println(model);
            errors[n] = errorPerChar(model);
            switch (method) {
                case UNIFORM:
                    stepU(model);
                case LINEAR:
                    stepL(model);
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

    public Sample(List<TextLine> lines) {
        this.lines = lines;
    }

    public Sample(File file) throws IOException {
        lines = readFile(file);
    }

}
