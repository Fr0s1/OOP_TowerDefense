package GamePlay;

public class Player {
    private static final int startingLive = 15;
    private static final int startingMoney = 200;

    private static int currentLife;
    private static int currentMoney;

    public Player() {
        this.currentLife = startingLive;
        this.currentMoney = startingMoney;
    }

    public static void addMoney(int amount) {
        currentMoney += amount;
    }

    public static void spendMoney(int amount){
        currentMoney -= amount;
    }

    public static void decreaseLife() {
        currentLife -= 1;
    }

    public static int getCurrentLife() {
        return currentLife;
    }

    public static int getCurrentMoney() {
        return currentMoney;
    }
}