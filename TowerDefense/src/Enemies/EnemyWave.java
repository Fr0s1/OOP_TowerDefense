package Enemies;

import GamePlay.Player;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class EnemyWave {

    int[][] enemyWave = {
            {2, 1, 0, 0},
            {5, 2, 0, 0},
            {6, 2, 1, 0},
            {6, 3, 2, 1},
            {6, 3, 3, 2},
            {8, 4, 4, 3},
            {9, 5, 5, 4},
            {10, 6, 6, 5},
            {12, 6, 6, 6},
            {15, 6, 8, 7},
            {16, 6, 9, 8},
            {20, 8, 10, 9},
    };

    public static Queue<Enemy> enemyQueue;
    public static ArrayList<Enemy> activeEnemyList;

    public EnemyWave() {

        enemyQueue = new LinkedList<>();

        activeEnemyList = new ArrayList<>();

    }

    boolean hasSpawn = false;
    boolean addToQueue = false;

    public void update(int currentLevel) {

        if (!hasSpawn && currentLevel < 12) {

            spawn(currentLevel); // Thêm tất cả các quân địch trong một wave vào hàng chờ
            addToQueue = true;
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

        }

    }

    int tickCount = 0; // Biến đếm
    int enemySpawnDelay = 20; // Để không add liên tiếp quân địch vào danh sách

    public void addEnemyToActiveList() {

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

    public boolean hasAddToQueue() {
        return addToQueue;
    }
}