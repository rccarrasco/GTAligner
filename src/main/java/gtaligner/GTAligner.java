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

            double[] errors = sample.train(model, method, 50);

            System.out.println(Arrays.toString(errors));
        }
    }
    /**
     * String[] texts = {"hola amigo", "adiós amigo", "la miga mola"}; int[]
     * weights = {45, 50, 60};
     *
     * lines = new ArrayList<>();
     *
     * for (int n = 0; n < texts.length; ++n) { TextLine line = new
     * TextLine(texts[n], weights[n]); lines.add(line); }
        *
     */
}
