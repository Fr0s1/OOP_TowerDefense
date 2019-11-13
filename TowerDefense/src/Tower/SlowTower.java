package Tower;

public class SlowTower extends Tower {

    public static double fireRange = 48 * Math.sqrt(13);
    public static int cost = 200;

    private static double damage = 0;
    private static float reloadTime = 0.9f;
    private static TowerType type = TowerType.SLOW_TOWER;

    public SlowTower(int xLoc, int yLoc) {
        super(xLoc, yLoc, damage, fireRange, reloadTime, type, cost);
    }

}
