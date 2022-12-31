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

    public static GameObject create(GameObjectCollection gameObjects, int layer,
                                    Vector2 windowDimensions, float cycleLength) {
        GameObject sun = new GameObject(Vector2.ZERO, new Vector2(SUN_SIZE, SUN_SIZE),
                new OvalRenderable(Color.YELLOW));
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sun.setTag(SUN_TAG);

        new Transition<Float>(sun, x -> sun.setCenter(calcSunPosition(windowDimensions, x)),
                0f, (float)(2 * Math.PI),
                Transition.LINEAR_INTERPOLATOR_FLOAT, cycleLength,
                Transition.TransitionType.TRANSITION_LOOP, null);

        gameObjects.addGameObject(sun, layer);
        return sun;
    }

    private static Vector2 calcSunPosition(Vector2 windowDimensions, float angleInSky) {
        float horizontalRadius = (windowDimensions.x() / 2) - SUN_SIZE;
        float verticalRadius = (windowDimensions.y() / 2) - SUN_SIZE;
        Vector2 windowCenter = windowDimensions.mult(0.5f);

        return new Vector2((float) (windowCenter.x() - horizontalRadius * Math.sin(angleInSky)),
                (float) (windowCenter.y() - verticalRadius * Math.cos(angleInSky)));
    }
}
