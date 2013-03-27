/*
 * The Word class represents a word in the dictionary with the part of speech
 * and connotation.
 */

public class Word
{
    /*
     * partOfSpeech[7:0]            connotation
     * -----------------            -----------
     * bit 7 : verb                 0 = unknown
     * bit 6 : noun                 1 = positive
     * bit 5 : adjective            2 = negative
     * bit 4 : adverb               3 = neutral
     * bit 3 : pronoun
     * bit 2 : preposition
     * bit 1 : conjunction
     * bit 0 : interjection
     */
    
    String word;
    byte partOfSpeech;
    byte connotation;
    
    /* 
     * This constructor assigns the member variables to their respective arguments.
     */
    Word(String word, byte partOfSpeech, byte connotation)
    {
        this.word = word;
        this.partOfSpeech = partOfSpeech;
        this.connotation = connotation;
    }
    
    /*
     * This method adds a part of speech to the word by bitwise ORing the member
     * variable and the argument. In this way, a word can have more than 1 part
     * of speech.
     */
    void addPartOfSpeech(String partOfSpeech)
    {
        this.partOfSpeech |= stringToPartOfSpeech(partOfSpeech);
    }
    
    /*
     * This method adds the connotation by overwriting the old connotation if it
     * was unknown.
     */
    void addConnotation(String connotation)
    {
        if (this.connotation == 0)
            this.connotation = stringToConnotation(connotation);
    }
    
    /*
     * This method returns the byte equivalent of part of speech string.
     */
    static byte stringToPartOfSpeech(String s)
    {
        switch (s)
        {
            case "Verb":
                return (byte) 0x80;
            case "Noun":
                return (byte) 0x40;
            case "Adjective":
                return (byte) 0x20;
            case "Adverb":
                return (byte) 0x10;
            case "Pronoun":
                return (byte) 0x08;
            case "Preposition":
                return (byte) 0x04;
            case "Conjunction":
                return (byte) 0x02;
            case "Interjection":
                return (byte) 0x01;
            default:
                return (byte) 0x00;
        }
    }
    
    /*
     * This method returns the byte equivalent of the connotation string.
     */
    static byte stringToConnotation(String s)
    {
        switch (s)
        {
            case "Positive":
                return (byte) 1;
            case "Negative":
                return (byte) 2;
            case "Neutral":
                return (byte) 3;
            default:
                return (byte) 0;
        }
    }
}
