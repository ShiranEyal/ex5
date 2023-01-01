package pepse.util.pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.components.GameObjectPhysics;
import danogl.components.Transition;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.pepse.world.Avatar;
import pepse.util.pepse.world.Block;

import java.awt.desktop.AppHiddenEvent;

public class Leaf extends Block {
    private static final float LEAF_FALL_VELOCITY = 20;
    private Transition<Float> horizontalTransition;
    private boolean isOnTree;
    private final Vector2 originalPosition;


    /**
     * Object constructor for Block GameObject
     *
     * @param topLeftCorner
     * @param renderable    image of block
     */
    public Leaf(Vector2 topLeftCorner, Renderable renderable) {
        super(topLeftCorner, renderable);
        originalPosition = topLeftCorner.getImmutableCopy();
        isOnTree = true;
        physics().setMass(0);
    }

    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        removeComponent(horizontalTransition);
        transform().setVelocityX(0);
    }

    @Override
    public boolean shouldCollideWith(GameObject other) {
        if (other instanceof Avatar && !isOnTree) {
            return false;
        }
        return super.shouldCollideWith(other);
    }

    public void kill() {
        isOnTree = false;
        transform().setVelocityY(LEAF_FALL_VELOCITY);
        physics().setMass(0);
        horizontalTransition = new Transition<>(
                this, x -> transform().setVelocityX(x), -10f,
                10f, Transition.LINEAR_INTERPOLATOR_FLOAT, 7,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
    }

    public void reset() {
        isOnTree = true;
        transform().setTopLeftCorner(originalPosition);
        physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
        renderer().setOpaqueness(1);
    }
}
