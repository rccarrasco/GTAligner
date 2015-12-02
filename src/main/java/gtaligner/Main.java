package gtaligner;

import gtaligner.io.Messages;
import java.util.ArrayList;
import java.util.List;

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

        if (args.length < 2) {
            System.err.println("Usage: GTAligner [-n numiter] [-m method] img1 img2 ...");
            System.err.println("\tMethod can be l (linear) or r (random)");
        } else {
            Model model;
            Feature feature = Feature.WEIGHT;
            //Feature.select(Feature.WEIGHT);
            TrainingMethod method = TrainingMethod.LINEAR;
            int numiter = 100;
            List<String> filenames = new ArrayList<>();
            TextSample sample;

            //System.err.println("Working dir: " + System.getProperty("user.dir"));
            // Input data
            for (int n = 0; n < args.length; ++n) {
                String arg = args[n];
                switch (arg) {
                    case "-n":
                        numiter = Integer.parseInt(args[++n]);
                        break;
                    case "-m":
                        switch (args[++n]) {
                            case "u":
                                method = TrainingMethod.UNIFORM;
                                break;
                            case "l":
                                method = TrainingMethod.LINEAR;
                                break;
                            case "r":
                                method = TrainingMethod.RANDOM;
                                break;
                        }
                        break;
                    default:
                        filenames.add(arg);
                }
            }

            // Computation
            sample = new TextSample(filenames);
            model = new Model(sample.getChars(), 1, 1000); 
            double[] errors = model.train(sample, numiter);
            System.out.println(sample.charStats().toCSV('\t'));
            System.out.println(model.toCSV('\t', "%.1f"));
            System.out.println("error = " + errors[errors.length - 1]);
        }

    }

}
