package pepse.util.pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.gui.*;
import danogl.gui.rendering.Camera;
import danogl.util.Vector2;
import pepse.util.pepse.world.*;
import pepse.util.pepse.world.daynight.Night;
import pepse.util.pepse.world.daynight.Sun;
import pepse.util.pepse.world.daynight.SunHalo;
import pepse.util.pepse.world.trees.Tree;

import java.awt.*;
import java.util.Random;

public class PepseGameManager extends GameManager {

    ////// Frame Target //////
    private static final int FRAME_TARGET = 80;

    ////// Music path //////
    private static final String MAIN_THEME = "assets/main_theme.wav";

    ////// Layers Constants //////
    private static final int SKY_LAYER = Layer.BACKGROUND;
    private static final int TERRAIN_LAYER = Layer.DEFAULT;
    private static final int NIGHT_LAYER = Layer.FOREGROUND;
    private static final int SUN_LAYER = Layer.BACKGROUND + 1;
    private static final int SUN_HALO_LAYER = Layer.BACKGROUND + 10;
    private static final int TREE_TRUNK_LAYER = Layer.DEFAULT + 10;
    private static final int LEAVES_LAYER = TREE_TRUNK_LAYER + 1;
    private static final int AVATAR_LAYER = Layer.DEFAULT + 20;


    ////// Day Night Cycle initialization constants  //////
    private static final float DAY_CYCLE_LENGTH = 60f;
    private static final Color SUN_HALO_COLOR = new Color(255, 255, 0, 20);
    private static final float CREATE_AVATAR_DELAY = 5f;
    private static final int WORLD_CREATION_BUFFER_SIZE = 4 * Block.SIZE;

    ////// Avatar initialization constants //////
    private static final int INITIAL_AVATAR_X_POS = 25 * Block.SIZE;
    private static final int CREATE_AVATAR_Y_OFFSET = 5;

    ////// UI objects //////
    private static final Vector2 TIMER_DIMENSIONS = Vector2.of(70, 30);
    private static final int GAME_LENGTH_IN_SECONDS = 180;


    ////// initializeGame parameters //////
    private WindowController windowController;
    private UserInputListener inputListener;
    private ImageReader imageReader;
    private SoundReader soundReader;


    ////// world generation fields //////
    private static final int CHUNK_SIZE = 20 * Block.SIZE;
    private int halfWindowWidth;
    private int seed;
    private int lowestRenderedX;
    private int highestRenderedX;


    ////// game objects generators //////
    private Terrain terrain;
    private Avatar avatar;
    private Tree tree;
    private Sound theme;


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
                               UserInputListener inputListener,
                               WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        windowController.setTargetFramerate(FRAME_TARGET);
        Vector2 windowDimensions = windowController.getWindowDimensions();

        // initialize timer
        // TODO add runnable
        Timer timer = new Timer(Vector2.ZERO, TIMER_DIMENSIONS,
                GAME_LENGTH_IN_SECONDS, () -> windowController.openYesNoDialog(""));
        timer.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects().addGameObject(timer, Layer.UI);

        // save the parameters of this method into field
        saveInitializeGameParameters(imageReader, soundReader, inputListener, windowController);

        // initialize general fields that deal with the world generation
        initializeWorldGenerationFields(windowDimensions);

        //create sky
        Sky.create(this.gameObjects(), windowDimensions, SKY_LAYER);
        //create ground blocks
        terrain = new Terrain(this.gameObjects(), Layer.DEFAULT, windowDimensions, seed);
        terrain.createInRange(lowestRenderedX, highestRenderedX);
        //create night and sun
        initializeDayNightCycle();
        //create trees
        initializeTree();
        // create avatar
        initializeAvatar();
        //init music
        initializeMusic(soundReader);
    }

    private void initializeMusic(SoundReader soundReader) {
        theme = soundReader.readSound(MAIN_THEME);
        theme.playLooped();
    }

    private void saveInitializeGameParameters(ImageReader imageReader, SoundReader soundReader,
                                              UserInputListener inputListener,
                                              WindowController windowController) {
        this.windowController = windowController;
        this.inputListener = inputListener;
        this.imageReader = imageReader;
        this.soundReader = soundReader;
    }

    private void initializeWorldGenerationFields(Vector2 windowDimensions) {
        seed = new Random().nextInt();
        lowestRenderedX = -CHUNK_SIZE;
        highestRenderedX = (int) windowDimensions.x() + CHUNK_SIZE;
        halfWindowWidth = (int) windowDimensions.x() / 2;
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
        tree.createInRange(lowestRenderedX, highestRenderedX);

        // makes sure that the tree layers isn't empty (otherwise we can't enable collisions
        // with other layers)
        GameObject nonCollidingObject = new GameObject(Vector2.ZERO, Vector2.ZERO, null) {
                    @Override
                    public boolean shouldCollideWith(GameObject other) { return false; }
                    };
        gameObjects().addGameObject(nonCollidingObject, LEAVES_LAYER);
        gameObjects().addGameObject(nonCollidingObject, TREE_TRUNK_LAYER);

        // enable collisions between leaves and first two ground layers
        gameObjects().layers().shouldLayersCollide(LEAVES_LAYER, TERRAIN_LAYER, true);
        gameObjects().layers().shouldLayersCollide(LEAVES_LAYER, TERRAIN_LAYER - 1, true);
    }
    private void initializeAvatar() {
        int initialX = INITIAL_AVATAR_X_POS;
        if (tree.treeInX(initialX)) {
            initialX++;
        }
        Vector2 initialPos = new Vector2(initialX,
                terrain.groundHeightAt(initialX) - CREATE_AVATAR_Y_OFFSET * Block.SIZE);
        avatar = Avatar.create(gameObjects(), AVATAR_LAYER,
                initialPos, inputListener, imageReader);

        // according to the given signature of Avatar.create function we can't pass the
        // SoundReader and this is the reason to the wierd "setSoundReaderAndSounds" function.
        avatar.setSoundReaderAndSounds(soundReader);

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

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        int avatarX = (int) avatar.getTopLeftCorner().x();
        updateWorldGeneration(avatarX);
    }

    private void updateWorldGeneration(int avatarX) {
        if (avatarX - lowestRenderedX < halfWindowWidth) {
            terrain.createInRange(lowestRenderedX - CHUNK_SIZE, lowestRenderedX);
            tree.createInRange(lowestRenderedX - CHUNK_SIZE, lowestRenderedX);
            terrain.removeBlocksInRange(highestRenderedX - CHUNK_SIZE, highestRenderedX);
            tree.removeTreeInRange(highestRenderedX - CHUNK_SIZE, highestRenderedX);

            highestRenderedX -= CHUNK_SIZE;
            lowestRenderedX -= CHUNK_SIZE;
        }
        if (highestRenderedX - avatarX < halfWindowWidth) {
            terrain.createInRange(highestRenderedX, highestRenderedX + CHUNK_SIZE);
            tree.createInRange(highestRenderedX, highestRenderedX + CHUNK_SIZE);
            terrain.removeBlocksInRange(lowestRenderedX, lowestRenderedX + CHUNK_SIZE);
            tree.removeTreeInRange(lowestRenderedX, lowestRenderedX + CHUNK_SIZE);

            highestRenderedX += CHUNK_SIZE;
            lowestRenderedX += CHUNK_SIZE;
        }
    }

    /**
     * main function
     * @param args
     */
    public static void main(String[] args) {
        new PepseGameManager().run();
    }
}
