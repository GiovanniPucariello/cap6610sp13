package ml.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;

import ml.core.MLCore;

/*
 * This class is the core of the machine learning algorithm.
 */
public class Main
{
    // The feature vector is the concatenation of 7 fields:
    // 1. Label
    // 2. Last 4 Labels, 7 bytes
    // 3. Sentence Type: . ! ?, 3 bytes
    // 4. Connotation, 4 bytes
    // 5. Part of Speech, 8 bytes
    // 6. Gesture, 5 bytes
    // 7. Bag of Words, n bytes
    static HashMap<String, Word> dictionary;
    static Vector<Byte> featureVector;
    static MLCore core;
    
    public static void main(String[] args)
    {
        buildDictionary("wiktionary.txt");
        
        training("TrainingSet.txt");
        
        // Instantiate MLCore.
        core = new MLCore(featureVector.size());
        core.train(featureVector);
        System.out.println(core.output(featureVector));
        
        System.out.println("Done!");
        
        // TODO Testing.
        
    }
    
    /*
     * This method parses the words from the dictionary file and places the
     * words in a hash map.
     */
    private static void buildDictionary(String fileName)
    {
        try
        {
            // Create dictionary.
            dictionary = new HashMap<String, Word>();
            
            // Declare variables to read from the dictionary file.
            BufferedReader dictionaryFile = new BufferedReader(new FileReader(
                    fileName));
            
            String line = dictionaryFile.readLine();
            Scanner entry;
            String theWord;
            
            // Entry format, where each field is delimited by '\t':
            // [language] [word] [part of speech] [connotation] [definition]
            while (line != null)
            {
                // Put line into Scanner to extract fields.
                entry = new Scanner(line);
                entry.useDelimiter("\t");
                
                // Discard [language] field.
                entry.next();
                
                // Parse entry into Word object.
                theWord = entry.next();
                if (dictionary.containsKey(theWord))
                {
                    // Word is already in dictionary so add part of speech and
                    // connotation.
                    Word temp = dictionary.get(theWord);
                    temp.addPartOfSpeech(entry.next());
                    temp.addConnotation(entry.next());
                }
                else
                {
                    // Create new word entry and put in dictionary.
                    Word newDictionaryEntry = new Word(theWord,
                            Word.stringToPartOfSpeech(entry.next()),
                            Word.stringToConnotation(entry.next()));
                    
                    dictionary.put(theWord, newDictionaryEntry);
                }
                
                line = dictionaryFile.readLine();
            }
            
            // Close file.
            dictionaryFile.close();
        }
        catch (IOException e)
        {
            System.out.println("Method \"buildDictionary\" failed.");
            e.printStackTrace();
        }
    }
    
    /*
     * This method trains the machine learning algorithm of the specified
     * training set.
     */
    private static void training(String fileName)
    {
        try
        {
            // Declare a queue and initialize the last 4 labels to neutral.
            LinkedList<Global.Emotion> last4LabelsQueue = new LinkedList<Global.Emotion>();
            for (int i = 0; i < 4; ++i)
                last4LabelsQueue.add(Global.Emotion.neutral);
            
            BufferedReader trainingSet = new BufferedReader(new FileReader(
                    fileName));
            
            String line = trainingSet.readLine();
            
            // Read in the training sentences.
            // The sentences are in the form: [label] [gesture] [sentence],
            // where the gesture may or may not be present.
            while (line != null)
            {
                featureVector = buildFeatureVector(line, last4LabelsQueue);
                
                // TODO Put feature vector in machine learning algorithm.
                
                line = trainingSet.readLine();
            }
            
            // Close file.
            trainingSet.close();
        }
        catch (Exception e)
        {
            System.out.println("Method \"training\" failed.");
            e.printStackTrace();
        }
    }
    
    // -------------------------------------------------- Private Helper Methods
    
    private static Vector<Byte> buildFeatureVector(String line,
            LinkedList<Global.Emotion> last4LabelsQueue)
    {
        // Put line into Scanner to extract fields, removing the
        // punctuation.
        Scanner sentence = new Scanner(line.substring(0, line.length() - 1));
        
        // Build the Last 4 Labels field.
        Vector<Byte> last4Labels = new Vector<Byte>(7);
        for (int i = 0; i < 7; ++i)
            last4Labels.add((byte) 0);
        
        ListIterator<Global.Emotion> itr = last4LabelsQueue.listIterator();
        for (int i = 1; i <= 4; ++i)
        {
            switch (itr.next())
            {
                case happiness:
                    last4Labels.set(0, (byte) (last4Labels.elementAt(0) + i));
                    break;
                
                case sadness:
                    last4Labels.set(1, (byte) (last4Labels.elementAt(1) + i));
                    break;
                
                case surprise:
                    last4Labels.set(2, (byte) (last4Labels.elementAt(2) + i));
                    break;
                
                case fear:
                    last4Labels.set(3, (byte) (last4Labels.elementAt(3) + i));
                    break;
                
                case disgust:
                    last4Labels.set(4, (byte) (last4Labels.elementAt(4) + i));
                    break;
                
                case anger:
                    last4Labels.set(5, (byte) (last4Labels.elementAt(5) + i));
                    break;
                
                default:
                    last4Labels.set(6, (byte) (last4Labels.elementAt(6) + i));
                    break;
            }
        }
        
        // Update last 4 labels queue and build Label field.
        last4LabelsQueue.remove();
        Byte Label;
        
        switch (sentence.next())
        {
            case "happiness":
                last4LabelsQueue.addLast(Global.Emotion.happiness);
                Label = 0;
                break;
            
            case "sadness":
                last4LabelsQueue.addLast(Global.Emotion.sadness);
                Label = 1;
                break;
            
            case "surprise":
                last4LabelsQueue.addLast(Global.Emotion.surprise);
                Label = 2;
                break;
            
            case "fear":
                last4LabelsQueue.addLast(Global.Emotion.fear);
                Label = 3;
                break;
            
            case "disgust":
                last4LabelsQueue.addLast(Global.Emotion.disgust);
                Label = 4;
                break;
            
            case "anger":
                last4LabelsQueue.addLast(Global.Emotion.anger);
                Label = 5;
                break;
            
            default:
                last4LabelsQueue.addLast(Global.Emotion.neutral);
                Label = 6;
                break;
        }
        
        // Build the Sentence Type field.
        Vector<Byte> sentenceType = new Vector<Byte>(3);
        for (int i = 0; i < 3; ++i)
            sentenceType.add((byte) 0);
        
        if (line.endsWith("."))
            sentenceType.set(0, (byte) 1);
        else if (line.endsWith("!"))
            sentenceType.set(1, (byte) 1);
        else
            sentenceType.set(2, (byte) 1);
        
        // Declare Gesture field.
        Vector<Byte> gesture = new Vector<Byte>(5);
        for (int i = 0; i < 5; ++i)
            gesture.add((byte) 0);
        
        // Declare Connotation field.
        Vector<Byte> connotation = new Vector<Byte>(4);
        for (int i = 0; i < 4; ++i)
            connotation.add((byte) 0);
        
        // Declare Part of Speech field.
        Vector<Byte> partOfSpeech = new Vector<Byte>(8);
        for (int i = 0; i < 8; ++i)
            partOfSpeech.add((byte) 0);
        
        // Read the sentence.
        String word;
        while (sentence.hasNext())
        {
            word = sentence.next();
            
            // Build the Gesture field.
            switch (word)
            {
                case "*smile*":
                    gesture.set(0, (byte) 1);
                    continue;
                    
                case "*frown*":
                    gesture.set(1, (byte) 1);
                    continue;
                    
                case "*gasp*":
                    gesture.set(2, (byte) 1);
                    continue;
                    
                case "*glare*":
                    gesture.set(3, (byte) 1);
                    continue;
                    
                case "*shrug*":
                    gesture.set(4, (byte) 1);
                    continue;
            }
            
            if (dictionary.containsKey(word))
            {
                // Build the Connotation field.
                switch (dictionary.get(word).connotation)
                {
                    case 0:
                        connotation.set(0, (byte) 1);
                        break;
                    
                    case 1:
                        connotation.set(1, (byte) 1);
                        break;
                    
                    case 2:
                        connotation.set(2, (byte) 1);
                        break;
                    
                    default:
                        connotation.set(3, (byte) 1);
                        break;
                }
                
                // Build the Part of Speech field.
                if ((dictionary.get(word).partOfSpeech & 0x80) != 0)
                    partOfSpeech.set(0, (byte) (partOfSpeech.elementAt(0) + 1));
                if ((dictionary.get(word).partOfSpeech & 0x40) != 0)
                    partOfSpeech.set(1, (byte) (partOfSpeech.elementAt(1) + 1));
                if ((dictionary.get(word).partOfSpeech & 0x20) != 0)
                    partOfSpeech.set(2, (byte) (partOfSpeech.elementAt(2) + 1));
                if ((dictionary.get(word).partOfSpeech & 0x10) != 0)
                    partOfSpeech.set(3, (byte) (partOfSpeech.elementAt(3) + 1));
                if ((dictionary.get(word).partOfSpeech & 0x08) != 0)
                    partOfSpeech.set(4, (byte) (partOfSpeech.elementAt(4) + 1));
                if ((dictionary.get(word).partOfSpeech & 0x04) != 0)
                    partOfSpeech.set(5, (byte) (partOfSpeech.elementAt(5) + 1));
                if ((dictionary.get(word).partOfSpeech & 0x02) != 0)
                    partOfSpeech.set(6, (byte) (partOfSpeech.elementAt(6) + 1));
                if ((dictionary.get(word).partOfSpeech & 0x01) != 0)
                    partOfSpeech.set(7, (byte) (partOfSpeech.elementAt(7) + 1));
                
                // Increment word count.
                ++dictionary.get(word).count;
            }
        }
        
        sentence.close();
        
        // Build the Bag of Words field.
        Vector<Byte> bagOfWords = new Vector<Byte>();
        Iterator<Map.Entry<String, Word>> itr2 = dictionary.entrySet()
                .iterator();
        while (itr2.hasNext())
        {
            Word temp = itr2.next().getValue();
            bagOfWords.add((byte) temp.count);
            temp.count = 0;
        }
        
        // Return the feature vector.
        Vector<Byte> temp = new Vector<Byte>();
        temp.add(Label);
        temp.addAll(last4Labels);
        temp.addAll(sentenceType);
        temp.addAll(connotation);
        temp.addAll(partOfSpeech);
        temp.addAll(gesture);
        temp.addAll(bagOfWords);
        
        return temp;
    }
}
