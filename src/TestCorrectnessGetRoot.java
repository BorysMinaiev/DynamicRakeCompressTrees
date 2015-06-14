import java.util.Random;

/**
 * Created by Borys Minaiev
 * parent[i] = random number from [0 .. i]
 */
public class TestCorrectnessGetRoot {
    private final Random rnd = new Random(123);

    public static void main(String[] args) {
        new TestCorrectnessGetRoot().run();
    }

    private void doOneTest(boolean debug) {
        if (debug) {
            System.err.println("debug!");
        }
        final int MAXN = 1000; // vertices number
        final int MAXM = 10000; // modifications number
        final int n = 1 + rnd.nextInt(MAXN);

        int[] perm = Utils.genRandomPermutation(n, rnd);

        final int[] parent = new int[n];
        for (int i = 0; i < parent.length; i++) {
            parent[perm[i]] = perm[rnd.nextInt(i + 1)];
        }
        final MyRandomGenerator randGenerator = new MyRandomGenerator(n,
                rnd.nextInt());
        final RCTree tree = new RCTree(parent, randGenerator);

        for (int it = 0; it < MAXM; it++) {
            final int id = rnd.nextInt(n);
            if (rnd.nextBoolean()) {
                // modification
                if (id == 0) {
                    continue;
                }
                if (parent[perm[id]] == perm[id]) {
                    // link
                    parent[perm[id]] = perm[rnd.nextInt(id + 1)];
                } else {
                    // cut
                    parent[perm[id]] = perm[id];
                }
                tree.changeTree(perm[id], parent[perm[id]]);
            } else {
                // get root check
                int realRoot = id;
                while (parent[realRoot] != realRoot) {
                    realRoot = parent[realRoot];
                }
                if (realRoot != tree.getRoot(id)) {
                    throw new AssertionError("fail");
                }
            }

        }

    }
    private void run() {
        final int ITERATIONS_COUNT = 100;
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
