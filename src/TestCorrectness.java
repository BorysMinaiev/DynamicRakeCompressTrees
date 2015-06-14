import java.util.*;

/**
 * Created by Borys Minaiev
 */
public class TestCorrectness {
    private final Random rnd = new Random(123);

    public static void main(String[] args) {
        new TestCorrectness().run();
    }

    private void doOneTest(boolean debug) {
        if (debug) {
            System.err.println("debug!");
        }
        final int MAXN = 100;
        final int n = rnd.nextInt(MAXN) + 1;

        int[] perm = Utils.genRandomPermutation(n, rnd);

        final int[] parent = new int[n];
        for (int i = 0; i < parent.length; i++) {
            parent[perm[i]] = perm[rnd.nextInt(i + 1)];
        }
        int id = rnd.nextInt(n);
        final MyRandomGenerator randGenerator = new MyRandomGenerator(n,
                rnd.nextInt());
        final RCTree tree = new RCTree(parent, randGenerator);
        final int[] newParent = parent.clone();
        final int wasParent = newParent[id];
        newParent[id] = id;

        // cut
        tree.changeTree(id, newParent[id]);
        final RCTree correctTree = new RCTree(newParent, randGenerator);
        if (!Utils.same(tree, correctTree)) {
            throw new AssertionError("failed");
        }

        // link
        tree.changeTree(id, wasParent);
        final RCTree correctOne = new RCTree(parent, randGenerator);
        if (!Utils.same(tree, correctOne)) {
            throw new AssertionError("failed");
        }
    }
    private void run() {
        final int ITERATIONS_COUNT = 123123;
        for (int it = 0; it < ITERATIONS_COUNT; it++) {
            System.err.println("iteration = " + it);
            boolean debug = it == -1;
            if (debug) {
                System.err.println("debug!");
            }
            doOneTest(debug);
        }
    }
}
