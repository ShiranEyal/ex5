package pepse.util.pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * Sky class, creates the sky background
 */
public class Sky {
    //sky color
    private static final Color BASIC_SKY_COLOR = Color.decode("#80C6E5");
    private static final String SKY_TAG = "sky";

    /**
     * Create function that creates a new GameObject representing the sky,
     * adds it to gameObjects and returns the newly created object.
     * @param gameObjects all current GameObjects
     * @param windowDimensions the dimensions of the window
     * @param skyLayer layer to create the sky in
     * @return
     */
    public static GameObject create(GameObjectCollection gameObjects,
                                    Vector2 windowDimensions, int skyLayer) {
        GameObject sky = new GameObject(
                Vector2.ZERO, windowDimensions,
                new RectangleRenderable(BASIC_SKY_COLOR));
        sky.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sky.setTag(SKY_TAG);
        gameObjects.addGameObject(sky, skyLayer);
        return sky;
    }
}
