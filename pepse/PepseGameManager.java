package pepse.util.pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.pepse.world.Avatar;
import pepse.util.pepse.world.Block;
import pepse.util.pepse.world.Sky;
import pepse.util.pepse.world.Terrain;
import pepse.util.pepse.world.daynight.Night;
import pepse.util.pepse.world.daynight.Sun;
import pepse.util.pepse.world.daynight.SunHalo;
import pepse.util.pepse.world.trees.Tree;

import java.awt.*;

import java.awt.*;
import java.util.Random;

public class PepseGameManager extends GameManager {

    private static final float DAY_CYCLE_LENGTH = 60f;
    private static final Color SUN_HALO_COLOR = new Color(255, 255, 0, 20);

    //avatar size
    private static final int AVATAR_SIZE= 30;
    //Y_ACC
    private static final int Y_ACC = 500;
    //top left corner
    public static final Vector2 TOP_LEFT_CORNER = new Vector2(0, 0);

    private static WindowController windowController;
    private Avatar avatar;
    private static Terrain terrain;

    ////// Layers Constants //////
    public static final int SKY_LAYER = Layer.BACKGROUND;
    public static final int TERRAIN_LAYER = Layer.DEFAULT;
    public static final int NIGHT_LAYER = Layer.FOREGROUND;
    private static final int SUN_LAYER = Layer.BACKGROUND + 1;
    private static final int SUN_HALO_LAYER = Layer.BACKGROUND + 10;
    private static final int TREE_LAYER = Layer.DEFAULT + 10;

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
        this.windowController = windowController;
        Vector2 windowSize = windowController.getWindowDimensions();

        //create sky
        Sky.create(this.gameObjects(), windowSize, SKY_LAYER);

        //create ground blocks
        Random rand  = new Random();
        Terrain T = new Terrain(this.gameObjects(), Layer.DEFAULT, windowSize, rand.nextInt());
        T.createInRange(0, (int) windowSize.x());
        this.terrain = T;


        //create night and sun
        Night.create(gameObjects(), NIGHT_LAYER, windowSize, DAY_CYCLE_LENGTH);
        GameObject sun = Sun.create(gameObjects(), SUN_LAYER, windowSize,
                DAY_CYCLE_LENGTH);
        SunHalo.create(gameObjects(), SUN_HALO_LAYER, sun, SUN_HALO_COLOR);

        //create trees
        Tree tree = new Tree(gameObjects(), TREE_LAYER, windowSize, terrain, 0);
        tree.createInRange(0, (int) windowSize.x());
        gameObjects().layers().shouldLayersCollide(TREE_LAYER + 1, TERRAIN_LAYER, true);

        //create avatar
        this.avatar = create(gameObjects(), Layer.DEFAULT, TOP_LEFT_CORNER,
                inputListener, imageReader);
    }

    /**
     * create avatar character
     * @param gameObjects
     * @param layer
     * @param topLeftCorner
     * @param inputListener
     * @param imageReader
     * @return
     */
    public static Avatar create(GameObjectCollection gameObjects,
                                int layer, Vector2 topLeftCorner,
                                UserInputListener inputListener,
                                ImageReader imageReader) {
        Renderable avatarImg = new RectangleRenderable(Color.BLUE);
        Avatar avatar = new Avatar(topLeftCorner, new Vector2(AVATAR_SIZE, AVATAR_SIZE),
                avatarImg, inputListener);
        int x_pos = (int) windowController.getWindowDimensions().x() / 2;
        int y_pos = (int) windowController.getWindowDimensions().y() / 2;
        avatar.setCenter(new Vector2(x_pos, y_pos));
        avatar.transform().setAcceleration(Vector2.DOWN.multY(Y_ACC));
        avatar.physics().preventIntersectionsFromDirection(Vector2.ZERO);
        gameObjects.addGameObject(avatar, layer);
        return avatar;
    }

    /**
     * main function
     * @param args
     */
    public static void main(String[] args) {
        new PepseGameManager().run();
    }
}
