package pepse.util.pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * Night class, creates a gameObject representing
 * nightTime and manages it
 */
public class Night {

    private static final String NIGHT_TAG = "night";
    private static final Float MIDNIGHT_OPACITY = 0.7f;

    /**
     * Create function that creates a new GameObject representing the night,
     * adds it to gameObjects and returns the newly created object.
     * @param gameObjects all current GameObjects
     * @param layer layer to create night in
     * @param windowDimensions dimensions of the window
     * @param cycleLength length of a single night cycle
     * @return
     */
    public static GameObject create(GameObjectCollection gameObjects,
                                    int layer,
                                    Vector2 windowDimensions,
                                    float cycleLength) {
        GameObject night = new GameObject(Vector2.ZERO, windowDimensions,
                new RectangleRenderable(Color.black));
        night.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        night.setTag(NIGHT_TAG);
        gameObjects.addGameObject(night, layer);
        new Transition<Float>(night,
                night.renderer()::setOpaqueness,
                0f,
                MIDNIGHT_OPACITY,
                Transition.CUBIC_INTERPOLATOR_FLOAT,
                cycleLength / 2, Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null);
        return night;
    }
}
