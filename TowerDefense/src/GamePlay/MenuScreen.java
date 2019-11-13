package GamePlay;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.*;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;


public class MenuScreen extends BasicGameState {
    private Image backGround;
    private Image logo;
    private Image startButtonGraphic;
    private Image exitButtonGraphic;

    private Rectangle startButton;
    private Rectangle exitButton;

    public MenuScreen(int state) {
    }

    @Override
    public int getID() {
        return 0;
    }


    @Override
    public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
        Music rickAndMorty = new Music("sound_effect/RickandMorty.wav");

        loadImage();
        createButton(gameContainer);

//        rickAndMorty.play();
    }

    @Override
    public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics graphics) throws SlickException {
        drawImage(gameContainer);
    }

    @Override
    public void update(GameContainer gameContainer, StateBasedGame stateBasedGame, int i) throws SlickException {
        if (Mouse.isButtonDown(0)) {
            if (startButton.contains(Mouse.getX(), gameContainer.getHeight() - Mouse.getY())) {
                stateBasedGame.enterState(1);
            }
            if (exitButton.contains(Mouse.getX(), gameContainer.getHeight() - Mouse.getY())) {
                System.exit(0);
            }
        }
    }

    public void loadImage() throws SlickException {
        logo = new Image("graphics/TD_logo.png");
        startButtonGraphic = new Image("graphics/StartGameButton.png");
        exitButtonGraphic = new Image("graphics/ExitGameButton.png");
        backGround = new Image("graphics/background.jpg");
    }

    public void drawImage(GameContainer gc) {
        backGround.draw(0, 0);
        logo.draw(gc.getWidth() / 2 - logo.getWidth() / 2, 0);
        startButtonGraphic.draw((gc.getWidth() - startButtonGraphic.getWidth()) / 2, gc.getHeight() - logo.getHeight());
        exitButtonGraphic.draw((gc.getWidth() - exitButtonGraphic.getWidth()) / 2, logo.getHeight() + startButtonGraphic.getHeight() + 70);
    }

    public void createButton(GameContainer gc) {
        startButton = new Rectangle((gc.getWidth() - startButtonGraphic.getWidth()) / 2, gc.getHeight() - logo.getHeight(), startButtonGraphic.getWidth(), startButtonGraphic.getHeight());
        exitButton = new Rectangle((gc.getWidth() - exitButtonGraphic.getWidth()) / 2, logo.getHeight() + startButtonGraphic.getHeight() + 70, exitButtonGraphic.getWidth(), exitButtonGraphic.getHeight());
    }

}