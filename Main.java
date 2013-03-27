import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class Main
{
    static HashMap<String, Word> dictionary;
    
    public static void main(String[] args)
    {
        buildDictionary("wiktionaryAbridged.txt");
        
        // TODO Create wiktionaryAbridged.txt file.
        // TODO Test buildDictionary method.
        
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
}
