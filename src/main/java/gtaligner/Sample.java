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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author rafa
 */
public class Sample {

    List<TextLine> lines;

    public double errorPerChar(WeightModel model) {
        double err = 0;
        int numchar = 0;

        for (TextLine line : lines) {
            err += Math.abs(line.delta(model));
            numchar += line.length();
        }

        return err / numchar;
    }

    /**
     * Single iteration training
     *
     * @param model
     */
    public void step(WeightModel model) {
        WeightModel deltas = new WeightModel();
        double error = 0;

        for (TextLine line : lines) {
            double lineDelta = line.delta(model);

            for (int n = 0; n < line.length(); ++n) {
                double charDelta = lineDelta / (lines.size() * line.length());

                deltas.add(line.charAt(n), charDelta);
            }
        }
        model.add(deltas);
    }

    /**
     *
     * @param model
     * @param numiter
     * @return average error per character at every iteration
     */
    public double[] train(WeightModel model, int numiter) {
        double[] errors = new double[numiter + 1];

        for (int n = 0; n < numiter; ++n) {
            errors[n] = errorPerChar(model);
            step(model);
        }
        errors[numiter] = errorPerChar(model);
        return errors;
    }

    public Sample(File file) {
        /**
         * To be implemented
         */
        String[] texts = {"hola amigo", "adiós amigo", "la miga mola"};
        int[] weights = {45, 50, 60};

        lines = new ArrayList<>();

        for (int n = 0; n < texts.length; ++n) {
            TextLine line = new TextLine(texts[n], weights[n]);
            lines.add(line);
        }
    }

}
