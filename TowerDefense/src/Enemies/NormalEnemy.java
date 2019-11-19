package Enemies;

import Map.PlayMap;

class NormalEnemy extends Enemy {
    private static int movementSpeed = 50;

    private static EnemyType type = EnemyType.NORMAL;
    private static int reward = 50;
    private static double health = 150;
    private static double armor = 0.2;

    NormalEnemy() {
        super(type, PlayMap.startTile, health, movementSpeed, armor, reward);
    }
}