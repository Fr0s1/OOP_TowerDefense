package Tower;

public class SniperTower extends Tower {

    public static double fireRange = 96 * Math.sqrt(10);
    private static double damage = 80;

    private static float reloadTime = 3.0f;
    private static TowerType type = TowerType.SNIPER_TOWER;
    public static int cost = 700;

    public SniperTower(int xLoc, int yLoc) {
        super(xLoc, yLoc, damage, fireRange, reloadTime, type, cost);
    }
}