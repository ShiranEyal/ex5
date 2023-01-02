package pepse.util.pepse.world.trees;

import danogl.collisions.GameObjectCollection;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.pepse.world.Block;
import pepse.util.pepse.world.Terrain;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class Tree {
    private static final int MIN_TREE_HEIGHT = Block.SIZE * 5;
    private static final int MAX_TREE_HEIGHT = Block.SIZE * 10;

    private static final Color TREE_TRUNK_COLOR = new Color(100, 50, 20);
    private static final Color TREE_LEAF_COLOR = new Color(50, 200, 30);
    private static final String TREE_TRUNK_TAG = "tree trunk";
    private static final String TREE_LEAF_TAG = "tree leaf";
    private static final int MAX_LEAF_WIND_DELAY = 5;
    private static final float MIN_LEAF_LIFETIME = 10f;
    private static final float MAX_LEAF_LIFETIME = 300f;
    private static final float MAX_DEATH_LIFETIME = 40f;
    private static final float LEAF_FADEOUT_TIME = 25f;
    private static final Float TREES_PLACEMENT_THRESHOLD = 0.125f;
    private static final int TRUNK_Y_OFFSET = 2 * Block.SIZE;
    private static final int NUM_LEAVES_IN_SIDE = 2;
    private static final Renderable LEAVES_RENDERABLE = new RectangleRenderable(TREE_LEAF_COLOR);
    private static final Renderable TRUNK_RENDERABLE = new RectangleRenderable(TREE_TRUNK_COLOR);
    private static final Float LEAF_ROTATION_INIT_VAL = 5f;
    private static final Float LEAF_ROTATION_FINAL_VAL = -10f;
    private static final float LEAF_ROTATION_TIME = 3;
    private static final float LEAF_WIDTH_TRANSITION_DIFF = 3;
    private static final float LEAF_WIDTH_TRANSITION_TIME = 7;

    private final Terrain terrain;
    private final GameObjectCollection gameObjects;
    private final int layer;

    private static final HashMap<Integer, List<Block>> xToTree = new HashMap<>();

    private final Function<Integer, Float> treesPlacementFunction;
    private final Function<Integer, Float> treesHeightFunction;
    private final Random random;




    public Tree(GameObjectCollection gameObjects, int layer, Terrain terrain, int seed) {
        this.terrain = terrain;
        this.gameObjects = gameObjects;
        this.layer = layer;
        random = new Random(seed);
        int treesPlacementSeed = random.nextInt();
        treesPlacementFunction = reproducibleFunction(treesPlacementSeed);
        int treesHeightSeed = random.nextInt();
        treesHeightFunction = reproducibleFunction(treesHeightSeed);
    }

    private static Function<Integer, Float> reproducibleFunction(int seed) {
        return x -> 0.25f * (2 + (float) (Math.sin(x + seed) + Math.sin(Math.E * x * seed)));
    }

    public void createInRange(int minX, int maxX) {
        int firstBlockX = (minX / Block.SIZE) * (Block.SIZE);
        for (int curX = firstBlockX; curX < maxX; curX += Block.SIZE) {
            if (treesPlacementFunction.apply(curX) < TREES_PLACEMENT_THRESHOLD) {
                createTreeInX(curX);
                curX += 5 * Block.SIZE; // this line makes sure that the trees
                                        // aren't too close each other.
            }
        }
    }

    private void createTreeInX(int x) {
        int treeHeight = MIN_TREE_HEIGHT +
                (int)(treesHeightFunction.apply(x) * (MAX_TREE_HEIGHT - MIN_TREE_HEIGHT));
        int terrainHeightInPixels = (int)(Math.floor(terrain.groundHeightAt(x)) / Block.SIZE) * Block.SIZE;
        int firstTrunkBlockY = terrainHeightInPixels - TRUNK_Y_OFFSET;
        int lastTrunkBlockY = terrainHeightInPixels -  treeHeight - TRUNK_Y_OFFSET;
        xToTree.put(x, new ArrayList<>());

        createTreeTrunk(x, firstTrunkBlockY, lastTrunkBlockY);
        createTreeLeaves(x, lastTrunkBlockY);
    }

    private void createTreeTrunk(int x, int fromY, int toY) {
        for (float curY = fromY; curY >= toY; curY -= Block.SIZE) {
            Block trunkBlock = new Block(new Vector2(x, curY), TRUNK_RENDERABLE);
            trunkBlock.setTag(TREE_TRUNK_TAG);
            gameObjects.addGameObject(trunkBlock, layer);
            xToTree.get(x).add(trunkBlock);
        }
    }


    private void createTreeLeaves(int centerX, int centerY) {
        for (int curX = centerX - Block.SIZE * NUM_LEAVES_IN_SIDE;
             curX <= centerX + Block.SIZE * NUM_LEAVES_IN_SIDE; curX += Block.SIZE) {
            for (int curY = centerY - Block.SIZE * NUM_LEAVES_IN_SIDE;
                 curY <= centerY + Block.SIZE * NUM_LEAVES_IN_SIDE; curY += Block.SIZE) {
                Leaf leaf = new Leaf(new Vector2(curX, curY), LEAVES_RENDERABLE);
                leaf.setTag(TREE_LEAF_TAG);

                // leaf wind movement after random time
                float leafTransitionsDelay = random.nextFloat(MAX_LEAF_WIND_DELAY);
                new ScheduledTask(leaf, leafTransitionsDelay, false, () -> leafWindTransitions(leaf));

                // start the life cycle
                resetLeafAndScheduleKill(leaf);

                gameObjects.addGameObject(leaf, layer + 1);
                xToTree.get(centerX).add(leaf);
            }
        }
    }


    private void resetLeafAndScheduleKill(Leaf leaf) {
        leaf.reset();
        leaf.renderer().setOpaqueness(1);
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
                LEAF_ROTATION_INIT_VAL, LEAF_ROTATION_FINAL_VAL,
                Transition.LINEAR_INTERPOLATOR_FLOAT, LEAF_ROTATION_TIME,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);

        new Transition<Float>(leaf, width -> leaf.setDimensions(new Vector2(width, leaf.getDimensions().y())),
                (float)Block.SIZE, (float) Block.SIZE - LEAF_WIDTH_TRANSITION_DIFF,
                Transition.LINEAR_INTERPOLATOR_FLOAT, LEAF_WIDTH_TRANSITION_TIME,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
    }

    public void removeTreeAtX(int x) {
        if (!xToTree.containsKey(x)) { return; }
        for (Block treeBlock : xToTree.get(x)) {
            if (treeBlock instanceof Leaf) {
                gameObjects.removeGameObject(treeBlock, layer + 1);
            } else {
                gameObjects.removeGameObject(treeBlock, layer);
            }
        }
        xToTree.remove(x);
    }

    public void removeTreeInRange(int minX, int maxX) {
        for (int x = (minX / Block.SIZE) * Block.SIZE;
             x < (maxX / Block.SIZE) * Block.SIZE ; x += Block.SIZE) {
            removeTreeAtX(x);
        }
    }

    public boolean treeInX(int x) {
        return xToTree.containsKey(x);
    }
}


