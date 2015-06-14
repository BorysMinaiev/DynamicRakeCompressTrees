/**
 * Created by Borys Minaiev
 */
public class Cell {
    int parent, cntChild, sumChild;
    int newParent, diffCntChild, diffSumChild;
    boolean removeOnNextLayer;

    Cell(int parent) {
        this.parent = parent;
        newParent = -1;
    }

    void addChild(int v) {
        cntChild++;
        sumChild += v;
    }

    void removeChild(int v) {
        cntChild--;
        sumChild -= v;
    }
}
