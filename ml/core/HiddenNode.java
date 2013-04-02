package ml.core;

import java.util.Vector;

/*
 * This class implements the hidden nodes in the Perceptron.
 */
public class HiddenNode
{
    double[] weights;
    
    // This constructor initializes the weights to a random value between 0 and
    // 1.
    HiddenNode(int numberOfInputs)
    {
        weights = new double[numberOfInputs];
        for (int i = 0; i < numberOfInputs; ++i)
            weights[i] = Math.random();
    }
    
    // This method generates the output of the hidden node.
    // The first Byte in the feature vector is discarded b/c it is the class
    // label.
    double output(Vector<Byte> featureVector)
    {
        double temp = 0;
        
        for (int i = 1; i < featureVector.size(); ++i)
            temp += weights[i] * featureVector.elementAt(i);
        
        return sigmoid(temp);
    }
    
    // This method is used to train the node.
    void train(double perceptronOutput, byte classLabel, double outNodeWeight,
            Vector<Byte> featureVector)
    {
        for (int i = 0; i < weights.length; ++i)
            weights[i] += (classLabel - perceptronOutput)
                    * (1 - perceptronOutput) * (1 + perceptronOutput)
                    * outNodeWeight * (1 - this.output(featureVector))
                    * (1 + this.output(featureVector)) / 4;
    }
    
    // This method is the implementation of a sigmoid function.
    private double sigmoid(double x)
    {
        return 1 / (1 + Math.exp(x));
    }
}
