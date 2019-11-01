package HelpfulFunctions;

public class Function {
    public static double calculateDistance(double xPos1, double yPos1, double xPos2, double yPos2) {
        double deltaXPos = xPos1 - xPos2;
        double deltaYPos = yPos1 - yPos2;
        double distance = Math.sqrt(Math.pow(deltaXPos, 2) + Math.pow(deltaYPos, 2));

        return distance;
    }
}
