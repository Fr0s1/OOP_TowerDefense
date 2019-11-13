package Enemies;

import Map.PlayMap;

class BossEnemy extends Enemy {

    private static Enemy.EnemyType type = EnemyType.BOSS;

    private static double movementSpeed = 30;

    private static int reward = 200;
    private static double health = 500;
    private static double armor = 0.7;


    BossEnemy() {
        super(type, PlayMap.startTile, health, movementSpeed, armor, reward);
    }

}