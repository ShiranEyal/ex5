package pepse.util.pepse.world;

import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.pepse.util.ColorSupplier;

import java.awt.*;

/**
 * Terrain class, used to create ground blocks
 */
public class Terrain {
    //ground height
    private static final float GROUND_HEIGHT_AMOUNT = 2f/3f;
    //ground color
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    //top left corner
    private static final Vector2 TOP_LEFT_CORNER = new Vector2(0, 0);
    private GameObjectCollection gameObjects;
    private int groundLayer;
    private Vector2 windowDimensions;
    private float groundHeightAtX0;

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
    }

    /**
     * get ground height at certain x
     * @param x position we want to get heit at
     * @return height of ground at x
     */
    public float groundHeightAt(float x) {
        return groundHeightAtX0;
    }

    /**
     * create all blocks in certain x range
     * @param minX
     * @param maxX
     */
    public void createInRange(int minX, int maxX) {
        int x = getClosestX(minX);
        Renderable blockRenderable = new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR));
        while (x < maxX) {
            float groundHeightAtx = groundHeightAt(x);
            float y = windowDimensions.y();
            while (y > groundHeightAtx) {
                Block b = new Block(TOP_LEFT_CORNER, blockRenderable);
                b.setCenter(new Vector2(x + Block.SIZE/2, y - Block.SIZE/2));
                b.setTag("Ground");
                gameObjects.addGameObject(b);
                y -= Block.SIZE;
            }
            x += Block.SIZE;
        }
    }
    /*
    helper function to get closest x to starting x that is
    divisable by blockSize
     */
    private int getClosestX(int minX) {
        if (minX < 0) {
            return Block.SIZE * ((int) (minX/Block.SIZE + 1));
        }
        return Block.SIZE * ((int) (minX/Block.SIZE));
    }

}
