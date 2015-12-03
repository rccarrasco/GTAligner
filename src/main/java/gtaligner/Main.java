package gtaligner;

import gtaligner.io.FileExtensionFilter;
import gtaligner.io.Messages;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author rafa
 */
public class Main {

    private final static String[] extensions = {".png", ".jpeg", ".jpg"};

    private static List<File> imageFiles(String path) {
        File dir = new File(path);
        File[] array = dir.isDirectory()
                ? dir.listFiles(new FileExtensionFilter(extensions))
                : new File[0];

        return Arrays.asList(array);
    }

    /**
     * The main function
     *
     * @param args array of parameters
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: GTAligner [-n numiter] [-m method] img1 img2 ...");
            System.err.println("\tMethod can be l (linear) or r (random)");
        } else {
            Model model;
            //Feature.select(Feature.WEIGHT);
            TrainingMethod method = TrainingMethod.LINEAR;
            int numiter = 100;
            List<File> files = new ArrayList<>();
            TextSample sample;

            System.err.println("Working dir: " + System.getProperty("user.dir"));

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
                    case "-d":
                        String dir = args[++n];
                        files.addAll(imageFiles(dir));
                        break;
                    default:
                        files.add(new File(arg));
                }
            }

            // Computation
            sample = new TextSample(files);
            model = new Model(sample.getChars(), 1, 1000);
            double[] errors = model.train(sample, numiter);
            System.out.println(sample.charStats().toCSV('\t'));
            System.out.println(model.toString('\t', "%.1f"));
            System.out.println("error = " + errors[errors.length - 1]);

            model.printInfo(sample, Feature.WEIGHT);

        }
    }

}
