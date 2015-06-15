/**
 * Created by Borys Minaiev
 */
public class Query {
    private final boolean isTreeChange;
    private final int vertex;
    private final int newParent;

    Query(int vertex) {
        this.isTreeChange = false;
        this.vertex = vertex;
        this.newParent = -1;
    }

    Query(int vertex, int parent) {
        this.isTreeChange = true;
        this.vertex = vertex;
        this.newParent = parent;
    }

    public boolean getIsTreeChange() {
        return isTreeChange;
    }

    public int getVertex() {
        return vertex;
    }

    public int getNewParent() {
        return newParent;
    }
}
