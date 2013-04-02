package ml.core;

import java.util.Vector;

/*
 * This class connects the output node and hidden nodes.
 */
public class Perceptron
{
    OutputNode outputNode;
    HiddenNode[] hiddenNodes;
    int numberOfInputs;
    
    double[] hiddenValues;
    
    // This contructor connects the output node and the hidde nodes.
    Perceptron(int numberOfInputs)
    {
        // Instantiate output and hidden nodes.
        outputNode = new OutputNode(numberOfInputs);
        
        hiddenNodes = new HiddenNode[numberOfInputs];
        for (int i = 0; i < numberOfInputs; ++i)
            hiddenNodes[i] = new HiddenNode(numberOfInputs);
        
        this.numberOfInputs = numberOfInputs;
        
        hiddenValues = new double[numberOfInputs];
    }
    
    // This method outputs the result from the Perceptron.
    double output(Vector<Byte> featureVector)
    {
        for (int i = 0; i < numberOfInputs; ++i)
            hiddenValues[i] = hiddenNodes[i].output(featureVector);
        
        return outputNode.output(hiddenValues);
    }
    
    // This method trains the Perceptron.
    void train(byte classLabel, Vector<Byte> featureVector)
    {
        for (int i = 0; i < numberOfInputs; ++i)
            hiddenValues[i] = hiddenNodes[i].output(featureVector);
        
        outputNode.train(hiddenValues, classLabel);
        
        for (int i = 0; i < numberOfInputs; ++i)
            hiddenNodes[i].train(output(featureVector), classLabel,
                    outputNode.weights[i], featureVector);
    }
}
