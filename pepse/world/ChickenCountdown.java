package pepse.util.pepse.world;

import danogl.GameObject;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Counter;
import danogl.util.Vector2;

public class ChickenCountdown extends GameObject {


    private static final String COUNTDOWN_TEXT = "CHICKENS LEFT: ";
    private final TextRenderable displayRenderable;
    private Counter chickensCounter;

    public ChickenCountdown(Vector2 topLeftCorner, Vector2 dimensions,
                            Counter chickensCounter) {
        super(topLeftCorner, dimensions, null);
        this.chickensCounter = chickensCounter;
        displayRenderable = new TextRenderable(Integer.toString(chickensCounter.value()));
        renderer().setRenderable(displayRenderable);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        displayRenderable.setString(COUNTDOWN_TEXT + Integer.toString(chickensCounter.value()));
    }
}
