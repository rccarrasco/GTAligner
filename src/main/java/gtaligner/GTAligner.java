package gtaligner;

import java.io.IOException;
import java.util.Arrays;
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
        for (Character c : model.weights.keySet()) {
            if (Pattern.matches("\\p{Punct}", c.toString())) {
                model.setWeight(c, 1);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.err.println("Usage: GTAligner -u/-l/-r numiter datafile1 datafile2 ...");
        } else {
            TrainingMethod method;
            WeightModel model;
            int numiter;
            Sample sample;

            numiter = Integer.parseInt(args[1]);
            sample = new Sample(Arrays.copyOfRange(args, 2, args.length));
            
            System.err.println(sample);
            System.err.println(sample.charStats());

            switch (args[0]) {
                case "-u":
                    method = TrainingMethod.UNIFORM;
                    model = new WeightModel(sample, 400);
                    printErrors(sample, model, numiter, method);
                    System.err.println(model.toString());
                    break;
                case "-l":
                    method = TrainingMethod.LINEAR;
                    model = new WeightModel(sample, 400);
                    initPunct(model);
                    printErrors(sample, model, numiter, method);
                    System.err.println(model.toString());
                    break;
                case "-r":
                    method = TrainingMethod.RANDOM;
                    model = new WeightModel(sample, 400);
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
