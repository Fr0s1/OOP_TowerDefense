package Enemies;

import Map.PlayMap;

public class TankerEnemy extends Enemy {
    private static double movementSpeed = 40;
    private static Enemy.EnemyType type = EnemyType.TANKER;
    private static int reward = 50;
    private static double health = 200;
    private static double armor = 3.0f;

    public TankerEnemy() {
        super(type, PlayMap.startTile, health, movementSpeed, armor, reward);
    }
}
