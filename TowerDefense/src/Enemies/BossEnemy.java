package Enemies;

import Map.PlayMap;

public class BossEnemy extends Enemy {

    private static double movementSpeed = 30;
    private static Enemy.EnemyType type = EnemyType.BOSS;
    private static int reward = 200;
    private static double health = 500;
    private static double armor = 7.0f;

    public BossEnemy() {
        super(type, PlayMap.startTile, health, movementSpeed, armor, reward);
    }

}