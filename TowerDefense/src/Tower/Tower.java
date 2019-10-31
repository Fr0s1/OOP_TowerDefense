package Tower;

import Enemies.Enemy;
import Enemies.EnemyWave;
import GamePlay.PlayScreen;
import HelpfulFunctions.Function;
import Map.PlayMap;

import java.util.*;

import static HelpfulFunctions.Function.*;
import static Enemies.EnemyWave.*;

class CompareEnemy implements Comparator<Enemy> {
    @Override
    public int compare(Enemy o1, Enemy o2) {
        if (o1.getMovementSpeed() < o2.getMovementSpeed()) {
            return 1;
        } else if (o1.getMovementSpeed() > o2.getMovementSpeed()) {
            return -1;
        } else {
            return 0;
        }
    }
}

public abstract class Tower {
    public enum TowerType {
        NORMAL_TOWER, MACHINE_GUN_TOWER, SNIPER_TOWER
    }

    TowerType towerType;
    // Vị trí tháp theo mảng hai chiều
    int xLoc, yLoc;

    // Vị trí tháp theo Pixel
    int xPos, yPos;

    double towerDamage;    // Sát thương của tháp
    double fireRange;   // Tầm bắn của tháp
    double timeSinceLastShot;
    double reloadTime;  // Thời gian nạp đạn

    float angleOfRotation;

    public static ArrayList<Tower> towersList; // Mảng lưu tất cả những tháp đang có trên sân

    CompareEnemy compareEnemy;
    private PriorityQueue<Enemy> enemiesInRange; // Hàng chờ lưu những quân địch có trong tầm bắn của tháp

    Enemy targetEnemy; // Mục tiêu hiện tại của tháp

    public Tower(int xLoc, int yLoc, double damage, double fireRange, double reloadTime, TowerType type) {
        this.xLoc = xLoc;
        this.yLoc = yLoc;
        this.xPos = xLoc * PlayMap.tileSize;
        this.yPos = yLoc * PlayMap.tileSize;
        this.towerDamage = damage;
        this.fireRange = fireRange;
        angleOfRotation = 0;

        towersList = new ArrayList<>();

        compareEnemy = new CompareEnemy();
        enemiesInRange = new PriorityQueue(compareEnemy);

        targetEnemy = null;

        timeSinceLastShot = 0;
        this.reloadTime = reloadTime;
        this.towerType = type;
    }

    public double calculateDistanceToEnemy(Enemy enemy) {
        return calculateDistance(this.xPos + PlayMap.tileSize / 2, this.yPos + PlayMap.tileSize / 2, enemy.getxPos() + PlayMap.tileSize / 2, enemy.getyPos() + PlayMap.tileSize / 2);
    }

    /* Tìm tất cả các quân địch trong tầm bắn
       Dùng hàng chờ ưu tiên (Priority Queue) vì:
            + Ưu tiên quân địch có di chuyển nhanh nhất (máu cũng ít nhất) để bắn trước.
            + Quân địch nào vào tầm bắn trước sẽ được trọn trước nếu cùng tốc độ di chuyển
            + Nếu mục tiêu tháp đang chọn ra khỏi tầm bắn sẽ cập nhật luôn được mục tiêu tiếp theo
    * */

    public void addEnemiesInRange(ArrayList<Enemy> wave) {
        enemiesInRange.clear();
        for (Enemy e : wave) {
            double distance = calculateDistanceToEnemy(e);
            if (distance <= fireRange) {
                enemiesInRange.add(e);
            }
        }
    }

    // Chọn mục tiêu là phần từ đầu tiên của hàng chờ ưu tiên
    public void setTarget() {
        if (enemiesInRange.size() >= 1) {
            targetEnemy = enemiesInRange.peek();
        } else {
            targetEnemy = null;
        }
    }

    // Chọn mục tiêu tiếp theo trong hàng chờ nếu quân địch ra khỏi tầm bắn
    public void updateTarget() {
        if (targetEnemy != null) {
            double distance = calculateDistanceToEnemy(targetEnemy);

            if (distance > fireRange) {
                enemiesInRange.poll();
                setTarget();
            }
        }
    }

    public Projectile shoot(Enemy targetEnemy) {
        timeSinceLastShot += PlayScreen.delta;
        if (timeSinceLastShot >= reloadTime && targetEnemy != null) {
            timeSinceLastShot = 0;

            switch (towerType) {
                case NORMAL_TOWER:
                    return new Projectile(this.xPos, this.yPos, targetEnemy.getxPos(), targetEnemy.getyPos(), towerDamage, targetEnemy, Projectile.ProjectileType.NORMAL_PROJECTILE);
                case SNIPER_TOWER:
                    return new Projectile(this.xPos, this.yPos, targetEnemy.getxPos(), targetEnemy.getyPos(), towerDamage, targetEnemy, Projectile.ProjectileType.SNIPER_PROJECTILE);
                default:
                    return new Projectile(this.xPos, this.yPos, targetEnemy.getxPos(), targetEnemy.getyPos(), towerDamage, targetEnemy, Projectile.ProjectileType.MACHINE_GUN_PROJECTILE);
            }
        } else {
            return null;
        }
    }

    public Enemy getTargetEnemy() {
        return targetEnemy;
    }

    // Cộng 90 do chiều của file ảnh png có sẵn xoay chậm hơn 90 độ
    public float getAngleOfRotationInDegrees() {
        if (targetEnemy != null) {
            float deltaX = this.targetEnemy.getxPos() - this.xPos;
            float deltaY = this.targetEnemy.getyPos() - this.yPos;

            angleOfRotation = (float) (Math.atan2(deltaY, deltaX) * (180 / Math.PI)) + 90.0f;
        }

        return angleOfRotation;
    }

    public int getxLoc() {
        return xLoc;
    }

    public int getyLoc() {
        return yLoc;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }

    public double getDamage() {
        return towerDamage;
    }

    public double getFireRange() {
        return fireRange;
    }

    public double getReloadTime() {
        return reloadTime;
    }

    public TowerType getType() {
        return towerType;
    }
}
