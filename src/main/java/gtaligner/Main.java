package gtaligner;

import gtaligner.io.Messages;
import gtaligner.math.CharMap;
import java.util.Arrays;

/**
 *
 * @author rafa
 */
public class Main {
    /*
     private static void printErrors(Sample sample, CharMap model, int numiter, TrainingMethod method) {
     double[] errors = sample.train(model, method, numiter);
     for (int n = 0; n < errors.length; ++n) {
     System.out.println(n + " " + errors[n]);
     }
     }
     */

    public static void main(String[] args) {

        if (args.length < 3) {
            System.err.println("Usage: GTAligner -u/-l/-r numiter img1 img2 ...");
        } else {
            TrainingMethod method;
            CharMap model;
            int numiter;
            TextSample sample;

            System.err.println("Working dir: " + System.getProperty("user.dir"));

            // Input data
            switch (args[0]) {
                case "-u":
                    method = TrainingMethod.UNIFORM;
                    break;
                case "-l":
                    method = TrainingMethod.LINEAR;
                    break;
                case "-r":
                    method = TrainingMethod.RANDOM;
                    break;
                default:
                    method = TrainingMethod.UNIFORM;
                    break;
            }
            numiter = Integer.parseInt(args[1]);
            sample = new TextSample(Arrays.copyOfRange(args, 2, args.length));

            // Computation
            model = new CharMap(sample.getChars(), 400,
                    Character.OTHER_PUNCTUATION, 100);
            double[] errors = sample.train(model, method, numiter);

            // Output
            Messages.info("SAMPLE");
            Messages.info(sample.charStats().toCSV('\t'));
            System.out.println(model.toCSV('\t'));
            System.err.println("error = " + errors[errors.length - 1]);

        }

    }

}
