package Tower;

import Enemies.Enemy;
import GamePlay.PlayScreen;
import Map.PlayMap;

import java.util.*;

class CompareEnemy implements Comparator<Enemy> {

    @Override
    public int compare(Enemy target1, Enemy target2) {

        // Ưu tiên mục tiêu đầu tiêu, nhưng nếu xuất hiện địch di chuyển nhanh thì chuyển sang mục tiêu ấy

        if (target1.getDistanceTraveled() < target2.getDistanceTraveled()) {

            if (target1.getOriginalMovementSpeed() > target2.getOriginalMovementSpeed()) {

                return -1;

            } else {

                return 1;

            }

        } else if (target1.getDistanceTraveled() > target2.getDistanceTraveled()) {

            return -1;

        } else {

            return 0;

        }

    }

}

class CompareSlowedEnemy implements Comparator<Enemy> {

    @Override
    public int compare(Enemy target1, Enemy target2) {

        if (target1.getMovementSpeed() < target2.getMovementSpeed()) {

            return 1;

        } else if (target1.getMovementSpeed() > target2.getMovementSpeed()) {

            return -1;

        } else {

            return 0;

        }

    }

}

public abstract class Tower {
    public enum TowerType {
        NORMAL_TOWER, MACHINE_GUN_TOWER, SNIPER_TOWER, SLOW_TOWER
    }

    private TowerType towerType;

    // Vị trí tháp theo mảng hai chiều
    private int xLoc, yLoc;

    // Vị trí tháp theo Pixel
    private int xPos, yPos;

    private double towerDamage;    // Sát thương của tháp
    private double fireRange;   // Tầm bắn của tháp
    private double timeSinceLastShot;
    private double reloadTime;  // Thời gian nạp đạn

    private int towerCost;

    private int towerLevel;

    private float angleOfRotation;

    private CompareEnemy compareEnemy;
    private CompareSlowedEnemy compareSlowedEnemy;

    private Queue<Enemy> enemiesInRange; // Hàng chờ lưu những quân địch có trong tầm bắn của tháp
    private PriorityQueue<Enemy> slowedEnemiesInRange;

    private Enemy targetEnemy; // Mục tiêu hiện tại của tháp

    private boolean canRefund;

    public Tower(int xLoc, int yLoc, double damage, double fireRange, double reloadTime, TowerType type, int towerCost) {
        this.towerLevel = 1;

        this.xLoc = xLoc;
        this.yLoc = yLoc;
        this.xPos = xLoc * PlayMap.tileSize;
        this.yPos = yLoc * PlayMap.tileSize;

        this.towerDamage = damage;
        this.fireRange = fireRange;

        angleOfRotation = 0;

        compareEnemy = new CompareEnemy();
        compareSlowedEnemy = new CompareSlowedEnemy();

        enemiesInRange = new PriorityQueue(compareEnemy);

        slowedEnemiesInRange = new PriorityQueue(compareSlowedEnemy);

        this.targetEnemy = null;

        timeSinceLastShot = 0;

        this.reloadTime = reloadTime;

        this.towerType = type;
        this.towerCost = towerCost;

        canRefund = true;
    }

    public double calculateDistanceToEnemy(Enemy enemy) {

        double deltaXPos = this.xPos - enemy.getxPos();
        double deltaYPos = this.yPos - enemy.getyPos();
        double distance = Math.sqrt(Math.pow(deltaXPos, 2) + Math.pow(deltaYPos, 2));

        return distance;
    }

    /* Tìm tất cả các quân địch trong tầm bắn
       Dùng hàng chờ ưu tiên (Priority Queue) vì:
            + Ưu tiên quân địch có di chuyển nhanh nhất (máu cũng ít nhất) để bắn trước.
            + Quân địch nào vào tầm bắn trước sẽ được trọn trước nếu cùng tốc độ di chuyển
            + Nếu mục tiêu tháp đang chọn ra khỏi tầm bắn sẽ cập nhật luôn được mục tiêu tiếp theo
    * */
    public void addEnemiesInRange(ArrayList<Enemy> wave) {

        enemiesInRange.clear();
        slowedEnemiesInRange.clear();

        for (Enemy e : wave) {

            double distance = calculateDistanceToEnemy(e);

            if (distance <= fireRange) {

                if (towerType != TowerType.SLOW_TOWER) {

                    enemiesInRange.add(e);

                } else {

                    slowedEnemiesInRange.add(e);

                }

            }

        }

    }

    // Chọn mục tiêu là phần từ đầu tiên của hàng chờ ưu tiên
    public void setTarget() {

        if (towerType == TowerType.SLOW_TOWER) {

            if (slowedEnemiesInRange.size() >= 1) {

                targetEnemy = slowedEnemiesInRange.peek();

            } else {

                targetEnemy = null;

            }

        } else {

            if (enemiesInRange.size() >= 1) {

                targetEnemy = enemiesInRange.peek();

            } else {

                targetEnemy = null;

            }

        }

    }

    // Chọn mục tiêu tiếp theo trong hàng chờ nếu quân địch ra khỏi tầm bắn
    public void updateTarget() {

        if (targetEnemy != null) {

            double distance = calculateDistanceToEnemy(targetEnemy);

            if (distance > fireRange) {

                if (towerType != TowerType.SLOW_TOWER) {

                    enemiesInRange.poll();

                } else {

                    slowedEnemiesInRange.poll();

                }

                setTarget();

            }

        }

    }

    public boolean canAttack() {

        timeSinceLastShot += PlayScreen.delta;

        if (timeSinceLastShot >= reloadTime && targetEnemy != null) {

            timeSinceLastShot = 0;

            return true;

        } else {

            return false;

        }

    }

    public Projectile attackEnemy(Enemy targetEnemy) {

        if (canAttack()) {

            timeSinceLastShot = 0;

            double baseEnemySlowDuration = 2;

            switch (towerType) {

                case NORMAL_TOWER:
                    return new Projectile(this.xPos, this.yPos, targetEnemy.getxPos(), targetEnemy.getyPos(), towerDamage, targetEnemy, Projectile.ProjectileType.NORMAL_PROJECTILE);

                case MACHINE_GUN_TOWER:
                    return new Projectile(this.xPos, this.yPos, targetEnemy.getxPos(), targetEnemy.getyPos(), towerDamage, targetEnemy, Projectile.ProjectileType.MACHINE_GUN_PROJECTILE);

                case SLOW_TOWER:
                    return new Projectile(this.xPos, this.yPos, targetEnemy.getxPos(), targetEnemy.getyPos(), towerDamage, targetEnemy, Projectile.ProjectileType.SLOW_TOWER_PROJECTILE, baseEnemySlowDuration + towerLevel);

                default:
                    return new Projectile(this.xPos, this.yPos, targetEnemy.getxPos(), targetEnemy.getyPos(), towerDamage, targetEnemy, Projectile.ProjectileType.SNIPER_PROJECTILE);

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

    public void upgradeTower() {

        if (this.towerLevel < 3) {

            towerLevel++;

            this.towerDamage *= 3.0 / 2;

            this.towerCost = Math.round((this.towerCost * 3) / 2);

        }

    }

    public int getTowerLevel() {
        return towerLevel;
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

    public int getCost() {
        return towerCost;
    }

    public void setCantRefund() {
        canRefund = false;
    }

    public boolean isRefundable() {
        return canRefund;
    }
}