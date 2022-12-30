package pepse.util.pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.util.Vector2;
import pepse.util.pepse.world.Sky;
import pepse.util.pepse.world.Terrain;
import pepse.util.pepse.world.daynight.Night;
import pepse.util.pepse.world.daynight.Sun;
import pepse.util.pepse.world.daynight.SunHalo;

import java.awt.*;

public class PepseGameManager extends GameManager {
    private static final float DAY_CYCLE_LENGTH = 30f;
    private static final Color SUN_HALO_COLOR = new Color(255, 255, 0, 20);


    /**
     * override initializeGame method in gamemanger
     * @param imageReader Contains a single method: readImage, which reads an image from disk.
     *                 See its documentation for help.
     * @param soundReader Contains a single method: readSound, which reads a wav file from
     *                    disk. See its documentation for help.
     * @param inputListener Contains a single method: isKeyPressed, which returns whether
     *                      a given key is currently pressed by the user or not. See its
     *                      documentation.
     * @param windowController Contains an array of helpful, self explanatory methods
     *                         concerning the window.
     */
    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader,
                               UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        //create sky
        Sky sky = new Sky();
        Vector2 windowSize = windowController.getWindowDimensions();
        sky.create(this.gameObjects(), windowSize, Layer.BACKGROUND);
        //create ground blocks
        Terrain T = new Terrain(this.gameObjects(), Layer.DEFAULT, windowSize, 0);
        T.createInRange(0, (int) windowSize.x());
        //create night and sun
        Night.create(gameObjects(), Layer.FOREGROUND, windowController.getWindowDimensions(),
                DAY_CYCLE_LENGTH);
        GameObject sun = Sun.create(gameObjects(), Layer.BACKGROUND + 1, windowController.getWindowDimensions(),
                DAY_CYCLE_LENGTH);
        SunHalo.create(gameObjects(), Layer.BACKGROUND + 10, sun, SUN_HALO_COLOR);
    }

    /**
     * main function
     * @param args
     */
    public static void main(String[] args) {
        new PepseGameManager().run();
    }
}
