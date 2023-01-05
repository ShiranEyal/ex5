package pepse.util.pepse.world.chickens;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.Sound;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;
import pepse.util.pepse.world.Shot;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Chicken class, extends GameObject,
 * represents a single chicken instance in the game.
 */
public class Chicken extends GameObject {
    private HashSet<Integer> shouldNotRender;
    private Sound chickenSound;
    private Counter chickensCounter;
    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     *                      the GameObject will not be rendered.
     */
    public Chicken(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                   HashSet<Integer> shouldNotRender, Sound chickenSounds, Counter counter) {
        super(topLeftCorner, dimensions, renderable);
        this.shouldNotRender = shouldNotRender;
        this.chickenSound = chickenSounds;
        this.chickensCounter = counter;
    }

    /**
     * Override onCollisionEnter function for Chicken
     * @param other The GameObject with which a collision occurred.
     * @param collision Information regarding this collision.
     *                  A reasonable elastic behavior can be achieved with:
     *                  setVelocity(getVelocity().flipped(collision.getNormal()));
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        chickenSound.play();
        int x = (int) getTopLeftCorner().x();
        shouldNotRender.add(x);
        chickensCounter.decrement();
    }

    /**
     * Override shouldCollideWith method for Chicken
     * @param other The other GameObject.
     * @return
     */
    @Override
    public boolean shouldCollideWith(GameObject other) {
        if (other instanceof Shot) {
            return true;
        }
        return false;
    }
}
