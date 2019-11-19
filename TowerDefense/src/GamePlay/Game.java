package GamePlay;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class Game extends StateBasedGame {

    private static final int menuScreen = 0;
    private static final int playScreen = 1;

    private Game(String name) {
        super(name);
        this.addState(new MenuScreen(menuScreen)); // Add menu screen
        this.addState(new PlayScreen(playScreen)); // Add play screen
    }

    @Override
    public void initStatesList(GameContainer gameContainer) throws SlickException {
        // Start at menu
        this.enterState(menuScreen);
    }

    public static void main(String[] args) throws SlickException {
        AppGameContainer app = new AppGameContainer(new Game("Tower Defense"));
        //set resolution to map size
        app.setDisplayMode(960, 720, false);
        app.setTargetFrameRate(30);
        app.setShowFPS(false);
        app.start();
    }
}
