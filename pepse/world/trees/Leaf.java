package pepse.util.pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.components.Transition;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.pepse.world.Block;

public class Leaf extends Block {

    private Transition<Float> horizontalTransition;

    /**
     * Object constructor for Block GameObject
     *
     * @param topLeftCorner
     * @param renderable    image of block
     */
    public Leaf(Vector2 topLeftCorner, Renderable renderable) {
        super(topLeftCorner, renderable);
    }

    public void setHorizontalTransition(Transition<Float> horizontalTransition) {
        this.horizontalTransition = horizontalTransition;
        physics().setMass(0);
    }

    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        transform().setVelocityY(0);
        removeComponent(horizontalTransition);
    }
}
