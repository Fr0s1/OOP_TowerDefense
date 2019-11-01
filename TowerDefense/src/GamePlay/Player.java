package GamePlay;

public class Player {
    private static int startingLive = 15;
    private static int startingMoney = 200;

    private int currentLife;
    private int currentMoney;

    public Player() {
        this.currentLife = startingLive;
        this.currentMoney = startingMoney;
    }

    public void addMoney(int amount) {
        this.currentMoney += amount;
    }

    public void decreaseLife() {
        this.currentLife -= 1;
    }
}
