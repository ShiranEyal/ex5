package pepse.util.pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
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
    private static final int INITIAL_AVATAR_X_POS = 25 * Block.SIZE;
    private static final float CREATE_AVATAR_DELAY = 1f;
    private static final int WORLD_CREATION_BUFFER_SIZE = 4 * Block.SIZE;
    private static final int CREATE_AVATAR_Y_OFFSET = 5;

    private Terrain terrain;
    private Avatar avatar;
    private Tree tree;

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
        seed = new Random().nextInt();
//        seed = 0;

        Vector2 windowSize = windowController.getWindowDimensions();
        //create sky
        Sky.create(this.gameObjects(), windowSize, SKY_LAYER);
        //create ground blocks
        terrain = new Terrain(this.gameObjects(), Layer.DEFAULT, windowSize, seed);
        terrain.createInRange(0, (int) windowSize.x());
        //create night and sun
        initializeDayNightCycle();
        //create trees
        initializeTree();
        // create avatar
        initializeAvatar();
    }

    private void initializeDayNightCycle() {
        Night.create(gameObjects(), NIGHT_LAYER,
                windowController.getWindowDimensions(), DAY_CYCLE_LENGTH);
        GameObject sun = Sun.create(gameObjects(),
                SUN_LAYER, windowController.getWindowDimensions(),
                DAY_CYCLE_LENGTH);
        SunHalo.create(gameObjects(), SUN_HALO_LAYER, sun, SUN_HALO_COLOR);
    }

    private void initializeTree() {
        tree = new Tree(gameObjects(), TREE_TRUNK_LAYER, terrain, seed);
        tree.createInRange(0, (int) windowController.getWindowDimensions().x());

        // enable collisions between leaves and first two ground layers
        gameObjects().layers().shouldLayersCollide(LEAVES_LAYER, TERRAIN_LAYER, true);
        gameObjects().layers().shouldLayersCollide(LEAVES_LAYER, TERRAIN_LAYER - 1, true);
    }
    private void initializeAvatar() {
        Vector2 initialPos = new Vector2(INITIAL_AVATAR_X_POS,
                terrain.groundHeightAt(INITIAL_AVATAR_X_POS) - CREATE_AVATAR_Y_OFFSET * Block.SIZE);
        avatar = Avatar.create(gameObjects(), AVATAR_LAYER,
                initialPos, inputListener, imageReader);

        // according to the given signature of Avatar.create function we can't pass the
        // SoundReader and this is the reason to the wierd "activateJumpingSound" function.
        avatar.activateJumpingSound(soundReader);

        // enable collisions between trees and avatar
        gameObjects().layers().shouldLayersCollide(TREE_TRUNK_LAYER, AVATAR_LAYER, true);
        gameObjects().layers().shouldLayersCollide(LEAVES_LAYER, AVATAR_LAYER, true);

        // enable collisions between avatar and first two ground layers
        gameObjects().layers().shouldLayersCollide(AVATAR_LAYER, TERRAIN_LAYER, true);
        gameObjects().layers().shouldLayersCollide(AVATAR_LAYER, TERRAIN_LAYER - 1, true);

        // focus camera on the avatar
        setCamera(new Camera(avatar, Vector2.ZERO, windowController.getWindowDimensions(),
                windowController.getWindowDimensions()));
    }

    /**
     * Override PepseGameManager update function for infinite render
     * @param deltaTime The time, in seconds, that passed since the last invocation
     *                  of this method (i.e., since the last frame). This is useful
     *                  for either accumulating the total time that passed since some
     *                  event, or for physics integration (i.e., multiply this by
     *                  the acceleration to get an estimate of the added velocity or
     *                  by the velocity to get an estimate of the difference in position).
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        //check if need to update blocks on screen
        float avatarX = avatar.getCenter().x();
        int xPosInBlocks = avatar.getXPosInBlocks();
        int minBlocksPos = (int) (avatarX - windowController.getWindowDimensions().x()/2);
        int maxBlocksPos = (int) (avatarX + windowController.getWindowDimensions().x()/2);
        if (xPosInBlocks < (int) avatarX/Block.SIZE) {
            terrain.removeXBlocks(minBlocksPos);
            terrain.createInRange(maxBlocksPos, maxBlocksPos + Block.SIZE);
            avatar.setXPosInBlocks(xPosInBlocks + 1);
//            updateTerrain(minBlocksPos, maxBlocksPos, xPosInBlocks);
        }
        if (xPosInBlocks > (int) avatarX/Block.SIZE) {
            terrain.removeXBlocks(maxBlocksPos);
            terrain.createInRange(minBlocksPos - Block.SIZE, minBlocksPos);
            avatar.setXPosInBlocks(xPosInBlocks - 1);
//            updateTerrain(maxBlocksPos, minBlocksPos - Block.SIZE, xPosInBlocks);
        }
    }

    //helper function to remove ground blocks from terrain
    private void updateTerrain(int posToRemove, int posToAdd, int xPosInBlocks) {
        terrain.removeXBlocks(posToRemove);
        terrain.createInRange(posToAdd, posToAdd + Block.SIZE);
        avatar.setXPosInBlocks(xPosInBlocks - 1);
    }

    /**
     * main function
     * @param args
     */
    public static void main(String[] args) {
        new PepseGameManager().run();
    }
}
