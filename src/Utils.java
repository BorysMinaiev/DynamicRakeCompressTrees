import java.util.*;

/**
 * Created by Borys Minaiev
 */
public class Utils {

    public static boolean same(RCTree first, RCTree second) {
        if (first == null && second == null) {
            return true;
        }
        if (first == null || second == null) {
            return false;
        }
        if (first.getCntVertices() != second.getCntVertices()) {
            return false;
        }
        final int n = first.getCntVertices();
        for (int i = 0; i < n; i++) {
            List<Cell> firstList = first.getCellsListForVertex(i);
            List<Cell> secondList = second.getCellsListForVertex(i);
            if (firstList.size() != secondList.size()) {
                return false;
            }
            final int sz = firstList.size();
            for (int j = 0; j < sz; j++) {
                Cell firstCell = firstList.get(j), secondCell = secondList
                        .get(j);
                if (!same(firstCell, secondCell)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static int[] genRandomPermutation(final int n, final Random rnd) {
        int[] perm = new int[n];
        for (int i = 0; i < n; i++) {
            int id = rnd.nextInt(i + 1);
            perm[i] = perm[id];
            perm[id] = i;
        }
        return perm;
    }

    private static boolean same(Cell firstCell, Cell secondCell) {
        if (firstCell == null && secondCell == null) {
            return true;
        }
        if (firstCell == null || secondCell == null) {
            return false;
        }
        if (firstCell.parent != secondCell.parent) {
            return false;
        }
        if (firstCell.cntChild != secondCell.cntChild) {
            return false;
        }
        return firstCell.sumChild == secondCell.sumChild;
    }

}
