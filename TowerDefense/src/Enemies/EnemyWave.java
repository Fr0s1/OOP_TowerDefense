package Enemies;

import GamePlay.PlayScreen;

import java.util.ArrayList;

public class EnemyWave {

    int[][] enemyWave = {
            {2, 1, 0, 0, 0},
            {5, 2, 0, 0, 0},
            {6, 2, 1, 0, 0},
            {6, 3, 2, 2, 1},
            {6, 3, 3, 3, 1},
            {8, 4, 4, 4, 1},
            {9, 5, 5, 5, 2},
            {10, 6, 6, 6, 2},
            {12, 6, 6, 7, 2},
            {15, 6, 8, 8, 2},
            {16, 6, 9, 9, 3},
            {20, 8, 10, 10, 3}
    };

    int level = 0;

    public static ArrayList<Enemy> enemyList;

    static int i = 0;

    private float timeSinceLastSpawn, spawnTime;

    public EnemyWave(float spawnTime) {

        this.spawnTime = spawnTime;

        timeSinceLastSpawn = 0;

        enemyList = new ArrayList<>();

    }

    public void update() {

        timeSinceLastSpawn += PlayScreen.delta * 10;

        if (timeSinceLastSpawn > spawnTime) {

            spawn();

            timeSinceLastSpawn = 0;

        }

        for (int i = 0; i < enemyList.size(); i++) {

            if (enemyList.get(i).isAlive()) {

                enemyList.get(i).move();

            } else {

                enemyList.remove(i);

            }
        }
    }

    private void spawn() {
        if (enemyList.size() < 5) {
            if (i++ % 2 == 0) {
                enemyList.add(new NormalEnemy());
            } else {
                enemyList.add(new FastEnemy());
            }
//            enemyList.add(new NormalEnemy());
        }
//        enemyList.add(new NormalEnemy());
//
//        if (i++ % 2 == 0) {
//            enemyList.add(new NormalEnemy());
//        } else {
//            enemyList.add(new FastEnemy());
//        }
    }
}
