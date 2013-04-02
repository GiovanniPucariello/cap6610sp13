package ml.core;

import java.util.Vector;

/*
 * This class implements 6 Perceptrons with a multiplexing conflict resolution
 * scheme to choose the final output.
 */
public class MLCore
{
    Perceptron sadness;
    
    // This constructor creates the ML core.
    public MLCore(int numberOfInputs)
    {
        sadness = new Perceptron(numberOfInputs);
    }
    
    // This method outputs the result of the Perceptron.
    public double output(Vector<Byte> featureVector)
    {
        return sadness.output(featureVector);
    }
    
    // This method trains the Perceptron.
    public void train(Vector<Byte> featureVector)
    {
        sadness.train(featureVector.elementAt(0), featureVector);
    }
}
