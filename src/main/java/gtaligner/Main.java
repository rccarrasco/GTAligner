package gtaligner;

import gtaligner.io.Messages;
import gtaligner.math.CharMap;
import java.io.IOException;
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

    public static void main(String[] args) throws IOException {
        Messages.info("LOG FILE");
        if (args.length < 3) {
            System.err.println("Usage: GTAligner -u/-l/-r numiter img1 img2 ...");
        } else {
            TrainingMethod method;
            CharMap model;
            int numiter;
            TextSample sample;

            numiter = Integer.parseInt(args[1]);
            sample = new TextSample(Arrays.copyOfRange(args, 2, args.length));

            Messages.info("SAMPLE");
            Messages.info(sample.charStats().toString());

            model = new CharMap(sample.getChars(), 400);

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

            double[] errors = sample.train(model, method, numiter);

            System.out.println(model.toCSV('\t'));
            System.out.println("error = " + sample.errorPerChar(model));
        }

    }

}
