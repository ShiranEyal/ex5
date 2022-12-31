package pepse.util.pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.*;
import java.awt.event.KeyEvent;

public class Avatar extends GameObject {
    //player velocity
    private static final int PLAYER_VELOCITY = 300;
    private UserInputListener inputListener;
    /**
     * Avatar object constructor
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     *                      the GameObject will not be rendered.
     */
    public Avatar(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                  UserInputListener inputListener) {
        super(topLeftCorner, dimensions, renderable);
        this.inputListener = inputListener;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (inputListener.isKeyPressed(KeyEvent.VK_LEFT)) {
            Vector2 newVel = new Vector2(-PLAYER_VELOCITY, getVelocity().y());
            setVelocity(newVel);
        } else if (inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) {
            Vector2 newVel = new Vector2(PLAYER_VELOCITY, getVelocity().y());
            setVelocity(newVel);
        } else {
            setVelocity(new Vector2(0, getVelocity().y()));
        }
    }
}
