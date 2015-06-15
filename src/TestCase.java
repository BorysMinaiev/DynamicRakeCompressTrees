import java.util.ArrayList;
import java.util.List;

/**
 * Created by Borys Minaiev
 */
public class TestCase {
    private final int n;
    private final int[] parent;
    private final List<Query> queryList;

    TestCase(int[] parent) {
        this.parent = parent.clone();
        this.n = parent.length;
        queryList = new ArrayList<>();
    }

    public void addQuery(Query query) {
        queryList.add(query);
    }

    public int getQueriesCount() {
        return queryList.size();
    }

    public Query getQuery(int id) {
        return queryList.get(id);
    }

    public int getN() {
        return parent.length;
    }

    public int getParent(int id) {
        return parent[id];
    }
}
