package Tower;

import Enemies.Enemy;

public class Projectile {
    public enum ProjectileType {
        MACHINE_GUN, NORMAL, SNIPER
    }

    // Vị trí (Pixel) theo đường bay của đạn
    private double xLoc;
    private double yLoc;

    // Vị trí tới của đạn tức vị trí của mục tiêu tại thời điểm đạn vừa được bắn ra
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

    public Projectile(double xInit, double yInit, double xDest, double yDest, double damage, Enemy targetEnemy) {
        this.xInit = xInit;
        this.yInit = yInit;
        this.xDest = xDest;
        this.yDest = yDest;

        this.xLoc = xInit + 12 * Math.cos(angleOfProjectileInRadians());
        this.yLoc = yInit + 12 * Math.sin(angleOfProjectileInRadians());

        arrivedAtTarget = false;

        this.damage = damage;
        this.targetEnemy = targetEnemy;

//        projType = pType;
        if (projType == ProjectileType.SNIPER) {
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
        if (Math.abs(xLoc - xDest) < speed / 2 && Math.abs(yLoc - yDest) < speed / 2) {
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
