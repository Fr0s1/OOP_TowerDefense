package Enemies;

import GamePlay.PlayScreen;

import java.util.ArrayList;

public class EnemyWave {
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

        for (Enemy e : enemyList) {
            if (e.isAlive()) {
                e.move();
            } else {

            }
        }
    }

    private void spawn() {
        if (enemyList.size() < 7) {
            if (i++ % 2 == 0) {
                enemyList.add(new NormalEnemy());
            } else {
                enemyList.add(new FastEnemy());
            }
        }
//        enemyList.add(new NormalEnemy());
    }
}
