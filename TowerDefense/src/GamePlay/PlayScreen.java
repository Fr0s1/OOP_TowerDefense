package GamePlay;

import Enemies.Enemy;
import Enemies.EnemyWave;
import Map.CustomMap;
import Map.PlayMap;
import Tile.RoadTile;
import Tower.*;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import java.util.ArrayList;

public class PlayScreen extends BasicGameState {
    public static float delta = 33 * 0.001f; // Giá trị của delta trong hàm update() của lớp PlayScreen, nôm na là thời gian update giữa các khung hình

    private PlayMap playMap = new PlayMap(CustomMap.map1);

    // Map tile image
    Image roadTile;
    Image towerTile;

    // Enemies images:
    Image normalCreep;
    Image fastCreep;

    EnemyWave wave = new EnemyWave(10);

    Image normalTower;
    Image towerBase;
    Image normalTowerProjectile;

    Tower t = new NormalTower(5, 3);
    Tower t1 = new NormalTower(3, 7);
    Tower t3 = new NormalTower(3, 11);

    public PlayScreen(int state) {

    }

    @Override
    public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
        // Load các file ảnh đại diện cho đối tượng tương ứng
        roadTile = new Image("graphics/MapTile/sand_tile.png"); // Ảnh đường đi
        towerTile = new Image("graphics/MapTile/grass_tile.png"); // Ảnh tháp
        normalCreep = new Image("graphics/Enemies/normal.png"); // Ảnh địch bình thường
        fastCreep = new Image("graphics/Enemies/fast.png");
        normalTower = new Image("graphics/Towers/normalTower48.png");
        towerBase = new Image("graphics/Towers/towerBase48.png");
        normalTowerProjectile = new Image("graphics/Projectiles/normalTowerProjectile.png");

        addTowersInMap();
    }

    @Override
    public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics graphics) throws SlickException {
        drawMap();

        for (Enemy e : EnemyWave.enemyList) {
            if (e.isAlive()) {
                if (e.getType() == Enemy.EnemyType.NORMAL) {
                    normalCreep.draw(e.getxPos(), e.getyPos());
                } else {
                    fastCreep.draw(e.getxPos(), e.getyPos());
                }
            }
        }

        drawTowers();
    }

    @Override
    public void update(GameContainer gameContainer, StateBasedGame stateBasedGame, int delta) throws SlickException {
        wave.update();
        updateTower(Tower.towersList);
    }

    @Override
    public int getID() {
        return 1;
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
            tower.updateTarget();
            tower.shoot(tower.getTargetEnemy());
        }
    }

    public void drawTowers() {
        for (Tower tower : Tower.towersList) {
            Image img = normalTower;
            towerBase.draw(tower.getXPos(), tower.getYPos());
            img.setRotation(tower.getAngleOfRotationInDegrees());
            img.drawCentered(tower.getXPos() + normalTower.getWidth() / 2, tower.getYPos() + normalTower.getHeight() / 2);
        }
    }

    public static double getDelta() {
        return delta;
    }
}