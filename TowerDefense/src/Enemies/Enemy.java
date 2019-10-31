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

    private double movementSpeed;
    private double armor;
    private int reward;

    private Tile spawnTile; // Vị trí sinh quân địch
    private EnemyType type; // Loại quân địch

    // Tọa độ theo pixel
    private float xPos;
    private float yPos;

    // Tọa độ theo mảng hai chiều
    private int xLoc;
    private int yLoc;

    private ArrayList<Checkpoint> checkpoints; // Mảng lưu các ô ở góc trên đường đi
    private int currentCheckpoint; // Vị trí trong mảng

    private int[] directions;
    // Mảng lưu hướng thay đổi của địch theo Pixel, có 2 phần tử
    // directions[0]: thay đổi theo hướng x
    // directions[1]: thay đổi theo hướng y

    private boolean first = true; // Kiểm tra địch xem có đang ở ô xuất phát hay không
    private boolean alive = true;

    public Enemy(EnemyType type, Tile spawnTile, double currentHealth, double movementSpeed, double armor, int reward) {
        this.type = type;

        this.maxHealth = currentHealth;
        this.currentHealth = currentHealth;

        this.movementSpeed = movementSpeed;
        this.armor = armor;
        this.reward = reward;

        this.spawnTile = spawnTile;
        this.xLoc = spawnTile.getX();
        this.yLoc = spawnTile.getY();
        this.xPos = xLoc * PlayMap.tileSize;
        this.yPos = yLoc * PlayMap.tileSize;

        this.checkpoints = new ArrayList<>();
        this.currentCheckpoint = 0;
        this.directions = new int[2];

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
        Checkpoint c = null;

        // Boolean to decide if checkpoint is found
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

            // Check if a next direction/checkpoint exists,
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

        // Check if position reached tile within variance of 3 (arbitrary):

        if (xPos > nextCheckpoint.getXPixel() - 3 && xPos < nextCheckpoint.getXPixel() + 3
                && yPos > nextCheckpoint.getYPixel() - 3 && yPos < nextCheckpoint.getYPixel() + 3) {

            reached = true;

            xPos = nextCheckpoint.getXPixel();
            updateXLoc(xPos);

            yPos = nextCheckpoint.getYPixel();
            updateYLoc(yPos);

        }

        return reached;
    }

    public void move() {
        if (first) {

            first = false;

        } else {
            if (checkpointReached()) {

                if (currentCheckpoint == checkpoints.size() - 1) {
                    die();
                } else {
                    currentCheckpoint++;
                }

            } else {

                xPos += checkpoints.get(currentCheckpoint).getxDirection() * movementSpeed * getDelta();
                updateXLoc(yPos);

                yPos += checkpoints.get(currentCheckpoint).getyDirection() * movementSpeed * getDelta();
                updateYLoc(yPos);

            }
        }
    }


    public void takeDamage(double damage) {

        currentHealth -= damage / armor;

        if (currentHealth <= 0.0) {

            die();

        }
    }

    public void updateXLoc(float xPos) {
        this.xLoc = (int) xPos / PlayMap.tileSize;
    }

    public void updateYLoc(float yPos) {
        this.yLoc = (int) yPos / PlayMap.tileSize;
    }

    private void die() {
        alive = false;
    }

    public boolean isAlive() {
        return alive;
    }

    public double getCurrentHealth() {
        return currentHealth;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public double getMovementSpeed() {
        return movementSpeed;
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

    public int getxLoc() {
        return xLoc;
    }

    public int getyLoc() {
        return yLoc;
    }

    public int[] getDirections() {
        return directions;
    }
}
