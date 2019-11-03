package Enemies;

import Map.PlayMap;

public class FastEnemy extends Enemy {

    private static double movementSpeed = 100;
    private static EnemyType type = EnemyType.FAST;
    private static int reward = 50;
    private static double health = 60;
    private static double armor = 1;

    public FastEnemy() {
        super(type, PlayMap.startTile, health, movementSpeed, armor, reward);
    }
}
