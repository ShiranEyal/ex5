package pepse.util.pepse;

import danogl.GameManager;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import pepse.util.pepse.world.daynight.Night;
import pepse.util.pepse.world.daynight.Sun;

public class PepseGameManager extends GameManager {
    private static final float DAY_CYCLE_LENGTH = 30f;

    //override initializeGame method in gamemanger
    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader,
                               UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        Night.create(gameObjects(), Layer.FOREGROUND, windowController.getWindowDimensions(),
                DAY_CYCLE_LENGTH);
        Sun.create(gameObjects(), Layer.BACKGROUND + 1, windowController.getWindowDimensions(),
                DAY_CYCLE_LENGTH);
    }
    //main function
    public static void main(String[] args) {
        new PepseGameManager().run();
    }
}
