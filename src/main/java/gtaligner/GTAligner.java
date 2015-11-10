package gtaligner;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 *
 * @author rafa
 */
public class GTAligner {

    public static void main(String[] args) throws IOException {
        if (args.length  < 1) {
            System.err.println("Usage: GTAligner datafile numiter");
        } else {
            File file = new File(args[0]);
            int numiter = Integer.parseInt(args[1]);
            Sample sample = new Sample(file);
            TrainingMethod method = TrainingMethod.UNIFORM;
            WeightModel model = new WeightModel();
            
            if (args.length > 2) {
                method = TrainingMethod.LINEAR;
            }

            double[] errors = sample.train(model, method, numiter);

            System.out.println(Arrays.toString(errors));
        }
    }
}
