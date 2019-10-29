package Enemies;

import Map.PlayMap;

public class FastEnemy extends Enemy {
    private static int movementSpeed = 75;
    private static EnemyType type = EnemyType.FAST;
    private static int reward = 50;
    private static int health = 60;

    public FastEnemy() {
        super(type, PlayMap.startTile, health, movementSpeed, reward);
    }
}
