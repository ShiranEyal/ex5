package pepse.util.pepse.world.chickens;

import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.ImageReader;
import danogl.gui.Sound;
import danogl.gui.SoundReader;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;
import pepse.util.pepse.world.Block;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Chickens class in charge of handling all instances of chickens
 */
public class Chickens {
    private static final float CHICKENS_HEIGHT = -1f/10f;
    private static final int CHICKENS_DISTANCE = 20 * Block.SIZE;
    private static final Vector2 CHICKEN_SIZE = new Vector2(3 * Block.SIZE, 3 * Block.SIZE);
    private static final String CHICKEN_NOISE_1 = "assets/chicken_noise_1.wav";
    private static final String CHICKEN_NOISE_2 = "assets/chicken_noise_2.wav";
    private static final String CHICKEN_IMAGE = "assets/red_chicken_noBG.png";
    private static final int CHICKEN_LAYER = Layer.DEFAULT + 30;
    private static final String CHICKEN_TAG = "chicken";
    private static final int CHICKEN_SPEED = 5;
    private static final int SWITCH_CHICKEN_VELOCITY_TIME = 1;
    private static final Float CHICKEN_HEIGHT_OFFSET = 10f;

    private GameObjectCollection gameObjects;
    private HashSet<Integer> shouldNotRender = new HashSet<>();
    private HashMap<Integer, Chicken> xToChickens = new HashMap<>();
    private Vector2 windowDimensions;
    private ImageReader imageReader;
    private SoundReader soundReader;
    private Counter chickensCounter;
    public Chickens(GameObjectCollection gameObjects, Vector2 windowDimensions,
                    ImageReader imageReader, SoundReader soundReader, Counter counter) {
        this.gameObjects = gameObjects;
        this.windowDimensions = windowDimensions;
        this.imageReader = imageReader;
        this.soundReader = soundReader;
        this.chickensCounter = counter;
    }

    /**
     * helper function to create the chickens
     * @param minX
     * @param maxX
     */
    public void createInRange(int minX, int maxX) {
        int chickenY = (int) (windowDimensions.y() * CHICKENS_HEIGHT);
        Renderable chickenImage = imageReader.readImage(CHICKEN_IMAGE, true);
        Sound[] chicken_sounds = {
                soundReader.readSound(CHICKEN_NOISE_1),
                soundReader.readSound(CHICKEN_NOISE_2)
        };

        int realMinX = getUpperClosestX(minX);
        int realMaxX = getLowerClosestX(maxX);
        for (int curX = realMinX;  curX <= realMaxX; curX += CHICKENS_DISTANCE) {
            if (!shouldNotRender.contains(curX) && !xToChickens.containsKey(curX)) {
                Vector2 chickenPos = new Vector2(curX, chickenY);
                Sound randomSound = chicken_sounds[(int) Math.random()*2];
                Chicken chicken = new Chicken(chickenPos, CHICKEN_SIZE, chickenImage,
                        shouldNotRender, randomSound, chickensCounter);
                chicken.setTag(CHICKEN_TAG);
                chicken.setVelocity(Vector2.UP.mult(CHICKEN_SPEED));
                gameObjects.addGameObject(chicken, CHICKEN_LAYER);
                gameObjects.layers().shouldLayersCollide(CHICKEN_LAYER, CHICKEN_LAYER, true);

                new Transition<Float>(chicken,
                        (y) -> chicken.setCenter(new Vector2(chicken.getCenter().x(), y)),
                        CHICKEN_HEIGHT_OFFSET, -CHICKEN_HEIGHT_OFFSET,
                        Transition.LINEAR_INTERPOLATOR_FLOAT, SWITCH_CHICKEN_VELOCITY_TIME,
                        Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);

            }
        }
    }

    /**
     * remove all chickens in given range
     * @param minX
     * @param maxX
     */
    public void removeInRange(int minX, int maxX) {
        int realX = getUpperClosestX(minX);
        int realMax = getLowerClosestX(maxX);
        while (realX < realMax) {
            if (xToChickens.get(realX) != null) {
                gameObjects.removeGameObject(xToChickens.get(realX), CHICKEN_LAYER);
                xToChickens.remove(realX);
            }
            realX += CHICKENS_DISTANCE;
        }
    }
    // helper function to get closest x to distances between chickens
    private int getLowerClosestX(int x) {
        if (x < 0) {
            return (x/CHICKENS_DISTANCE - 1) * CHICKENS_DISTANCE;
        }
        return (x/CHICKENS_DISTANCE) * CHICKENS_DISTANCE;
    }
    private int getUpperClosestX(int x) {
        if (x < 0) {
            return (x/CHICKENS_DISTANCE) * CHICKENS_DISTANCE;
        }
        return (x/CHICKENS_DISTANCE + 1) * CHICKENS_DISTANCE;
    }

}
