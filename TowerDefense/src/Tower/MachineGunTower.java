package Tower;

public class MachineGunTower extends Tower {
    private static double fireRange = 96 * Math.sqrt(2);
    private static double damage = 30;
    private static float reloadTime = 0.3f;
    private static TowerType type = TowerType.MACHINE_GUN_TOWER;

    public MachineGunTower(int xLoc, int yLoc) {
        super(xLoc, yLoc, damage, fireRange, reloadTime, type);
    }
}
