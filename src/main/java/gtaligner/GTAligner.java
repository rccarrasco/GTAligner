package gtaligner;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;

/**
 *
 * @author rafa
 */
public class GTAligner {

    private static void printErrors(Sample sample, WeightModel model, int numiter, TrainingMethod method) {
        double[] errors = sample.train(model, method, numiter);
        for (int n = 0; n < errors.length; ++n) {
            System.out.println(n + " " + errors[n]);
        }
    }
    
    private static void initPunct(WeightModel model) {
        for (Character c: model.weights.keySet()) {
            if (Pattern.matches("\\p{Punct}",c.toString())) {
                model.setWeight(c, 1);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.err.println("Usage: GTAligner datafile numiter -u/-l");
        } else {
            File file = new File(args[0]);
            int numiter = Integer.parseInt(args[1]);
            Sample sample = new Sample(file);
            TrainingMethod method;
            WeightModel model;

            switch (args[2]) {
                case "-u":
                    method = TrainingMethod.UNIFORM;
                    model = new WeightModel(sample, 100);
                    printErrors(sample, model, numiter, method);
                    System.err.println(model.toString());
                    break;
                case "-l":
                    method = TrainingMethod.LINEAR;
                    model = new WeightModel(sample, 100);
                    initPunct(model);
                    printErrors(sample, model, numiter, method);
                    System.err.println(model.toString());
                    break;
                default:
                    System.err.println("Usage: GTAligner datafile numiter -u/-l");
                    break;
            }

        }
    }
}
