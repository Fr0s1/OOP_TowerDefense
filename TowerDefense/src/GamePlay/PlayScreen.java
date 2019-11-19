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

    private static int selectMenuOption = -1;

    private long lastClick = (-1 * mouseClickDelay);

    public static float delta = 33 * 0.001f; // Giá trị của delta trong hàm update() (đơn vị là millisecond), đổi sang giây = 0.033 giây

    private PlayMap playMap = new PlayMap(CustomMap.map1);

    // Map tile image
    private Image gameOverImage;

    private Image roadTile;
    private Image towerTile;

    private Image normalTowerGraphic;

    private Image machineGunTowerGraphic;
    private Image sniperTowerGraphic;
    private Image slowTowerGraphic;
    private Image towerBase;

    private Image normalTowerProjectile;
    private Image machineGunTowerProjectile;
    private Image sniperTowerProjectile;
    private Image slowTowerProjectile;

    private Image normalTowerButton;
    private Image machineGunTowerButton;
    private Image sniperTowerButton;
    private Image slowTowerButton;

    private Image upgradeButtonGraphic;
    private Image backGround;
    private Image towerBox;

    private Image showLife;

    private Image showWaveGraphic;
    private Image waveInActive;

    private Image sellButtonGraphic;
    private Image nextWaveGraphic;
    private Image showMoneyGraphic;

    private Image sellTowerImage;
    private Image upgradeTowerImage;
    private Image[] towerLevel;

    private Image fastForwardGraphic;

    private Rectangle buyNormalTowerButton;
    private Rectangle buyMachineGunButton;
    private Rectangle buySniperTowerButton;
    private Rectangle buySlowTowerButton;

    private Rectangle upgradeButton;
    private Rectangle sellButton;
    private Rectangle nextWave;
    private Rectangle fastForwardButton;

    private String showMoney;
    private String showLives;
    private String showWave;

    private String normalTowerCost;
    private String machineGunTowerCost;
    private String sniperTowerCost;
    private String slowTowerCost;

    private Image[] normalEnemyWalkImages = new Image[19];
    private Animation normalEnemyWalkAnimation;

    private Image[] fastEnemyWalkImages = new Image[19];
    private Animation fastEnemyWalkAnimation;

    private Image[] tankEnemyWalkImages = new Image[19];
    private Animation tankEnemyWalkAnimation;

    private Image[] bossEnemyWalkImages = new Image[19];
    private Animation bossEnemyWalkAnimation;

    EnemyWave wave = new EnemyWave();

    private ArrayList<Projectile> projectiles;
    ArrayList<Tower> towersOnMap = new ArrayList<Tower>();

    private Graphics g;

    private Player player1;

    private int currentLevel = 1;

    private boolean gameOver = false;

    private int countFastForwardClickedTime = 0;
    private boolean fastForward = false;

    private boolean waveInProgress = false;

    public PlayScreen(int state) {

    }

    @Override
    public int getID() {
        return 1;
    }

    @Override
    public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
        g = new Graphics();

        Music themeSong = new Music("sound_effect/Overture1928.wav");

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

        drawMenu(graphics);

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

        if (!gameOver) {

            loadMenuInformation();

            if (fastForward) {
                gameContainer.setTargetFrameRate(60);
            } else {
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

    // Load các file ảnh tháp và bản đồ
    public void loadImages() throws SlickException {

        normalTowerButton = new Image("graphics/Towers/normalTower48.png");
        machineGunTowerButton = new Image("graphics/Towers/machineGunTower48.png");
        sniperTowerButton = new Image("graphics/Towers/sniperTower48.png");
        slowTowerButton = new Image("graphics/Towers/SlowTower48.png");

        upgradeButtonGraphic = new Image("graphics/UpgradeTowerButton.png");
        backGround = new Image("graphics/background.jpg");
        towerBox = new Image("graphics/towerBox.png");

        showLife = new Image("graphics/Heart.png");
        showWaveGraphic = new Image("graphics/WaveGraphic.png");
        waveInActive = new Image("graphics/nextWaveNonActive.png");

        sellButtonGraphic = new Image("graphics/SellButtonGraphic.png");
        nextWaveGraphic = new Image("graphics/nextWaveActive.png");
        showMoneyGraphic = new Image("graphics/CurrencyGraphic.png");

        // Load ảnh các level của tháp
        towerLevel = new Image[4];

        for (int i = 1; i <= 3; i++) {
            String path = "graphics/Towers/TowerLevel/Level" + i + ".png";
            towerLevel[i] = new Image(path);
        }

        fastForwardGraphic = new Image("graphics/FastForwardButton.png");

        sellTowerImage = new Image("graphics/SellSelectGraphic.png");
        upgradeTowerImage = new Image("graphics/UpgradeSelectGraphic.png");
        gameOverImage = new Image("graphics/GameOver.jpg");


        roadTile = new Image("graphics/MapTile/sand_tile.png"); // Ảnh đường đi
        towerTile = new Image("graphics/MapTile/grass_tile.png"); // Ảnh tháp


        normalTowerGraphic = new Image("graphics/Towers/normalTower48.png");
        machineGunTowerGraphic = new Image("graphics/Towers/machineGunTower48.png");
        sniperTowerGraphic = new Image("graphics/Towers/sniperTower48.png");
        slowTowerGraphic = new Image("graphics/Towers/SlowTower48.png");


        towerBase = new Image("graphics/Towers/towerBase48.png");


        normalTowerProjectile = new Image("graphics/Projectiles/normalTowerProjectile.png");
        machineGunTowerProjectile = new Image("graphics/Projectiles/machineGunTowerProjectile.png");
        sniperTowerProjectile = new Image("graphics/Projectiles/sniperTowerProjectile.png");
        slowTowerProjectile = new Image("graphics/Projectiles/slowTowerProjectile.png");
    }


    public void isGameOver() {
        if (Player.getCurrentLife() == 0) {
            gameOver = true;
        }
    }

    public void isWaveFinished() {
        if (Player.getCurrentLife() > 0 && EnemyWave.enemyQueue.size() == 0 && EnemyWave.activeEnemyList.size() == 0) {
            projectiles.clear();
            wave.setRespawn();
            currentLevel++;
            waveInProgress = false;
        }
    }

    public void loadMenuInformation() {
        showLives = "Current lives: " + Player.getCurrentLife();
        showMoney = String.valueOf(Player.getCurrentMoney());
        showWave = String.valueOf(currentLevel);

        normalTowerCost = String.valueOf(NormalTower.cost);
        machineGunTowerCost = String.valueOf(MachineGunTower.cost);
        sniperTowerCost = String.valueOf(SniperTower.cost);
        slowTowerCost = String.valueOf(SlowTower.cost);

    }

    public void drawMenu(Graphics graphics) {
        int size = 60;
        int boxSize = 80;

        backGround.draw(720, 0, 240, 720);
        towerBox.draw(740, 40, boxSize, boxSize);
        towerBox.draw(840, 40, boxSize, boxSize);
        towerBox.draw(740, 140, boxSize, boxSize);
        towerBox.draw(840, 140, boxSize, boxSize);

        normalTowerButton.draw(750, 50, size, size);
        graphics.drawString(normalTowerCost, 765, 50 + size + 2);

        machineGunTowerButton.draw(850, 50, size, size);
        graphics.drawString(machineGunTowerCost, 865, 50 + size + 2);

        sniperTowerButton.draw(750, 150, size, size);
        graphics.drawString(sniperTowerCost, 765, 150 + size + 2);

        slowTowerButton.draw(850, 150, size, size);
        graphics.drawString(slowTowerCost, 865, 150 + size + 2);


        showWaveGraphic.draw(760, 250);

        showLife.draw(730, 500, 30, 30);
        showMoneyGraphic.draw(725, 540, 45, 45);

        if (waveInProgress) {

            waveInActive.draw(760, 330);
        } else {

            nextWaveGraphic.draw(730 + 30, 350 - 20);
        }

        sellButtonGraphic.draw(745, 395);
        upgradeButtonGraphic.draw(850, 395, size, size);

        fastForwardGraphic.draw(840 - fastForwardGraphic.getWidth() / 2, 624);
    }

    public void createMenuButtons() {

        buyNormalTowerButton = new Rectangle(750, 50, normalTowerButton.getWidth(), normalTowerButton.getHeight());
        buyMachineGunButton = new Rectangle(850, 50, machineGunTowerButton.getWidth(), machineGunTowerButton.getHeight());
        buySniperTowerButton = new Rectangle(750, 150, sniperTowerGraphic.getWidth(), sniperTowerGraphic.getHeight());
        buySlowTowerButton = new Rectangle(850, 150, slowTowerGraphic.getWidth(), slowTowerGraphic.getHeight());

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

            Image towerImage;
            Image level;

            switch (tower.getType()) {

                case NORMAL_TOWER:
                    towerImage = normalTowerGraphic;
                    break;

                case MACHINE_GUN_TOWER:
                    towerImage = machineGunTowerGraphic;
                    break;

                case SLOW_TOWER:
                    towerImage = slowTowerGraphic;
                    break;

                default:
                    towerImage = sniperTowerGraphic;
                    break;
            }

            towerBase.draw(tower.getXPos(), tower.getYPos());

            towerImage.setRotation(tower.getAngleOfRotationInDegrees());
            towerImage.drawCentered(tower.getXPos() + normalTowerGraphic.getWidth() / 2, tower.getYPos() + normalTowerGraphic.getHeight() / 2);

            switch (tower.getTowerLevel()) {
                case 1:
                    level = towerLevel[1];
                    break;

                case 2:
                    level = towerLevel[2];
                    break;

                default:
                    level = towerLevel[3];
                    break;
            }

            level.drawCentered(tower.getXPos() + normalTowerGraphic.getWidth() / 2, tower.getYPos() + normalTowerGraphic.getHeight() / 2);

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

                    case SLOW_TOWER_PROJECTILE:
                        projectile = slowTowerProjectile;
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

            if (waveInProgress) {
                tower.setCantRefund();
            }

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

        if (selectMenuOption == -1) {

            if (buyNormalTowerButton.contains(Mouse.getX(), gameContainer.getHeight() - Mouse.getY()))
                selectMenuOption = 1;

            else if (buyMachineGunButton.contains(Mouse.getX(), gameContainer.getHeight() - Mouse.getY()))
                selectMenuOption = 2;

            else if (buySniperTowerButton.contains(Mouse.getX(), gameContainer.getHeight() - Mouse.getY()))
                selectMenuOption = 3;

            else if (buySlowTowerButton.contains(Mouse.getX(), gameContainer.getHeight() - Mouse.getY()))
                selectMenuOption = 4;

            else if (upgradeButton.contains(Mouse.getX(), gameContainer.getHeight() - Mouse.getY()))
                selectMenuOption = 5;

            else if (sellButton.contains(Mouse.getX(), gameContainer.getHeight() - Mouse.getY()))
                selectMenuOption = 6;

            else if (fastForwardButton.contains(Mouse.getX(), gameContainer.getHeight() - Mouse.getY()))

                fastForward = ++countFastForwardClickedTime % 2 == 1;

            else if (nextWave.contains(Mouse.getX(), gameContainer.getHeight() - Mouse.getY()))

                waveInProgress = true;

            return;
        }

        if (selectMenuOption > 0 && selectMenuOption < 5) {

            if (buildableTile(x, y) && isBuildable(x, y)) {

                switch (selectMenuOption) {

                    case 1:
                        if (Player.getCurrentMoney() >= NormalTower.cost) {

                            Tower newNormalTower = new NormalTower(x, y);
                            towersOnMap.add(newNormalTower);
                            Player.addMoney(-NormalTower.cost);
                            break;

                        } else
                            break;

                    case 2:
                        if (Player.getCurrentMoney() >= MachineGunTower.cost) {

                            Tower newMachineTower = new MachineGunTower(x, y);
                            towersOnMap.add(newMachineTower);
                            Player.addMoney(-MachineGunTower.cost);
                            break;

                        } else break;

                    case 3:
                        if (Player.getCurrentMoney() >= SniperTower.cost) {

                            Tower newSniperTower = new SniperTower(x, y);
                            towersOnMap.add(newSniperTower);
                            Player.addMoney(-SniperTower.cost);
                            break;

                        } else break;

                    case 4:
                        if (Player.getCurrentMoney() >= SlowTower.cost) {

                            Tower newSlowTower = new SlowTower(x, y);
                            towersOnMap.add(newSlowTower);
                            Player.addMoney(-SlowTower.cost);
                            break;

                        } else break;

                }

            }

        } else if (selectMenuOption == 5) {

            for (int i = 0; i < towersOnMap.size(); i++) {

                if (towersOnMap.get(i).getxLoc() == x && towersOnMap.get(i).getyLoc() == y) {

                    if (Player.getCurrentMoney() >= towersOnMap.get(i).getCost() / 2) {

                        if (towersOnMap.get(i).getTowerLevel() < 3) {
                            Player.addMoney(-towersOnMap.get(i).getCost() / 2);
                            towersOnMap.get(i).upgradeTower();
                        }

                    } else break;

                }

            }

        } else if (selectMenuOption == 6) {

            for (int i = 0; i < towersOnMap.size(); i++) {

                if (towersOnMap.get(i).getxLoc() == x && towersOnMap.get(i).getyLoc() == y) {

                    if (waveInProgress) {

                        Player.addMoney(towersOnMap.get(i).getCost() * 7 / 10);

                    } else {

                        if (towersOnMap.get(i).isRefundable()) {

                            Player.addMoney(towersOnMap.get(i).getCost());

                        } else {

                            Player.addMoney(towersOnMap.get(i).getCost() * 7 / 10);

                        }

                    }

                    towersOnMap.remove(i);

                }

            }

        }

        selectMenuOption = -1;

    }


    public void drawMenuButtonOption(GameContainer gameContainer) {

        g.setColor(Color.yellow);

        int mouseXPos = Mouse.getX();
        int mouseYPos = gameContainer.getHeight() - Mouse.getY();

        switch (selectMenuOption) {

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
                g.drawOval(mouseXPos - (float) SlowTower.fireRange, mouseYPos - (float) SlowTower.fireRange, (float) SlowTower.fireRange * 2, (float) SlowTower.fireRange * 2);
                slowTowerButton.drawCentered(mouseXPos, mouseYPos);

                break;

            case 5:
                upgradeTowerImage.drawCentered(mouseXPos, mouseYPos);
                break;

            case 6:
                sellTowerImage.drawCentered(mouseXPos, mouseYPos);
                break;

        }

    }

    // Hàm vẽ sân chơi theo mảng 2 chiều đại diện cho map:
    public void drawMap() {

        for (int y = 0; y < PlayMap.getHeightOfMap(); y++) {

            for (int x = 0; x < PlayMap.getWidthOfMap(); x++) {

                if (PlayMap.mapTile[y][x] instanceof RoadTile) {

                    roadTile.draw(x * roadTile.getWidth(), y * roadTile.getHeight());

                } else {

                    towerTile.draw(x * roadTile.getWidth(), y * roadTile.getHeight());

                }

            }

        }

    }


    public static double getDelta() {

        return delta;

    }

}