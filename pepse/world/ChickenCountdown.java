package pepse.util.pepse.world;

import danogl.GameObject;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Counter;
import danogl.util.Vector2;

/**
 * ChickenCountdown, extends GameObject,
 * represents the countdown at the bottom left of the screen.
 */
public class ChickenCountdown extends GameObject {
    private static final String COUNTDOWN_TEXT = "CHICKENS LEFT: ";
    private final TextRenderable displayRenderable;
    private Counter chickensCounter;

    /**
     * ChickenCountdown object constructor
     * @param topLeftCorner position of the GameObject
     * @param dimensions size of the GameObject
     * @param chickensCounter danogl Counter representing how many chickens left
     */
    public ChickenCountdown(Vector2 topLeftCorner, Vector2 dimensions,
                            Counter chickensCounter) {
        super(topLeftCorner, dimensions, null);
        this.chickensCounter = chickensCounter;
        displayRenderable = new TextRenderable(Integer.toString(chickensCounter.value()));
        renderer().setRenderable(displayRenderable);
    }

    /**
     * Override update function for ChickenCountdown
     * @param deltaTime The time elapsed, in seconds, since the last frame. Can
     *                  be used to determine a new position/velocity by multiplying
     *                  this delta with the velocity/acceleration respectively
     *                  and adding to the position/velocity:
     *                  velocity += deltaTime*acceleration
     *                  pos += deltaTime*velocity
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        displayRenderable.setString(COUNTDOWN_TEXT + Integer.toString(chickensCounter.value()));
    }
}
