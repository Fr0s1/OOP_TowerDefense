package Enemies;

import Map.PlayMap;

class FastEnemy extends Enemy {

    private static double movementSpeed = 100;

    private static EnemyType type = EnemyType.FAST;
    private static int reward = 25;
    private static double health = 50;
    private static double armor = 0;

    FastEnemy() {
        super(type, PlayMap.startTile, health, movementSpeed, armor, reward);
    }
}