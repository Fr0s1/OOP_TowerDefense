package Tower;

public class SniperTower extends Tower {
    private static double fireRange = 96 * Math.sqrt(10);
    private static double damage = 80;
    private static float reloadTime = 3.0f;
    private static TowerType type = TowerType.SNIPER_TOWER;

    public SniperTower(int xLoc, int yLoc) {
        super(xLoc, yLoc, damage, fireRange, reloadTime, type);
    }
}
