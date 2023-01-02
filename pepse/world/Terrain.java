package pepse.util.pepse.world;

import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.pepse.PepseGameManager;
import pepse.util.pepse.util.ColorSupplier;
import pepse.util.pepse.util.NoiseGenerator;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;

/**
 * Terrain class, used to create ground blocks
 */
public class Terrain {
    //ground height
    private static final float GROUND_HEIGHT_AMOUNT = 2f/3f;
    //ground color
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    //terrain depth
    private static final int TERRAIN_DEPTH = 24;
    //noise amplifier
    private static final int NOISE_AMP = 5;
    private static final String GROUND_TAG = "Ground";

    private GameObjectCollection gameObjects;
    private int groundLayer;
    private Vector2 windowDimensions;
    private float groundHeightAtX0;
    private NoiseGenerator noiseGenerator;
    private static HashMap<Integer, List<Block>> xToBlocks = new HashMap<>();

    /**
     * Terrain object constructor
     * @param gameObjects current game objects
     * @param groundLayer layer to create ground in
     * @param windowDimensions dimensions of current window
     * @param seed seed to generate random terrain with
     */
    public Terrain(GameObjectCollection gameObjects,
                   int groundLayer, Vector2 windowDimensions,
                   int seed) {
        this.gameObjects = gameObjects;
        this.groundLayer = groundLayer;
        this.windowDimensions = windowDimensions;
        this.groundHeightAtX0 = GROUND_HEIGHT_AMOUNT * this.windowDimensions.y();
        this.noiseGenerator = new NoiseGenerator(seed);

    }

    /**
     * get ground height at certain x
     * @param x position we want to get heit at
     * @return height of ground at x
     */
    public float groundHeightAt(float x) {
        double noise = (noiseGenerator.noise(x));
        return (float) (groundHeightAtX0 + noise * Block.SIZE * NOISE_AMP);
    }

    /**
     * create all blocks in certain x range
     * @param minX
     * @param maxX
     */
    public void createInRange(int minX, int maxX) {
        for (int x = getClosestX(minX); x < maxX; x += Block.SIZE) {
            int y = ((int)groundHeightAt(x) / Block.SIZE) * Block.SIZE;
            List<Block> blocksList = new ArrayList<>();
            for (int i = 0; i < TERRAIN_DEPTH; i++) {
                Block b = new Block(Vector2.ZERO,
                        new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR)));
                b.setTopLeftCorner(new Vector2(x, y - Block.SIZE));
                b.setTag(GROUND_TAG);
                gameObjects.addGameObject(b, groundLayer - i);
                y += Block.SIZE;
                blocksList.add(b);
            }
            xToBlocks.put(x, blocksList);
        }
    }

    /**
     * removes all ground blocks in certain x position
     * @param x
     */
    public void removeXBlocks(int x) {
        int realX = getClosestX(x);
        int i = 0;
        for (Block b : xToBlocks.get(realX)) {
            gameObjects.removeGameObject(b, groundLayer - i);
            i++;
        }
    }

    public void removeBlocksInRange(int minX, int maxX) {
        for (int x = minX; x < maxX; x += Block.SIZE) {
            removeXBlocks(x);
        }
    }

    /*
    helper function to get closest x to starting x that is
    divisable by blockSize
     */
    public int getClosestX(int minX) {
        if (minX < 0) {
            return Block.SIZE * (minX/Block.SIZE - 1);
        }
        return Block.SIZE * (minX/Block.SIZE);
    }

}
