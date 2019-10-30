package Enemies;

import Map.PlayMap;

public class NormalEnemy extends Enemy {
    private static int movementSpeed = 50;
    private static EnemyType type = EnemyType.NORMAL;
    private static int reward = 100;
    private static double health = 120;
    private static double armor = 1.5f;

    public NormalEnemy() {
        super(type, PlayMap.startTile, health, movementSpeed, armor, reward);
    }
}
