package pepse.util.pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.ScheduledTask;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
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
import java.util.Random;

public class PepseGameManager extends GameManager {

    ////// Layers Constants //////
    private static final int SKY_LAYER = Layer.BACKGROUND;
    private static final int TERRAIN_LAYER = Layer.DEFAULT;
    private static final int NIGHT_LAYER = Layer.FOREGROUND;
    private static final int SUN_LAYER = Layer.BACKGROUND + 1;
    private static final int SUN_HALO_LAYER = Layer.BACKGROUND + 10;
    private static final int TREE_TRUNK_LAYER = Layer.DEFAULT + 10;
    private static final int LEAVES_LAYER = TREE_TRUNK_LAYER + 1;
    private static final int AVATAR_LAYER = Layer.DEFAULT + 20;



    private static final float DAY_CYCLE_LENGTH = 60f;
    private static final Color SUN_HALO_COLOR = new Color(255, 255, 0, 20);
    private static final Vector2 INITIAL_AVATAR_POS = new Vector2(25 * Block.SIZE, -50);
    private static final float CREATE_AVATAR_DELAY = 1f;

    private Terrain terrain;
    private Avatar avatar;

    private int seed;


    private WindowController windowController;
    private UserInputListener inputListener;
    private ImageReader imageReader;
    private SoundReader soundReader;




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
        windowController.setTargetFramerate(80);
        this.windowController = windowController;
        this.inputListener = inputListener;
        this.imageReader = imageReader;
        this.soundReader = soundReader;

        Vector2 windowSize = windowController.getWindowDimensions();

//        seed = new Random().nextInt();
        seed = 2;

        //create sky
        Sky.create(this.gameObjects(), windowSize, SKY_LAYER);

        //create ground blocks
        Terrain T = new Terrain(this.gameObjects(), Layer.DEFAULT, windowSize, seed);
        T.createInRange(0, (int) windowSize.x());
        terrain = T;

        //create night and sun
        Night.create(gameObjects(), NIGHT_LAYER, windowSize, DAY_CYCLE_LENGTH);
        GameObject sun = Sun.create(gameObjects(), SUN_LAYER, windowSize,
                DAY_CYCLE_LENGTH);
        SunHalo.create(gameObjects(), SUN_HALO_LAYER, sun, SUN_HALO_COLOR);

        //create trees
        Tree tree = new Tree(gameObjects(), TREE_TRUNK_LAYER, terrain, seed);
        tree.createInRange(0, (int) windowSize.x());

        // enable collisions between leaves and first two ground layers
        gameObjects().layers().shouldLayersCollide(LEAVES_LAYER, TERRAIN_LAYER, true);
        gameObjects().layers().shouldLayersCollide(LEAVES_LAYER, TERRAIN_LAYER - 1, true);

        // create avatar
        // using ScheduledTask to delay it so the engine will have time to compute the
        // collisions with the ground.
        new ScheduledTask(sun, CREATE_AVATAR_DELAY, false, this::initAvatar);
    }

    private void initAvatar() {
        avatar = Avatar.create(gameObjects(), AVATAR_LAYER,
                INITIAL_AVATAR_POS, inputListener, imageReader);

        // according to the given signature of Avatar.create function we can't pass the
        // SoundReader and this is the reason to the wierd "activateJumpingSound" function.
        avatar.activateJumpingSound(soundReader);

        // enable collisions between trees and avatar
        gameObjects().layers().shouldLayersCollide(TREE_TRUNK_LAYER, AVATAR_LAYER, true);
        gameObjects().layers().shouldLayersCollide(LEAVES_LAYER, AVATAR_LAYER, true);
        // enable collisions between avatar and first two ground layers
        gameObjects().layers().shouldLayersCollide(AVATAR_LAYER, TERRAIN_LAYER, true);
        gameObjects().layers().shouldLayersCollide(AVATAR_LAYER, TERRAIN_LAYER - 1, true);
    }


    /**
     * main function
     * @param args
     */
    public static void main(String[] args) {
        new PepseGameManager().run();
    }
}
