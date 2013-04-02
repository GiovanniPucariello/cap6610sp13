package ml.core;

import java.util.Vector;

/*
 * This class implements the output node in the Perceptron.
 */
public class OutputNode
{
    double[] weights;
    
    // This constructor initializes the weights to a random value between 0 and
    // 1.
    OutputNode(int numberOfHiddenNodes)
    {
        weights = new double[numberOfHiddenNodes];
        for (int i = 0; i < numberOfHiddenNodes; ++i)
            weights[i] = Math.random();
    }
    
    // This method generates the output of the output node.
    double output(double[] hiddenOutput)
    {
        double temp = 0;
        
        for (int i = 1; i < hiddenOutput.length; ++i)
            temp += weights[i] * hiddenOutput[i];
        
        return sigmoid(temp);
    }
    
    // This method is used to train the node.
    void train(double[] hiddenOutput, byte classLabel)
    {
        for (int i = 0; i < weights.length; ++i)
            weights[i] += (classLabel - this.output(hiddenOutput))
                    * (1 - this.output(hiddenOutput))
                    * (1 + this.output(hiddenOutput)) * hiddenOutput[i] / 2;
    }
    
    // This method is the implementation of a sigmoid function.
    private double sigmoid(double x)
    {
        return 1 / (1 + Math.exp(x));
    }
}
