public class Emotion
{
    String type;
    double x, y;
    double weight;
    
    Emotion(String type, double x, double y)
    {
        this.type = type;
        this.x = x;
        this.y = y;
        this.weight = 0.01;
    }
    
    void setWeight(double weight)
    {
        this.weight = weight;
    }
}