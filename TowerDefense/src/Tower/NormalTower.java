package Tower;

public class NormalTower extends Tower {
    //
    private static double fireRange = 48 * Math.sqrt(13);
    private static int damage = 20;
    private static float reloadTime = 0.6f;

    public NormalTower(int xLoc, int yLoc) {
        super(xLoc, yLoc, damage, fireRange, reloadTime);
    }
}
