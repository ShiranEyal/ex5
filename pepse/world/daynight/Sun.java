package pepse.util.pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;


public class Sun {
    private static final float SUN_SIZE = 100f;
    private static final String SUN_TAG = "sun";
    private static final float TRAJECTORY_CENTER_X_FACTOR = 0.5f;

    // the sun trajectory looks much better when its center is lower than the window center.
    private static final float TRAJECTORY_CENTER_Y_FACTOR = 0.8f;
    private static final float TRAJECTORY_X_RADIUS_FACTOR = 0.9f;
    private static final float TRAJECTORY_Y_RADIUS_FACTOR = 1.5f;

    public static GameObject create(GameObjectCollection gameObjects, int layer,
                                    Vector2 windowDimensions, float cycleLength) {
        GameObject sun = new GameObject(Vector2.ZERO, new Vector2(SUN_SIZE, SUN_SIZE),
                new OvalRenderable(Color.YELLOW));
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sun.setTag(SUN_TAG);

        new Transition<>(sun, x -> sun.setCenter(calcSunPosition(windowDimensions, x)),
                0f, (float)(2 * Math.PI),
                Transition.LINEAR_INTERPOLATOR_FLOAT, cycleLength,
                Transition.TransitionType.TRANSITION_LOOP, null);

        gameObjects.addGameObject(sun, layer);
        return sun;
    }

    private static Vector2 calcSunPosition(Vector2 windowDimensions, float angleInSky) {
        float horizontalRadius = (windowDimensions.x() / 2) * TRAJECTORY_X_RADIUS_FACTOR;
        float verticalRadius = (windowDimensions.y() / 2) * TRAJECTORY_Y_RADIUS_FACTOR;

        Vector2 trajectoryCenter =
                windowDimensions.multX(TRAJECTORY_CENTER_X_FACTOR).multY(TRAJECTORY_CENTER_Y_FACTOR);

        return new Vector2((float) (trajectoryCenter.x() - horizontalRadius * Math.sin(angleInSky)),
                (float) (trajectoryCenter.y() - verticalRadius * Math.cos(angleInSky)));
    }
}
