package Enemies;

import Map.PlayMap;

class TankerEnemy extends Enemy {

    private static double movementSpeed = 40;

    private static Enemy.EnemyType type = EnemyType.TANKER;

    private static int reward = 100;
    private static double health = 400;
    private static double armor = 0.7;

    TankerEnemy() {
        super(type, PlayMap.startTile, health, movementSpeed, armor, reward);
    }
}