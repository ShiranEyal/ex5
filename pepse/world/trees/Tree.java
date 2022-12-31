package pepse.util.pepse.world.trees;

import pepse.util.pepse.world.Block;
import pepse.util.pepse.world.Terrain;

import java.util.Random;

public class Tree {
    private final Random random;
    private final Terrain terrain;

    public Tree(int seed, Terrain terrain) {
        random = new Random(seed);
        this.terrain = terrain;
    }

    public void createInRange(int minX, int maxX) {
        for (int x = minX; x <= maxX; x++) {
            if (random.nextInt(10) == 0) {
                createIn(x, (int)(Math.floor(terrain.groundHeightAt(x)) / Block.SIZE) * Block.SIZE);
            }
        }
    }

    private void createIn(int x, int groundHeightAt) {
    }
}
