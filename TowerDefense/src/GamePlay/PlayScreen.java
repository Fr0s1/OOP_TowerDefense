package GamePlay;

import Enemies.Enemy;
import Enemies.EnemyWave;
import Map.CustomMap;
import Map.PlayMap;
import Tile.RoadTile;
import Tower.*;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.*;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import java.util.ArrayList;

import static Map.PlayMap.buildableTile;

public class PlayScreen extends BasicGameState {

    private final int mouseClickDelay = 200;

    private static int selectTower = -1;

    private long lastClick = (-1 * mouseClickDelay);

    public static float delta = 33 * 0.001f; // Giá trị của delta trong hàm update() (đơn vị là millisecond), đổi sang giây = 0.033 giây

    private PlayMap playMap = new PlayMap(CustomMap.map1);

    // Map tile image
    Image gameOverImage;

    Image roadTile;
    Image towerTile;

    Image normalTowerGraphic;
    Image machineGunTowerGraphic;
    Image sniperTowerGraphic;
    Image towerBase;

    Image normalTowerProjectile;
    Image machineGunTowerProjectile;
    Image sniperTowerProjectile;

    Image normalTowerButton;
    Image machineGunTowerButton;
    Image sniperTowerButton;
    Image upgradeButtonGraphic;
    Image backGround;
    Image towerBox;

    Image showLife;
    Image showWaveGraphic;
    Image sellButtonGraphic;
    Image nextWaveGraphic;
    Image showMoneyGraphic;

    Image sellTowerImage;
    Image upgradeTowerImage;

    Image fastForwardGraphic;

    Rectangle buyNormalTowerButton;
    Rectangle buyMachineGunButton;
    Rectangle buySniperTowerButton;
    Rectangle upgradeButton;
    Rectangle sellButton;
    Rectangle nextWave;
    Rectangle fastForwardButton;

    public String showMoney;
    public String showLives;
    public String showWave;

    Image[] normalEnemyWalkImages = new Image[19];
    Animation normalEnemyWalkAnimation;

    Image[] fastEnemyWalkImages = new Image[19];
    Animation fastEnemyWalkAnimation;

    Image[] tankEnemyWalkImages = new Image[19];
    Animation tankEnemyWalkAnimation;

    Image[] bossEnemyWalkImages = new Image[19];
    Animation bossEnemyWalkAnimation;

    EnemyWave wave = new EnemyWave();

    ArrayList<Projectile> projectiles;
    ArrayList<Tower> towersOnMap = new ArrayList<Tower>();

    Graphics g;

    Player player1;

    int currentLevel = 1;

    boolean gameOver = false;

    int countFastForwardClickedTime = 0;
    boolean fastForward = false;

    boolean waveInProgress = false;

    Music themeSong;

    public PlayScreen(int state) {

    }

    @Override
    public int getID() {
        return 1;
    }

    @Override
    public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
        g = new Graphics();

        themeSong = new Music("sound_effect/SuperMarioBros.wav");

        projectiles = new ArrayList<>();

        player1 = new Player();

        loadImages();

        loadNormalEnemyWalkAnimation();
        loadFastEnemyWalkAnimation();
        loadTankEnemyWalkAnimation();
        loadBossEnemyWalkAnimation();

        createMenuButtons();

//        themeSong.play();
    }

    @Override
    public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics graphics) throws SlickException {

        drawMap();

        drawMenu();

        graphics.drawString(showLives, 730 + 40, 500 + 5);
        graphics.drawString(showWave, 780 + 70, 250 + 25);
        graphics.drawString(showMoney, 725 + 50, 540 + 15);

        drawTowers();

        if (!gameOver) {

            drawEnemyWave();

            drawProjectiles();

            drawMenuButtonOption(gameContainer);

        } else {

            gameOverImage.draw(PlayMap.getWidthOfMapInPixel() / 2 - gameOverImage.getWidth() / 2, PlayMap.getHeightOfMapInPixel() / 2 - gameOverImage.getHeight() / 2);

        }

    }


    @Override
    public void update(GameContainer gameContainer, StateBasedGame stateBasedGame, int delta) throws SlickException {

        isGameOver();

        showLives = "Current lives: " + player1.getCurrentLife();
        showMoney = String.valueOf(player1.getCurrentMoney());
        showWave = String.valueOf(currentLevel);

        if (!gameOver) {

            if (fastForward) {
                gameContainer.setTargetFrameRate(50);
            } else  {
                gameContainer.setTargetFrameRate(30);
            }

            if (waveInProgress) {

                wave.update(currentLevel);
                updateTower(towersOnMap);
                updateProjectileList();

                isWaveFinished();
            }

            int xLoc = Math.round(Mouse.getX() / 48);
            int yLoc = Math.round((gameContainer.getHeight() - Mouse.getY()) / 48);

            if (Mouse.isButtonDown(0)) {

                mouseClicked(xLoc, yLoc, stateBasedGame, gameContainer);

            }

            normalEnemyWalkAnimation.update(delta);
            fastEnemyWalkAnimation.update(delta);
            tankEnemyWalkAnimation.update(delta);
            bossEnemyWalkAnimation.update(delta);

        }

    }

    public void isGameOver() {
        if (Player.getCurrentLife() == 0) {
            gameOver = true;
        }
    }

    public void isWaveFinished() {
        if (Player.getCurrentLife() > 0 && EnemyWave.enemyQueue.size() == 0 && EnemyWave.activeEnemyList.size() == 0) {
            wave.setRespawn();
            currentLevel++;
            waveInProgress = false;
        }
    }

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

    public void drawMenu() {
        int size = 60;
        int boxSize = 80;

        backGround.draw(720, 0, 240, 720);
        towerBox.draw(740, 40, boxSize, boxSize);
        towerBox.draw(840, 40, boxSize, boxSize);
        towerBox.draw(740, 140, boxSize, boxSize);
        normalTowerButton.draw(750, 50, size, size);
        machineGunTowerButton.draw(850, 50, size, size);
        sniperTowerButton.draw(750, 150, size, size);

        showWaveGraphic.draw(760, 250);
        showLife.draw(730, 500, 30, 30);
        showMoneyGraphic.draw(725, 540, 45, 45);
        nextWaveGraphic.draw(730 + 30, 350 - 20);

        sellButtonGraphic.draw(745, 395);
        upgradeButtonGraphic.draw(850, 395, size, size);

        fastForwardGraphic.draw(840 - fastForwardGraphic.getWidth() / 2, 624);
    }

    public void createMenuButtons() {

        buyNormalTowerButton = new Rectangle(750, 50, normalTowerButton.getWidth(), normalTowerButton.getHeight());
        buyMachineGunButton = new Rectangle(850, 50, machineGunTowerButton.getWidth(), machineGunTowerButton.getHeight());
        buySniperTowerButton = new Rectangle(750, 150, sniperTowerGraphic.getWidth(), sniperTowerGraphic.getHeight());
        nextWave = new Rectangle(760, 330, nextWaveGraphic.getWidth(), nextWaveGraphic.getHeight());
        sellButton = new Rectangle(745, 395, sellButtonGraphic.getWidth(), sellButtonGraphic.getHeight());
        upgradeButton = new Rectangle(850, 395, upgradeButtonGraphic.getWidth(), upgradeButtonGraphic.getHeight());

        fastForwardButton = new Rectangle(840 - fastForwardGraphic.getWidth() / 2, 624, fastForwardGraphic.getWidth(), fastForwardGraphic.getHeight());
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
                    img = machineGunTowerGraphic;
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
                        projectile = machineGunTowerProjectile;
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
    public void loadImages() throws SlickException {

        normalTowerButton = new Image("graphics/Towers/normalTower48.png");
        machineGunTowerButton = new Image("graphics/Towers/machineGunTower48.png");
        sniperTowerButton = new Image("graphics/Towers/sniperTower48.png");

        upgradeButtonGraphic = new Image("graphics/UpgradeTowerButton.png");
        backGround = new Image("graphics/background.jpg");
        towerBox = new Image("graphics/towerBox.png");

        showLife = new Image("graphics/Heart.png");
        showWaveGraphic = new Image("graphics/WaveGraphic.png");
        sellButtonGraphic = new Image("graphics/SellButtonGraphic.png");
        nextWaveGraphic = new Image("graphics/nextWaveActive.png");
        showMoneyGraphic = new Image("graphics/CurrencyGraphic.png");

        fastForwardGraphic = new Image("graphics/FastForwardButton.png");

        sellTowerImage = new Image("graphics/SellSelectGraphic.png");
        upgradeTowerImage = new Image("graphics/UpgradeSelectGraphic.png");
        gameOverImage = new Image("graphics/GameOver.jpg");


        roadTile = new Image("graphics/MapTile/sand_tile.png"); // Ảnh đường đi
        towerTile = new Image("graphics/MapTile/grass_tile.png"); // Ảnh tháp


        normalTowerGraphic = new Image("graphics/Towers/normalTower48.png");
        machineGunTowerGraphic = new Image("graphics/Towers/machineGunTower48.png");
        sniperTowerGraphic = new Image("graphics/Towers/sniperTower48.png");
        towerBase = new Image("graphics/Towers/towerBase48.png");


        normalTowerProjectile = new Image("graphics/Projectiles/normalTowerProjectile.png");
        machineGunTowerProjectile = new Image("graphics/Projectiles/machineGunTowerProjectile.png");
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

    public boolean isBuildable(int xLoc, int yLoc) {

        for (int i = 0; i < towersOnMap.size(); i++) {

            if (towersOnMap.get(i).getxLoc() == xLoc && towersOnMap.get(i).getyLoc() == yLoc)

                return false;

        }

        return true;
    }

    private void mouseClicked(int x, int y, StateBasedGame sbg, GameContainer gameContainer) throws SlickException {
        //protection against multiple click registration
        if (lastClick + mouseClickDelay > System.currentTimeMillis())
            return;
        lastClick = System.currentTimeMillis();

        if (selectTower == -1) {

            if (buyNormalTowerButton.contains(Mouse.getX(), gameContainer.getHeight() - Mouse.getY()))
                selectTower = 1;

            else if (buyMachineGunButton.contains(Mouse.getX(), gameContainer.getHeight() - Mouse.getY()))
                selectTower = 2;

            else if (buySniperTowerButton.contains(Mouse.getX(), gameContainer.getHeight() - Mouse.getY()))
                selectTower = 3;

            else if (upgradeButton.contains(Mouse.getX(), gameContainer.getHeight() - Mouse.getY()))
                selectTower = 4;

            else if (sellButton.contains(Mouse.getX(), gameContainer.getHeight() - Mouse.getY()))
                selectTower = 5;

            else if (fastForwardButton.contains(Mouse.getX(), gameContainer.getHeight() - Mouse.getY()))

                if (++countFastForwardClickedTime % 2 == 1) fastForward = true;
                else fastForward = false;

            else if (nextWave.contains(Mouse.getX(), gameContainer.getHeight() - Mouse.getY()))

                waveInProgress = true;

            return;
        }

        if (selectTower > 0 && selectTower < 4) {

            if (buildableTile(x, y) && isBuildable(x, y)) {

                switch (selectTower) {

                    case 1:
                        if (player1.getCurrentMoney() >= NormalTower.cost) {

                            Tower newNormalTower = new NormalTower(x, y);
                            towersOnMap.add(newNormalTower);
                            player1.spendMoney(NormalTower.cost);
                            break;

                        } else
                            break;

                    case 2:
                        if (player1.getCurrentMoney() >= MachineGunTower.cost) {

                            Tower newMachineTower = new MachineGunTower(x, y);
                            towersOnMap.add(newMachineTower);
                            player1.spendMoney(MachineGunTower.cost);
                            break;

                        } else break;

                    case 3:
                        if (player1.getCurrentMoney() >= SniperTower.cost) {

                            Tower newSniperTower = new SniperTower(x, y);
                            towersOnMap.add(newSniperTower);
                            player1.spendMoney(SniperTower.cost);
                            break;

                        } else break;
                }

            }

        } else if (selectTower == 4) {

            for (int i = 0; i < towersOnMap.size(); i++) {

                if (towersOnMap.get(i).getxLoc() == x && towersOnMap.get(i).getyLoc() == y) {

                    if (player1.getCurrentMoney() >= towersOnMap.get(i).getCost() / 2) {

                        player1.spendMoney(towersOnMap.get(i).getCost() / 2);
                        towersOnMap.get(i).upgradeTower();

                    } else break;

                }

            }

        } else if (selectTower == 5) {

            for (int i = 0; i < towersOnMap.size(); i++) {

                if (towersOnMap.get(i).getxLoc() == x && towersOnMap.get(i).getyLoc() == y) {

                    if (waveInProgress) {

                        player1.addMoney(towersOnMap.get(i).getCost() * 7 / 10);

                    } else {

                        player1.addMoney(towersOnMap.get(i).getCost());
                    }

                    towersOnMap.remove(i);

                }

            }

        }

        selectTower = -1;

    }

    public void drawMenuButtonOption(GameContainer gameContainer) {

        g.setColor(Color.yellow);

        int mouseXPos = Mouse.getX();
        int mouseYPos = gameContainer.getHeight() - Mouse.getY();

        switch (selectTower) {

            case 1:
                g.drawOval(mouseXPos - (float) NormalTower.fireRange, mouseYPos - (float) NormalTower.fireRange, (float) NormalTower.fireRange * 2, (float) NormalTower.fireRange * 2);
                normalTowerButton.drawCentered(mouseXPos, mouseYPos);

                break;

            case 2:
                g.drawOval(Mouse.getX() - (float) MachineGunTower.fireRange, gameContainer.getHeight() - Mouse.getY() - (float) MachineGunTower.fireRange, (float) MachineGunTower.fireRange * 2, (float) MachineGunTower.fireRange * 2);
                machineGunTowerButton.drawCentered(Mouse.getX(), gameContainer.getHeight() - Mouse.getY());

                break;

            case 3:
                g.drawOval(mouseXPos - (float) SniperTower.fireRange, mouseYPos - (float) SniperTower.fireRange, (float) SniperTower.fireRange * 2, (float) SniperTower.fireRange * 2);
                sniperTowerButton.drawCentered(mouseXPos, mouseYPos);

                break;

            case 4:
                upgradeTowerImage.drawCentered(mouseXPos, mouseYPos);
                break;

            case 5:
                sellTowerImage.drawCentered(mouseXPos, mouseYPos);
                break;

        }

    }

    public static double getDelta() {

        return delta;

    }

}