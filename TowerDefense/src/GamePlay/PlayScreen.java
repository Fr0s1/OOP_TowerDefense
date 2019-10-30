package GamePlay;

import Enemies.Enemy;
import Enemies.EnemyWave;
import Map.CustomMap;
import Map.PlayMap;
import Tile.RoadTile;
import Tower.*;
import com.sun.org.apache.bcel.internal.generic.IADD;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import java.awt.*;
import java.util.ArrayList;

public class PlayScreen extends BasicGameState {
    public static float delta = 33 * 0.001f; // Giá trị của delta trong hàm update(), tính theo giây = 0.033 giây

    private PlayMap playMap = new PlayMap(CustomMap.map1);

    // Map tile image
    Image roadTile;
    Image towerTile;

    // Enemies images:
    Image normalEnemy;
    Image fastEnemy;
    Image tankEnemy;
    Image bossEnemy;

    EnemyWave wave = new EnemyWave(10);

    Image normalTowerGraphic;
    Image machinegunTowerGraphic;
    Image sniperTowerGraphic;
    Image towerBase;

    Image normalTowerProjectile;
    Image machinegunTowerProjectile;
    Image sniperTowerProjectile;

    Tower t = new NormalTower(9, 3);
    Tower t1 = new MachineGunTower(3, 7);
    Tower t3 = new SniperTower(3, 11);

    ArrayList<Projectile> projectiles;

    Graphics g;

    public PlayScreen(int state) {

    }

    @Override
    public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
        g = new Graphics();
        projectiles = new ArrayList<>();

        // Load các file ảnh đại diện cho đối tượng tương ứng
        roadTile = new Image("graphics/MapTile/sand_tile.png"); // Ảnh đường đi
        towerTile = new Image("graphics/MapTile/grass_tile.png"); // Ảnh tháp

        // Load ảnh quân địch
        normalEnemy = new Image("graphics/Enemies/normal.png"); // Ảnh địch bình thường
        fastEnemy = new Image("graphics/Enemies/fast.png");
        tankEnemy = new Image("graphics/Enemies/tanker.png");
        bossEnemy = new Image("graphics/Enemies/boss.png");

        //
        normalTowerGraphic = new Image("graphics/Towers/normalTower48.png");
        machinegunTowerGraphic = new Image("graphics/Towers/machineGunTower48.png");
        sniperTowerGraphic = new Image("graphics/Towers/sniperTower48.png");
        towerBase = new Image("graphics/Towers/towerBase48.png");

        normalTowerProjectile = new Image("graphics/Projectiles/normalTowerProjectile.png");
        machinegunTowerProjectile = new Image("graphics/Projectiles/machineGunTowerProjectile.png");
        sniperTowerProjectile = new Image("graphics/Projectiles/sniperTowerProjectile.png");

        addTowersInMap();
    }

    @Override
    public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics graphics) throws SlickException {
        drawMap();

        for (Enemy e : EnemyWave.enemyList) {
            if (e.isAlive()) {
                if (e.getType() == Enemy.EnemyType.NORMAL) {
                    drawHealthBar(e);
                    normalEnemy.draw(e.getxPos(), e.getyPos());
                } else {
                    drawHealthBar(e);
                    fastEnemy.draw(e.getxPos(), e.getyPos());
                }
            }
        }

//        healthBar.draw(0, 0, 24, 48);
        drawTowers();
        drawProjectiles();
    }

    @Override
    public void update(GameContainer gameContainer, StateBasedGame stateBasedGame, int delta) throws SlickException {
        wave.update();
        updateTower(Tower.towersList);

        for (Projectile p : projectiles) {
            if (p != null) {
                p.move();
            }
        }
    }

    @Override
    public int getID() {
        return 1;
    }

    public void drawHealthBar(Enemy e) {
        // Vẽ khung hình chữ nhật của cột máu
        g.setColor(Color.black);
        g.drawRect(e.getxPos(), e.getyPos() - 5, (float) 50, 7);

        // Tô đầy hình chữ nhật màu đỏ
        // + 1 vào tọa độ x và -4 ở tọa độ y để không tô vào viền
        g.setColor(Color.red);
        g.fillRect(e.getxPos() + 1, e.getyPos() - 4, 49, 6);

        // Vẽ máu hiện tại theo tỉ lệ giữa máu hiện tại của địch và máu tối đa
        g.setColor(Color.green);
        g.fillRect(e.getxPos() + 1, e.getyPos() - 4, (float) (49 * e.getCurrentHealth() / e.getMaxHealth()), 6);
    }

    // Hàm vẽ ảnh theo mảng 2 chiều đại diện cho map:
    public void drawMap() {
        for (int y = 0; y < playMap.getHeightOfMap(); y++) {
            for (int x = 0; x < playMap.getWidthOfMap(); x++) {
                if (PlayMap.mapTile[y][x] instanceof RoadTile) {
                    roadTile.draw(x * roadTile.getWidth(), y * roadTile.getHeight());
                } else {
                    towerTile.draw(x * roadTile.getWidth(), y * roadTile.getHeight());
                }
            }
        }
    }

    public void addTowersInMap() {
        Tower.towersList.add(t);
        Tower.towersList.add(t1);
        Tower.towersList.add(t3);
    }

    public void updateTower(ArrayList<Tower> towersList) {
        for (Tower tower : towersList) {
            tower.addEnemiesInRange(EnemyWave.enemyList);
            tower.setTarget();
            Projectile newProjectile = tower.shoot(tower.getTargetEnemy());
            if (newProjectile != null) {
                projectiles.add(newProjectile);
            }
            tower.updateTarget();
        }
    }

    public void drawTowers() {
        for (Tower tower : Tower.towersList) {
            Image img;

            switch (tower.getType()) {
                case NORMAL_TOWER:
                    img = normalTowerGraphic;
                    break;
                case MACHINE_GUN_TOWER:
                    img = machinegunTowerGraphic;
                    break;
                default:
                    img = sniperTowerGraphic;
                    break;
            }

            towerBase.draw(tower.getXPos(), tower.getYPos());
            img.setRotation(tower.getAngleOfRotationInDegrees());
            img.drawCentered(tower.getXPos() + normalTowerGraphic.getWidth() / 2, tower.getYPos() + normalTowerGraphic.getHeight() / 2);
        }
    }

    public void drawProjectiles() {
        for (int i = 0; i < projectiles.size(); i++) {
            if (projectiles.get(i).hasArrived()) {
                projectiles.remove(i);
            } else {
                Image projectile;
                switch (projectiles.get(i).getType()) {
                    case MACHINE_GUN_PROJECTILE:
                        projectile = machinegunTowerProjectile;
                        break;
                    case NORMAL_PROJECTILE:
                        projectile = normalTowerProjectile;
                        break;
                    default:
                        projectile = sniperTowerProjectile;
                        break;
                }
                projectile.setRotation((float) projectiles.get(i).angleOfProjectileInDegrees());
                projectile.draw((float) projectiles.get(i).getX(), (float) projectiles.get(i).getY());
            }
        }
    }

    public static double getDelta() {
        return delta;
    }
}