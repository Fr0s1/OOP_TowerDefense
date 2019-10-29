package Enemies;

import Map.PlayMap;

public class NormalEnemy extends Enemy {
    private static int movementSpeed = 50;
    private static EnemyType type = EnemyType.NORMAL;
    private static int reward = 100;
    private static int health = 140;

    public NormalEnemy() {
        super(type, PlayMap.startTile, health, movementSpeed, reward);
    }
}
