package GamePlay;

public class Player {
    private static final int startingLive = 15;
    private static final int startingMoney = 200;

    private static int currentLife;
    private static int currentMoney;

    Player() {
        currentLife = startingLive;
        currentMoney = startingMoney;
    }

    public static void addMoney(int amount) {
        currentMoney += amount;
    }

    public static void decreaseLife() {
        currentLife -= 1;
    }

    static int getCurrentLife() {
        return currentLife;
    }

    static int getCurrentMoney() {
        return currentMoney;
    }

}