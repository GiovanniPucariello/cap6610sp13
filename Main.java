import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;

public class Main
{
    private static HashMap<String, Word> dictionary;
    private static HashMap<String, Emotion> clusterPoints;
    private static Vector<String> flowOfEmotion;
    
    public static void main(String[] args)
    {
        dictionary = new HashMap<String, Word>();
        clusterPoints = new HashMap<String, Emotion>();
        flowOfEmotion = new Vector<String>();
        
        for (int i = 0; i < 4; ++i)
            flowOfEmotion.add("neutral");
        
        readDictionaryFile("Dictionary.txt", dictionary);
        setClusterPoints(clusterPoints);
        
        trainAlgorithm("TrainingSet.txt", dictionary);
        double accuracy = testAlgorithm("TestingSet.txt", dictionary, flowOfEmotion);
        
        System.out.println("Accuracy: " + accuracy);
        writeDictionaryFile("Dictionary.txt", dictionary);
    }
    
    // ---------------------------------------------------------------------------- Primary Methods
    
    // This method initializes the dictionary by importing words from the Dictionary.txt file.
    private static void readDictionaryFile(String fileName, HashMap<String, Word> dictionary)
    {
        try
        {
            BufferedReader dictionaryFile = new BufferedReader(new FileReader(fileName));
            
            String line = dictionaryFile.readLine();
            Scanner tokens;
            
            while (line != null)
            {
                tokens = new Scanner(line);
                tokens.useDelimiter("\t");
                
                String word = tokens.next();
                String connotation = tokens.next();
                
                dictionary.put(word, new Word(word, connotation));
                
                line = dictionaryFile.readLine();
            }
            
            dictionaryFile.close();
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
    
    // This method places the 7 universal emotions on the unit circle.
    private static void setClusterPoints(HashMap<String, Emotion> hm)
    {
        hm.put("neutral", new Emotion("neutral", 0, 0, 0.2));
        hm.put("happiness", new Emotion("happiness", Math.cos(0 * Math.PI / 3), Math.sin(0 * Math.PI / 3), 0.2));
        hm.put("surprise", new Emotion("surprise", Math.cos(1 * Math.PI / 3), Math.sin(1 * Math.PI / 3), 0.2));
        hm.put("fear", new Emotion("fear", Math.cos(2 * Math.PI / 3), Math.sin(2 * Math.PI / 3), 0.2));
        hm.put("sadness", new Emotion("sadness", Math.cos(3 * Math.PI / 3), Math.sin(3 * Math.PI / 3), 0.2));
        hm.put("disgust", new Emotion("disgust", Math.cos(4 * Math.PI / 3), Math.sin(4 * Math.PI / 3), 0.2));
        hm.put("anger", new Emotion("anger", Math.cos(5 * Math.PI / 3), Math.sin(5 * Math.PI / 3), 0.2));
    }
    
    // This method trains the algorithm on the TrainingSet.txt file.
    private static void trainAlgorithm(String fileName, HashMap<String, Word> dictionary)
    {
        try
        {
            BufferedReader trainingSet = new BufferedReader(new FileReader(fileName));
            
            String line = trainingSet.readLine();
            Scanner tokens, words;
            
            while (line != null)
            {
                tokens = new Scanner(line);
                tokens.useDelimiter("\t");
                
                String sentence = tokens.next();
                String emotion = tokens.next();
                
                // Pull words towards emotion cluster points.
                words = new Scanner(sentence);
                while (words.hasNext())
                {
                    String word = words.next();
                    
                    // If the word is not in the dictionary, add it.
                    if (!dictionary.containsKey(word))
                        dictionary.put(word, new Word(word, "neutral"));
                    
                    pullTowardsClusterPoint(dictionary.get(word), emotion);
                }
                
                line = trainingSet.readLine();
            }
            
            trainingSet.close();
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
    
    // This method tests the algorithm on the TestingSet.txt file.
    private static double testAlgorithm(String fileName, HashMap<String, Word> dictionary, Vector<String> foe)
    {
        int hit = 0, miss = 0;
        
        try
        {
            BufferedReader trainingSet = new BufferedReader(new FileReader(fileName));
            
            String line = trainingSet.readLine();
            Scanner tokens, words;
            
            while (line != null)
            {
                double testX = 0, testY = 0;
                
                tokens = new Scanner(line);
                tokens.useDelimiter("\t");
                
                String sentence = tokens.next();
                String emotion = tokens.next();
                
                // Determine where the sentence lies in the coordinate space.
                words = new Scanner(sentence);
                while (words.hasNext())
                {
                    String word = words.next();
                    
                    // If the word is not in the dictionary, add it.
                    if (!dictionary.containsKey(word))
                        dictionary.put(word, new Word(word, "neutral"));
                    
                    Word w = dictionary.get(word);
                    
                    testX += w.x;
                    testY += w.y;
                }
                
                // Classify sentence's emotion.
                String testEmotion = classify(testX, testY);
                
                if (testEmotion.compareTo(emotion) == 0)
                    ++hit;
                else
                    ++miss;
                
                line = trainingSet.readLine();
            }
            
            trainingSet.close();
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        
        return (double) hit / (double) (hit + miss);
    }
    
    // This method writes out the dictionary to the Dictionary.txt file.
    private static void writeDictionaryFile(String fileName, HashMap<String, Word> dictionary)
    {
        try
        {
            BufferedWriter dictionaryFile = new BufferedWriter(new FileWriter(fileName));
            
            Iterator<Map.Entry<String, Word>> i = dictionary.entrySet().iterator();
            while (i.hasNext())
            {
                Word temp = i.next().getValue();
                dictionaryFile.write(temp.word + "\t" + temp.connotation + "\t" + temp.x + "\t" + temp.y);
                dictionaryFile.newLine();
            }
            
            dictionaryFile.flush();
            dictionaryFile.close();
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
    
    // ----------------------------------------------------------------------------- Helper Methods
    
    // This method pulls the word towards the specified cluster point.
    private static void pullTowardsClusterPoint(Word w, String emotion)
    {
        Emotion ePoint = clusterPoints.get(emotion);
        w.x += ePoint.weight * (ePoint.x - w.x);
        w.y += ePoint.weight * (ePoint.y - w.y);
    }
    
    // This method classifies a point in the coordinate space by determining the cluster point closest to the given point.
    private static String classify(double x, double y)
    {
        // 0 : neutral, 1 : happiness, 2 : surprise, 3 : fear, 4 : sadness, 5 : disgust, 6 : anger
        Vector<Double> distances = new Vector<Double>();
        
        distances.add(calculateDistance(clusterPoints.get("neutral").x, clusterPoints.get("neutral").y, x, y));
        distances.add(calculateDistance(clusterPoints.get("happiness").x, clusterPoints.get("happiness").y, x, y));
        distances.add(calculateDistance(clusterPoints.get("surprise").x, clusterPoints.get("surprise").y, x, y));
        distances.add(calculateDistance(clusterPoints.get("fear").x, clusterPoints.get("fear").y, x, y));
        distances.add(calculateDistance(clusterPoints.get("sadness").x, clusterPoints.get("sadness").y, x, y));
        distances.add(calculateDistance(clusterPoints.get("disgust").x, clusterPoints.get("disgust").y, x, y));
        distances.add(calculateDistance(clusterPoints.get("anger").x, clusterPoints.get("anger").y, x, y));
        
        // Find the closest cluster point.
        int index = 0;
        double smallestDistance = distances.elementAt(0);
        for (int i = 1; i < distances.size(); ++i)
        {
            if (distances.elementAt(i) < smallestDistance)
            {
                index = i;
                smallestDistance = distances.elementAt(i);
            }
        }
        
        switch (index)
        {
            case 1:
                return "happiness";
            case 2:
                return "surprise";
            case 3:
                return "fear";
            case 4:
                return "sadness";
            case 5:
                return "disgust";
            case 6:
                return "anger";
            default:
                return "neutral";
        }
    }
    
    // This method determines the distance between two points in 2D space.
    private static double calculateDistance(double x1, double y1, double x2, double y2)
    {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
}