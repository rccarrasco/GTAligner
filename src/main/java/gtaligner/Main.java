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
            System.err.println("Usage: GTAligner [-n numiter] [-m method] [-f feature] img1 img2 ...");
            System.err.println("\tMethod can be u (uniform), l (linear) or r (random)");
            System.err.println("\tFeature can be s (shadow) or w (weight)");
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
                    case "-f":
                        switch (args[++n]) {
                            case "s":
                                feature = Feature.SHADOW;
                                break;
                            case "w":
                                feature = Feature.WEIGHT;
                                break;
                        }
                        break;
                    default:
                        filenames.add(arg);

                }
            }

            /*
             model = new Model(sample.getChars(), 100); // all values intitalised equal

             System.err.println("Sample with " + sample.size()
             + " files has been processed");
             double[] errors = model.train(sample, feature, method, numiter);

             // Output
             Messages.info("SAMPLE");
             Messages.info(sample.charStats().toCSV('\t'));
             System.out.println(model.toCSV('\t'));
             System.err.println("error = " + errors[errors.length - 1]);
             */
            // Computation
            sample = new TextSample(filenames);
            model = new Model(sample.getChars(), 100); // all values intitalised equal
            double[] errors = model.train(sample, numiter);
            System.out.println(model.toCSV('\t', "%.1f"));
            System.err.println("error = " + errors[errors.length - 1]);
            Messages.info("SAMPLE\n");
            Messages.info(sample.charStats().toCSV('\t'));
        }

    }

}
