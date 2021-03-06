package Enemies;

import Map.*;

import static GamePlay.PlayScreen.*;

import Tile.Checkpoint;
import Tile.Tile;

import java.util.ArrayList;

public abstract class Enemy {

    // Các loại quân địch
    public enum EnemyType {
        FAST, NORMAL, TANKER, BOSS
    }

    private double maxHealth;
    private double currentHealth;

    private double distanceTraveled;

    private double movementSpeed;
    private double armor;
    private int reward;

    private Tile spawnTile; // Vị trí sinh quân địch
    private EnemyType type; // Loại quân địch

    // Tọa độ theo pixel
    private float xPos;
    private float yPos;

    private ArrayList<Checkpoint> checkpoints; // Mảng lưu các ô ở góc trên đường đi
    private int currentCheckpoint; // Vị trí trong mảng

    private int[] directions;
    // Mảng lưu hướng thay đổi của địch theo Pixel, có 2 phần tử
    // directions[0]: thay đổi theo hướng x
    // directions[1]: thay đổi theo hướng y

    private boolean first = true; // Kiểm tra địch xem có đang ở ô xuất phát hay không
    private boolean alive = true;
    private boolean visible = true;

    private boolean reachedExit = false;

    private double slowDurationCount;
    private boolean affectedBySlowTower;

    private double slowedDuration = 3;

    Enemy(EnemyType type, Tile spawnTile, double currentHealth, double movementSpeed, double armor, int reward) {
        this.type = type;

        this.maxHealth = currentHealth;
        this.currentHealth = currentHealth;

        this.distanceTraveled = 0;

        this.movementSpeed = movementSpeed;
        this.armor = armor;
        this.reward = reward;

        this.spawnTile = spawnTile;
        this.xPos = spawnTile.getX() * PlayMap.tileSize;
        this.yPos = spawnTile.getY() * PlayMap.tileSize;

        this.checkpoints = new ArrayList<>();
        this.currentCheckpoint = 0;
        this.directions = new int[2];

        slowDurationCount = 0;
        affectedBySlowTower = false;

        directions = findNextDirection(spawnTile); // Tìm hướng đi tiếp theo ở ô quân địch bắt đầu

        findAllCheckpoints(); // Thêm vào mảng tất cả các ô ở góc
    }

    // Hàm tìm hướng đi tiếp theo ở ô đường đi bất kì
    private int[] findNextDirection(Tile currentTile) {

        int[] dir = new int[2];

        Tile u = PlayMap.getTile(currentTile.getX(), currentTile.getY() - 1);
        Tile r = PlayMap.getTile(currentTile.getX() + 1, currentTile.getY());
        Tile d = PlayMap.getTile(currentTile.getX(), currentTile.getY() + 1);
        Tile l = PlayMap.getTile(currentTile.getX() - 1, currentTile.getY());

        if (currentTile.getType() == u.getType() && directions[1] != 1) {

            dir[0] = 0;
            dir[1] = -1;

        } else if (currentTile.getType() == r.getType() && directions[0] != -1) {

            dir[0] = 1;
            dir[1] = 0;

        } else if (currentTile.getType() == d.getType() && directions[1] != -1) {

            dir[0] = 0;
            dir[1] = 1;

        } else if (currentTile.getType() == l.getType() && directions[0] != 1) {

            dir[0] = -1;
            dir[1] = 0;

        } else {

            dir[0] = dir[1] = 2;

        }

        return dir;
    }

    // Tìm ô ở góc từ 1 ô đường đi
    private Checkpoint findNextCheckpoint(Tile currentTile, int[] dir) {

        Tile next = null;

        Checkpoint c; // Ô ở góc tiếp theo theo hướng đang đi

        boolean found = false;

        int counter = 1;

        while (!found) {

            if (currentTile.getX() + dir[0] * counter == PlayMap.getWidthOfMap() || currentTile.getY() + dir[1] * counter == PlayMap.getHeightOfMap() || currentTile.getType() !=
                    PlayMap.getTile(currentTile.getX() + dir[0] * counter, currentTile.getY() + dir[1] * counter).getType()) {

                found = true;

                counter -= 1;

                next = PlayMap.getTile(currentTile.getX() + dir[0] * counter, currentTile.getY() + dir[1] * counter);

            }

            counter++;

        }

        c = new Checkpoint(next, dir[0], dir[1]);

        return c;
    }

    // Tìm hết tất cả các ô ở góc
    private void findAllCheckpoints() {

        checkpoints.add(findNextCheckpoint(spawnTile, directions = findNextDirection(spawnTile)));

        int counter = 0;
        boolean cont = true;

        while (cont) {
            int[] currentDirections = findNextDirection(checkpoints.get(counter).getTile());

            // Tìm xem ô ở góc tiếp theo có tồn tại hay không
            if (currentDirections[0] == 2 || counter == 20) {

                cont = false;

            } else {

                checkpoints.add(findNextCheckpoint(checkpoints.get(counter).getTile(), directions = findNextDirection(checkpoints.get(counter).getTile())));

            }

            counter++;

        }

    }

    private boolean checkpointReached() {

        boolean reached = false;

        Tile nextCheckpoint = checkpoints.get(currentCheckpoint).getTile();

        if (xPos > nextCheckpoint.getXPixel() - 3 && xPos < nextCheckpoint.getXPixel() + 3
                && yPos > nextCheckpoint.getYPixel() - 3 && yPos < nextCheckpoint.getYPixel() + 3) {

            reached = true;

            xPos = nextCheckpoint.getXPixel();

            yPos = nextCheckpoint.getYPixel();

        }

        return reached;
    }

    public void move() {

        if (first) {

            first = false;

        } else {

            // Nếu đang bị ảnh hưởng bởi tháp làm chậm, cập nhật thời gian bị làm chậm
            if (affectedBySlowTower) {

                slowDurationCount += delta;

            }

            if (slowDurationCount > slowedDuration) {

                unfreezeEnemy();

            }

            if (checkpointReached()) {

                if (currentCheckpoint == checkpoints.size() - 1) {

                    visible = false;

                    reachedExit = true;

                } else {

                    currentCheckpoint++;

                }

            } else {

                double deltaX = checkpoints.get(currentCheckpoint).getxDirection() * getMovementSpeed() * getDelta();
                double deltaY = checkpoints.get(currentCheckpoint).getyDirection() * getMovementSpeed() * getDelta();

                xPos += deltaX;
                yPos += deltaY;

                distanceTraveled += (Math.abs(deltaX) + Math.abs(deltaY));

            }

        }

    }


    public void takeDamage(double damage) {

        currentHealth -= damage * (1 - armor);

        if (currentHealth <= 0) {

            alive = false;

            visible = false;

        }

    }

    public void slowEnemy(double duration) {

        this.slowedDuration = duration;

        affectedBySlowTower = true;

        this.resetSlowDuration();

    }

    public void unfreezeEnemy() {


        this.resetSlowDuration();

        affectedBySlowTower = false;

    }

    public void resetSlowDuration() {
        this.slowDurationCount = 0;
    }

    public boolean isAlive() {
        return alive;
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean hasReachedExit() {
        return reachedExit;
    }

    public double getMovementSpeed() {

        if (affectedBySlowTower) {

            double slowMultiplier = 0.5;

            return movementSpeed * slowMultiplier;

        } else {

            return movementSpeed;

        }

    }

    public double getOriginalMovementSpeed() {
        return movementSpeed;
    }

    public boolean isAffectedBySlowTower() {
        return this.affectedBySlowTower;
    }

    public double getCurrentHealth() {
        return currentHealth;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public int getReward() {
        return reward;
    }

    public EnemyType getType() {
        return type;
    }

    public float getxPos() {
        return xPos;
    }

    public float getyPos() {
        return yPos;
    }

    public int getXDirection() {
        return checkpoints.get(currentCheckpoint).getxDirection();
    }

    public int getYDirection() {
        return checkpoints.get(currentCheckpoint).getyDirection();
    }

    public double getDistanceTraveled() {
        return distanceTraveled;
    }
}