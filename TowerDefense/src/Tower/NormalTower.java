package Tower;

public class NormalTower extends Tower {
    //
    private static double fireRange = 120 * Math.sqrt(2);
    private static int damage = 20;
    private static float reloadTime = 5;

    public NormalTower(int xLoc, int yLoc) {
        super(xLoc, yLoc, damage, fireRange, reloadTime);
    }
}
