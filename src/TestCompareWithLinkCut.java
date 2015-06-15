import LinkCut.LinkCut;
import LinkCut.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Borys Minaiev
 * parent[i] = random number from [0 .. i]
 */
public class TestCompareWithLinkCut {
    private final Random rnd = new Random(123);

    public static void main(String[] args) {
        new TestCompareWithLinkCut().run();
    }

    private int generateParent(int i) {
        if (i == 0) {
            return 0;
        }
        int from = 0;
        int to = i;
        return rnd.nextInt(to - from + 1) + from;
    }

    private void doOneTest(boolean debug) {
        if (debug) {
            System.err.println("debug!");
        }
        final int MAXN = 100000; // vertices number
        final int MAXM = 1000000; // modifications number
        final int n = MAXN;

        int[] perm = Utils.genRandomPermutation(n, rnd);

        final int[] parent = new int[n];
        for (int i = 0; i < parent.length; i++) {
            parent[perm[i]] = perm[generateParent(i)];
        }

        final TestCase testCase = new TestCase(parent);

        for (int it = 0; it < MAXM; it++) {
            final int id = rnd.nextInt(n);
            if (rnd.nextBoolean()) {
                testCase.addQuery(new Query(id));
            } else {
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
                testCase.addQuery(new Query(perm[id], parent[perm[id]]));
            }
        }

        List<Integer> LCresult = testLinkCutTree(testCase);
        List<Integer> RCresult = testRakeCompressTree(testCase);

        if (!RCresult.equals(LCresult)) {
            throw new AssertionError("results are not equal");
        }
    }

    private List<Integer> testRakeCompressTree(final TestCase testCase) {
        final long START_TIME = System.currentTimeMillis();
        final MyRandomGenerator randGenerator = new MyRandomGenerator(testCase.getN(),
                rnd.nextInt());
        int[] parent = new int[testCase.getN()];
        for (int i = 0; i < testCase.getN(); i++) {
            parent[i] = testCase.getParent(i);
        }
        final RCTree tree = new RCTree(parent, randGenerator);
        final List<Integer> result = new ArrayList<>();
        for (int i = 0; i < testCase.getQueriesCount(); i++) {
            final Query query = testCase.getQuery(i);
            if (query.getIsTreeChange()) {
                tree.changeTree(query.getVertex(), query.getNewParent());
            } else {
                result.add(tree.getRoot(query.getVertex()));
            }
        }
        System.err.println("time for RC tree = " + (System.currentTimeMillis() - START_TIME) + " ms");
        return result;
    }

    private List<Integer> testLinkCutTree(final TestCase testCase) {
        final long START_TIME = System.currentTimeMillis();
        Node[] nodes = new Node[testCase.getN()];
        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = new Node(1, i);
        }
        for (int i = 0; i < testCase.getN(); i++) {
            if (testCase.getParent(i) != i) {
                LinkCut.link(nodes[i], nodes[testCase.getParent(i)]);
            }
        }
        final List<Integer> result = new ArrayList<>();
        for (int i = 0; i < testCase.getQueriesCount(); i++) {
            final Query query = testCase.getQuery(i);
            if (query.getIsTreeChange()) {
                if (query.getVertex() == query.getNewParent()) {
                    LinkCut.cut(nodes[query.getVertex()]);
                } else {
                    LinkCut.link(nodes[query.getVertex()], nodes[query.getNewParent()]);
                }
            } else {
                result.add(LinkCut.rootid(nodes[query.getVertex()]));
            }
        }
        System.err.println("time for LC tree = " + (System.currentTimeMillis() - START_TIME) + " ms");
        return result;
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
