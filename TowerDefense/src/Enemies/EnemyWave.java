package Enemies;

import GamePlay.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

public class EnemyWave {

    private int[][] enemyWave = {
            {2, 1, 0, 0},
            {3, 2, 0, 0},
            {4, 2, 1, 0},
            {5, 3, 2, 1},
            {6, 4, 3, 2},
            {7, 4, 4, 3},
            {8, 5, 5, 4},
            {9, 6, 6, 5},
            {10, 6, 6, 6},
            {11, 6, 8, 7},
            {12, 6, 9, 8},
            {13, 8, 10, 9},
    };

    public static Queue<Enemy> enemyQueue;
    public static ArrayList<Enemy> activeEnemyList;

    public EnemyWave() {

        enemyQueue = new LinkedList<>();

        activeEnemyList = new ArrayList<>();

    }

    private boolean hasSpawn = false;

    public void update(int currentLevel) {

        if (!hasSpawn) {

            spawn(currentLevel); // Thêm tất cả các quân địch trong một wave vào hàng chờ
            hasSpawn = true;

        }

        addEnemyToActiveList(); // Thêm quân địch để hiện thị trên bản đồ

        for (int i = 0; i < activeEnemyList.size(); i++) {

            Enemy currentEnemy = activeEnemyList.get(i);

            if (currentEnemy.isAlive()) {

                currentEnemy.move();

                if (currentEnemy.hasReachedExit()) {

                    activeEnemyList.remove(i);

                    Player.decreaseLife();
                }

            } else {

                Player.addMoney(currentEnemy.getReward());

                activeEnemyList.remove(i);

            }

        }

    }

    private void spawn(int currentLevel) {

        for (int i = 0; i < 4; i++) {

            if (currentLevel <= 12) {

                for (int j = 1; j <= enemyWave[currentLevel - 1][i]; j++) {

                    switch (i) {
                        case 0:
                            enemyQueue.add(new NormalEnemy());
                            break;

                        case 1:
                            enemyQueue.add(new FastEnemy());
                            break;

                        case 2:
                            enemyQueue.add(new TankerEnemy());
                            break;

                        default:
                            enemyQueue.add(new BossEnemy());
                            break;
                    }

                }

            } else {

                for (int j = 1; j <= enemyWave[11][i] + currentLevel - 11; j++) {

                    switch (i) {
                        case 0:
                            enemyQueue.add(new NormalEnemy());
                            break;

                        case 1:
                            enemyQueue.add(new FastEnemy());
                            break;

                        case 2:
                            enemyQueue.add(new TankerEnemy());
                            break;

                        default:
                            enemyQueue.add(new BossEnemy());
                            break;
                    }

                }

            }

        }

        Collections.shuffle((LinkedList<Enemy>) enemyQueue);

    }

    private int tickCount = 0; // Biến đếm
    private int enemySpawnDelay = 15; // Để không add liên tiếp quân địch vào danh sách

    private void addEnemyToActiveList() {

        tickCount++;

        if (tickCount > enemySpawnDelay) {

            if (enemyQueue.size() > 0) {

                EnemyWave.activeEnemyList.add(EnemyWave.enemyQueue.poll());

            }

            tickCount = 0;

        }

    }

    public void setRespawn() {

        hasSpawn = false;

    }

}