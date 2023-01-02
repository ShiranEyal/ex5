package pepse.util.pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.components.GameObjectPhysics;
import danogl.components.Transition;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.pepse.world.Avatar;
import pepse.util.pepse.world.Block;

public class Leaf extends Block {
    private static final float LEAF_FALL_VELOCITY = 20;
    private static final Float HORIZONTAL_TRANSITION_MIN_VEL = -10f;
    private static final Float HORIZONTAL_TRANSITION_MAX_VEL = 7f;
    private static final float HORIZONTAL_TRANSITION_TIME = 7;
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
        if (other instanceof Leaf) {
            return false;
        }
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
                this, x -> transform().setVelocityX(x),
                HORIZONTAL_TRANSITION_MIN_VEL, HORIZONTAL_TRANSITION_MAX_VEL,
                Transition.LINEAR_INTERPOLATOR_FLOAT, HORIZONTAL_TRANSITION_TIME,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
    }

    public void reset() {
        isOnTree = true;
        transform().setTopLeftCorner(originalPosition);
        physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
        transform().setVelocity(Vector2.ZERO);
    }
}
