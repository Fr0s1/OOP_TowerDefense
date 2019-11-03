package GamePlay;

import Enemies.Enemy;
import Enemies.EnemyWave;
import Map.CustomMap;
import Map.PlayMap;
import Tile.RoadTile;
import Tower.*;
import org.newdawn.slick.*;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import java.util.ArrayList;

public class PlayScreen extends BasicGameState {
    public static float delta = 33 * 0.001f; // Giá trị của delta trong hàm update() (đơn vị là millisecond), đổi sang giây = 0.033 giây

    private PlayMap playMap = new PlayMap(CustomMap.map1);

    // Map tile image
    Image roadTile;
    Image towerTile;

    Image normalTowerGraphic;
    Image machinegunTowerGraphic;
    Image sniperTowerGraphic;
    Image towerBase;

    Image normalTowerProjectile;
    Image machinegunTowerProjectile;
    Image sniperTowerProjectile;


    Image[] normalEnemyWalkImages = new Image[19];
    Animation normalEnemyWalkAnimation;

    Image[] fastEnemyWalkImages = new Image[19];
    Animation fastEnemyWalkAnimation;

    Image[] tankEnemyWalkImages = new Image[19];
    Animation tankEnemyWalkAnimation;

    Image[] bossEnemyWalkImages = new Image[19];
    Animation bossEnemyWalkAnimation;

    EnemyWave wave = new EnemyWave();

    Tower t = new MachineGunTower(9, 3);
    Tower t1 = new NormalTower(3, 3);
    Tower t3 = new SniperTower(3, 11);

    ArrayList<Projectile> projectiles;
    ArrayList<Tower> towersOnMap = new ArrayList<Tower>();

    Graphics g;

    Music themeSong;

    int currentLevel = 1;

    public PlayScreen(int state) {

    }

    @Override
    public int getID() {
        return 1;
    }

    @Override
    public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
        g = new Graphics();
        projectiles = new ArrayList<>();

        loadEnemiesAndTowersImage();

        loadNormalEnemyWalkAnimation();
        loadFastEnemyWalkAnimation();
        loadTankEnemyWalkAnimation();
        loadBossEnemyWalkAnimation();

        addTowersInMap();

        themeSong = new Music("sound_effect/Overture1928.wav");

//        themeSong.loop();
    }

    @Override
    public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics graphics) throws SlickException {

        drawMap();

//        drawMenu();

        drawEnemyWave();

        drawTowers();

        drawProjectiles();

    }

    @Override
    public void update(GameContainer gameContainer, StateBasedGame stateBasedGame, int delta) throws SlickException {

        wave.update(9);

        updateTower(towersOnMap);
        updateProjectileList();

        normalEnemyWalkAnimation.update(delta);
        fastEnemyWalkAnimation.update(delta);
        tankEnemyWalkAnimation.update(delta);
        bossEnemyWalkAnimation.update(delta);
    }

//    public void drawMenu() {
//        for (int y = 0; y < Game.WindowHeight; y += 64) {
//            for (int x = 15 * 48; x < Game.WindowWidth; x += 64) {
//                menuTile.draw(x, y);
//            }
//        }
//    }

    // Hàm vẽ sân chơi theo mảng 2 chiều đại diện cho map:
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

    public void drawEnemyWave() {

        for (Enemy currentEnemy : EnemyWave.activeEnemyList) {

            if (currentEnemy.isVisible()) {

                drawHealthBar(currentEnemy);

                Animation a;

                switch (currentEnemy.getType()) {
                    case FAST:
                        a = fastEnemyWalkAnimation;
                        break;
                    case NORMAL:
                        a = normalEnemyWalkAnimation;
                        break;
                    case TANKER:
                        a = tankEnemyWalkAnimation;
                        break;
                    default:
                        a = bossEnemyWalkAnimation;
                        break;
                }


                if (currentEnemy.getXDirection() == -1) {

                    a.getCurrentFrame().getFlippedCopy(true, false).draw(currentEnemy.getxPos(), currentEnemy.getyPos());

                } else {

                    a.getCurrentFrame().draw(currentEnemy.getxPos(), currentEnemy.getyPos());

                }

            }

        }

    }

    // Vẽ cột máu cho mỗi quân địch
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

    public void drawTowers() {

        for (Tower tower : towersOnMap) {
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

    public void addTowersInMap() {
        towersOnMap.add(t);
        towersOnMap.add(t1);
        towersOnMap.add(t3);
    }

    public void updateTower(ArrayList<Tower> towersOnMap) {

        for (Tower tower : towersOnMap) {

            tower.addEnemiesInRange(EnemyWave.activeEnemyList);

            tower.setTarget();

            Projectile newProjectile = tower.attackEnemy(tower.getTargetEnemy());

            if (newProjectile != null) {
                projectiles.add(newProjectile);
            }

            tower.updateTarget();

        }

    }

    public void updateProjectileList() {

        for (Projectile p : projectiles) {

            p.move();

        }

    }

    // Load các file ảnh tháp và bản đồ
    public void loadEnemiesAndTowersImage() throws SlickException {
        roadTile = new Image("graphics/MapTile/sand_tile.png"); // Ảnh đường đi
        towerTile = new Image("graphics/MapTile/grass_tile.png"); // Ảnh tháp


        normalTowerGraphic = new Image("graphics/Towers/normalTower48.png");
        machinegunTowerGraphic = new Image("graphics/Towers/machineGunTower48.png");
        sniperTowerGraphic = new Image("graphics/Towers/sniperTower48.png");
        towerBase = new Image("graphics/Towers/towerBase48.png");

        normalTowerProjectile = new Image("graphics/Projectiles/normalTowerProjectile.png");
        machinegunTowerProjectile = new Image("graphics/Projectiles/machineGunTowerProjectile.png");
        sniperTowerProjectile = new Image("graphics/Projectiles/sniperTowerProjectile.png");

    }

    // Load hiệu ứng hình ảnh đi các quân địch
    public void loadNormalEnemyWalkAnimation() throws SlickException {

        for (int i = 0; i < 19; i++) {

            String path = "graphics/Enemies/NormalEnemyAnimation/2_enemies_1_walk_0";

            if (i <= 9) {
                path += "0" + i + ".png";
            } else {
                path += i + ".png";
            }

            normalEnemyWalkImages[i] = new Image(path);
        }

        normalEnemyWalkAnimation = new Animation(normalEnemyWalkImages, 50);
    }

    public void loadFastEnemyWalkAnimation() throws SlickException {

        for (int i = 0; i < 19; i++) {

            String path = "graphics/Enemies/FastAnimation/1_enemies_1_run_0";

            if (i <= 9) {
                path += "0" + i + ".png";
            } else {
                path += i + ".png";
            }

            fastEnemyWalkImages[i] = new Image(path);
        }

        fastEnemyWalkAnimation = new Animation(fastEnemyWalkImages, 50);
    }

    public void loadTankEnemyWalkAnimation() throws SlickException {

        for (int i = 0; i < 19; i++) {

            String path = "graphics/Enemies/TankerAnimation/6_enemies_1_walk_0";

            if (i <= 9) {
                path += "0" + i + ".png";
            } else {
                path += i + ".png";
            }

            tankEnemyWalkImages[i] = new Image(path);
        }

        tankEnemyWalkAnimation = new Animation(tankEnemyWalkImages, 50);
    }

    public void loadBossEnemyWalkAnimation() throws SlickException {

        for (int i = 0; i < 19; i++) {

            String path = "graphics/Enemies/BossAnimation/9_enemies_1_walk_0";

            if (i <= 9) {
                path += "0" + i + ".png";
            } else {
                path += i + ".png";
            }

            bossEnemyWalkImages[i] = new Image(path);
        }

        bossEnemyWalkAnimation = new Animation(bossEnemyWalkImages, 50);
    }

    public static double getDelta() {
        return delta;
    }
}