public class Emotion
{
    String type;
    double x, y;
    double weight;
    
    Emotion(String type, double x, double y, double weight)
    {
        this.type = type;
        this.x = x;
        this.y = y;
        this.weight = weight;
    }
    
    void setWeight(double weight)
    {
        this.weight = weight;
    }
}