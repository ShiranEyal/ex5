package pepse.util.pepse.world;

import danogl.GameObject;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * Block class, extends GameObject,
 * represents a single static block in the game.
 */
public class Block extends GameObject {
    public static final int SIZE = 30;

    /**
     * Object constructor for Block GameObject
     * @param topLeftCorner
     * @param renderable image of block
     */
    public Block(Vector2 topLeftCorner, Renderable renderable) {
        super(topLeftCorner, Vector2.ONES.mult(SIZE), renderable);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
    }
}
