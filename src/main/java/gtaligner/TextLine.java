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
 * A line of text (spaces are not considered)
 *
 * @author rafa
 */
public class TextLine {
//    String rawText;

    String text;
    int weight;

    public TextLine(String text, int weight) {
        //       this.rawText = text;
        this.text = text.replaceAll("\\p{Space}", "");
        this.weight = weight;
    }

    public String getText() {
        return text;
    }

    public int getWeight() {
        return weight;
    }

    public int length() {
        return text.length();
    }

    public char charAt(int pos) {
        return text.charAt(pos);
    }

    public double predictedWeight(WeightModel model) {
        double prediction = 0;

        for (int n = 0; n < text.length(); ++n) {
            prediction += model.get(text.charAt(n));
        }

        return prediction;
    }

    /**
     * Missing weight
     *
     * @param model
     * @return
     */
    public double delta(WeightModel model) {
        return weight - predictedWeight(model);
    }

}
