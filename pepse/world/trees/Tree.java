package pepse.util.pepse.world.trees;

import danogl.collisions.GameObjectCollection;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.pepse.world.Block;
import pepse.util.pepse.world.Terrain;

import java.awt.*;
import java.util.Random;

public class Tree {
    private static final int MIN_TREE_HEIGHT = Block.SIZE * 5;
    private static final int MAX_TREE_HEIGHT = Block.SIZE * 10;

    private static final Color TREE_TRUNK_COLOR = new Color(100, 50, 20);
    private static final Color TREE_LEAF_COLOR = new Color(50, 200, 30);
    private static final String TREE_TRUNK_TAG = "tree trunk";
    private static final String TREE_LEAF_TAG = "tree leaf";
    private static final int MAX_LEAF_DELAY = 5;
    private static final float MIN_LEAF_LIFETIME = 10f;
    private static final float MAX_LEAF_LIFETIME = 300f;
    private static final float MAX_DEATH_LIFETIME = 40f;
    private static final float LEAF_FADEOUT_TIME = 25f;

    private final Random random;
    private final Terrain terrain;
    private final GameObjectCollection gameObjects;
    private final int layer;

    public Tree(GameObjectCollection gameObjects, int layer, Terrain terrain, int seed) {
        random = new Random(seed);
        this.terrain = terrain;
        this.gameObjects = gameObjects;
        this.layer = layer;

    }

    public void createInRange(int minX, int maxX) {
        int firstBlockX = (minX / Block.SIZE) * (Block.SIZE);
        for (int curX = firstBlockX; curX <= maxX; curX += Block.SIZE) {
            if (random.nextInt(10) == 0) {
                createTreeInX(curX);
                curX += 3 * Block.SIZE; // this line makes sure that the trees aren't too close each other.
            }
        }
    }

    private void createTreeInX(int x) {
        int treeHeight = random.nextInt(MIN_TREE_HEIGHT, MAX_TREE_HEIGHT);
        int terrainHeightInPixels = (int)(Math.floor(terrain.groundHeightAt(x)) / Block.SIZE) * Block.SIZE;
        int firstTrunkBlockY = terrainHeightInPixels - 2 * Block.SIZE;
        int lastTrunkBlockY = terrainHeightInPixels -  treeHeight - 2 * Block.SIZE;
        createTreeTrunk(x, firstTrunkBlockY, lastTrunkBlockY);
        createTreeLeaves(x, lastTrunkBlockY);
    }

    private void createTreeTrunk(int x, int fromY, int toY) {
        for (float curY = fromY; curY >= toY; curY -= Block.SIZE) {
            Block trunkBlock = new Block(new Vector2(x, curY),
                    new RectangleRenderable(TREE_TRUNK_COLOR));
            trunkBlock.setTag(TREE_TRUNK_TAG);
            gameObjects.addGameObject(trunkBlock, layer);
        }
    }


    private void createTreeLeaves(int centerX, int centerY) {
        for (int curX = centerX - Block.SIZE * 2; curX <= centerX + Block.SIZE * 2;
             curX += Block.SIZE) {
            for (int curY = centerY - Block.SIZE * 2; curY <= centerY + Block.SIZE * 2; curY += Block.SIZE) {
                Leaf leaf = new Leaf(new Vector2(curX, curY), new RectangleRenderable(TREE_LEAF_COLOR));
                leaf.setTag(TREE_LEAF_TAG);

                // leaf wind movement after random time
                float leafTransitionsDelay = random.nextFloat(MAX_LEAF_DELAY);
                new ScheduledTask(leaf, leafTransitionsDelay, false, () -> leafWindTransitions(leaf));

                // start the life cycle
                resetLeafAndScheduleKill(leaf);

                gameObjects.addGameObject(leaf, layer + 1);
            }
        }
    }


    private void resetLeafAndScheduleKill(Leaf leaf) {
        leaf.reset();
        new ScheduledTask(leaf, random.nextFloat(MIN_LEAF_LIFETIME, MAX_LEAF_LIFETIME),
                false, () -> killLeafAndScheduleReset(leaf));
    }

    private void killLeafAndScheduleReset(Leaf leaf) {
        leaf.kill();
        Runnable resetLeafEndOfFadeOut =
                () -> new ScheduledTask(leaf, random.nextFloat(MAX_DEATH_LIFETIME),
                false, () -> resetLeafAndScheduleKill(leaf));
        leaf.renderer().fadeOut(LEAF_FADEOUT_TIME, resetLeafEndOfFadeOut);
    }


    private static void leafWindTransitions(Block leaf) {
        new Transition<Float>(leaf, angle -> leaf.renderer().setRenderableAngle(angle),
                5f, -10f,
                Transition.LINEAR_INTERPOLATOR_FLOAT, 3,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
        new Transition<Float>(leaf, width -> leaf.setDimensions(new Vector2(width, leaf.getDimensions().y())),
                (float)Block.SIZE, (float) Block.SIZE - 3, Transition.LINEAR_INTERPOLATOR_FLOAT,
                7, Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
    }
}


