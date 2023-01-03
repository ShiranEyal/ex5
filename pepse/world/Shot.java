package pepse.util.pepse.world;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.SoundReader;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * Shot class that represents a shot from the avatar
 */
public class Shot extends GameObject {
    private GameObjectCollection gameObjects;
    private int layer;

    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     *                      the GameObject will not be rendered.
     */
    public Shot(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                GameObjectCollection gameObjects, int layer) {
        super(topLeftCorner, dimensions, renderable);
        this.gameObjects = gameObjects;
        this.layer = layer;
    }

    /**
     * Override onCollisionEnter function for shot
     * @param other The GameObject with which a collision occurred.
     * @param collision Information regarding this collision.
     *                  A reasonable elastic behavior can be achieved with:
     *                  setVelocity(getVelocity().flipped(collision.getNormal()));
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        gameObjects.removeGameObject(other, layer);
        gameObjects.removeGameObject(this, layer);
    }
}
