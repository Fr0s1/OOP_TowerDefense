package Tower;

import Enemies.Enemy;
import Map.PlayMap;

public class Projectile {
    public enum ProjectileType {
        MACHINE_GUN_PROJECTILE, NORMAL_PROJECTILE, SNIPER_PROJECTILE
    }

    // Vị trí (Pixel) theo đường bay của đạn
    private double xLoc;
    private double yLoc;

    // Vị trí đích của đạn tức vị trí của đích tại đúng thời điểm đạn vừa được bắn ra
    private double xDest;
    private double yDest;

    // Vị trí đạn được bắn ra
    private double xInit;
    private double yInit;

    private double damage;
    private double speed = 20;
    private ProjectileType projType;
    private boolean arrivedAtTarget = false;
    private Enemy targetEnemy;

    public Projectile(double xInit, double yInit, double xDest, double yDest, double damage, Enemy targetEnemy, ProjectileType type) {
        this.xInit = xInit;
        this.yInit = yInit;
        this.xDest = xDest;
        this.yDest = yDest;

        this.xLoc = xInit + 12 * Math.cos(angleOfProjectileInRadians());
        this.yLoc = yInit + 12 * Math.sin(angleOfProjectileInRadians());

        arrivedAtTarget = false;

        this.damage = damage;
        this.targetEnemy = targetEnemy;

        this.projType = type;

        if (projType == ProjectileType.SNIPER_PROJECTILE) {
            speed = 30;
        }
    }

    public double angleOfProjectileInDegrees() {
        return (180 / Math.PI) * Math.atan2(yDest - yInit, xDest - xInit);
    }

    public double angleOfProjectileInRadians() {
        return Math.atan2(yDest - yInit, xDest - xInit);
    }

    public void move() {
        // Kiểm tra xem đạn đã trúng quân địch
        if (Math.abs(xLoc - xDest) <= PlayMap.tileSize / 3 && Math.abs(yLoc - yDest) <= PlayMap.tileSize / 3) {

            arrivedAtTarget = true;
            targetEnemy.takeDamage(damage);

        } else {

            xLoc += speed * Math.cos(angleOfProjectileInRadians());

            yLoc += speed * Math.sin(angleOfProjectileInRadians());

        }
    }

    public boolean hasArrived() {
        return arrivedAtTarget;
    }

    public double getX() {
        return this.xLoc;
    }

    public double getY() {
        return this.yLoc;
    }

    public double getSpeed() {
        return this.speed;
    }

    public ProjectileType getType() {
        return this.projType;
    }
}
