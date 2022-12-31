package pepse.util.pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;

public class SunHalo {

    private static final int HALO_SIZE = 200;
    private static final String SUN_HALO_TAG = "sun halo";

    public static GameObject create(GameObjectCollection gameObjects,
                                    int layer, GameObject sun, Color color) {
        GameObject halo = new GameObject(Vector2.ZERO, new Vector2(HALO_SIZE, HALO_SIZE),
                new OvalRenderable(color)) {
            @Override
            public void update(float deltaTime) {
                super.update(deltaTime);
                setCenter(sun.getCenter());
            }
        };

        halo.setTag(SUN_HALO_TAG);
        halo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(halo, layer);
        return halo;
    }

}
