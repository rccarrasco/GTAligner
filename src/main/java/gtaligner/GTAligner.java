package gtaligner;

import java.io.File;
import java.util.Arrays;

/**
 *
 * @author rafa
 */
public class GTAligner {

  
    public static void main(String[] args) {
        File file = new File("sample.txt");
        Sample sample = new Sample(file);
        TrainingMethod method = TrainingMethod.UNIFORM;
        
        WeightModel model = new WeightModel();
        
        double[] errors = sample.train(model, method, 5);
        
        System.out.println(Arrays.toString(errors));
    }
    
    
}
