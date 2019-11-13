package Tower;

public class NormalTower extends Tower {

    public static int cost = 100;
    public static double fireRange = 48 * Math.sqrt(13);

    private static double damage = 25;
    private static float reloadTime = 0.6f;
    private static TowerType type = TowerType.NORMAL_TOWER;

    public NormalTower(int xLoc, int yLoc) {
        super(xLoc, yLoc, damage, fireRange, reloadTime, type, cost);
    }
}