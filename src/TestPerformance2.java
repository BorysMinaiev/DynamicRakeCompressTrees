import java.util.Random;

/**
 * Created by Borys Minaiev
 * parent[i] = random number from [max(i - 10, 0) .. i]
 */
public class TestPerformance2 {
    private final Random rnd = new Random(123);

    public static void main(String[] args) {
        new TestPerformance2().run();
    }

    private int generateParent(int i) {
        int from = Math.max(0, i - 10);
        int to = i;
        return rnd.nextInt(to - from + 1) + from;
    }

    private void doOneTest(boolean debug) {
        if (debug) {
            System.err.println("debug!");
        }
        final int MAXN = 100000; // vertices number
        final int MAXM = 100000; // modifications number
        final int n = MAXN;

        int[] perm = Utils.genRandomPermutation(n, rnd);

        final int[] parent = new int[n];
        for (int i = 0; i < parent.length; i++) {
            parent[perm[i]] = perm[generateParent(i)];
        }
        final MyRandomGenerator randGenerator = new MyRandomGenerator(n,
                rnd.nextInt());
        final long START_TIME = System.currentTimeMillis();
        final RCTree tree = new RCTree(parent, randGenerator);

        for (int it = 0; it < MAXM; it++) {
            final int id = rnd.nextInt(n);
            if (id == 0) {
                continue;
            }
            if (parent[perm[id]] == perm[id]) {
                // link
                parent[perm[id]] = perm[generateParent(id)];
            } else {
                // cut
                parent[perm[id]] = perm[id];
            }
            tree.changeTree(perm[id], parent[perm[id]]);
        }

        System.err.println("time = " + (System.currentTimeMillis() - START_TIME) + " ms");
    }
    private void run() {
        final int ITERATIONS_COUNT = 10;
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
