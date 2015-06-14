import java.util.*;

/**
 * Created by Borys Minaiev
 */
public class RCTree {
    private final List<Cell>[] vertexStatus;
    private final int[] lastTimeChanged;
    private int time;
    private final MyRandomGenerator randGenerator;

    /*
     * expected time = O(n)
     * expected memory = O(n)
     */
    RCTree(final int[] parent, final MyRandomGenerator randGenerator) {
        final int cntVertices = parent.length;
        vertexStatus = new List[cntVertices];
        for (int i = 0; i < cntVertices; i++) {
            vertexStatus[i] = new ArrayList<>();
        }
        lastTimeChanged = new int[cntVertices];
        time = 0;
        this.randGenerator = randGenerator;
        for (int i = 0; i < cntVertices; i++) {
            vertexStatus[i].add(new Cell(parent[i]));
        }
        for (int i = 0; i < cntVertices; i++) {
            if (parent[i] == i) {
                continue;
            }
            vertexStatus[parent[i]].get(0).addChild(i);
        }
        int layer = 0;
        List<Integer> alive = new ArrayList<>();
        List<Integer> nextAlive = new ArrayList<>();
        for (int v = 0; v < cntVertices; v++) {
            alive.add(v);
        }
        while (true) {
            nextAlive.clear();
            if (alive.isEmpty()) {
                break;
            }
            for (int v : alive) {
                shouldRemoveVertex(v, layer);
            }
            for (int v : alive) {
                final Cell c = vertexStatus[v].get(layer);
                if (c.removeOnNextLayer) {
                    if (c.cntChild == 1) {
                        vertexStatus[c.sumChild].get(layer).newParent = c.parent;
                    }
                    if (c.parent != v) {
                        final Cell parentCell = vertexStatus[c.parent]
                                .get(layer);
                        parentCell.diffCntChild--;
                        parentCell.diffSumChild -= v;
                        if (c.cntChild == 1) {
                            parentCell.diffCntChild++;
                            parentCell.diffSumChild += c.sumChild;
                        }
                    }
                }
            }
            for (int v : alive) {
                final Cell c = vertexStatus[v].get(layer);
                if (c.removeOnNextLayer) {
                    continue;
                }
                nextAlive.add(v);
                final Cell newCell = new Cell(c.newParent == -1 ? c.parent
                        : c.newParent);
                newCell.cntChild = c.cntChild + c.diffCntChild;
                newCell.sumChild = c.sumChild + c.diffSumChild;
                vertexStatus[v].add(newCell);
            }
            layer++;
            List<Integer> tmp = alive;
            alive = nextAlive;
            nextAlive = tmp;
        }
    }

    /*
     * expected time = O(\log n)
     */
    public void changeTree(int vertexChanged, int vertexNewParent) {
        time++;
        List<List<Integer>> affectedOnLayer = new ArrayList<>();
        List<Integer> aliveAndAffected = new ArrayList<>();
        {
            int wasParent = vertexStatus[vertexChanged].get(0).parent;
            markVertexAffected(vertexChanged, affectedOnLayer, 0, aliveAndAffected);
            vertexStatus[vertexChanged].get(0).parent = vertexNewParent;
            markVertexAffected(wasParent, affectedOnLayer, 0,
                    aliveAndAffected);
            markVertexAffected(vertexNewParent, affectedOnLayer, 0,
                    aliveAndAffected);
            if (wasParent != vertexChanged) {
                vertexStatus[wasParent].get(0).removeChild(vertexChanged);
            }
            if (vertexNewParent != vertexChanged) {
                vertexStatus[vertexNewParent].get(0).addChild(vertexChanged);
            }

        }
        int layer = 0;
        List<Integer> nextAliveAndAffected = new ArrayList<>();
        while (true) {
            nextAliveAndAffected.clear();
            if (aliveAndAffected.isEmpty() && affectedOnLayer.size() <= layer) {
                break;
            }

            // add vertices affected on this layer
            List<Integer> affectedOnThisLayer = affectedOnLayer.size() > layer ? affectedOnLayer
                    .get(layer) : null;
            if (affectedOnThisLayer != null) {
                for (int v : affectedOnThisLayer) {
                    markVertexAffected(v, affectedOnLayer, layer,
                            aliveAndAffected);
                }
            }

            // if affected vertex has no child -> its parent affected
            for (int i = 0; i < aliveAndAffected.size(); i++) {
                int v = aliveAndAffected.get(i);
                if (getCell(v, layer).cntChild == 0) {
                    int u = getCell(v, layer).parent;
                    markVertexAffected(u, affectedOnLayer, layer,
                            aliveAndAffected);
                }
            }

            // update remove status for affected vertices
            for (int v : aliveAndAffected) {
                shouldRemoveVertex(v, layer);
            }

            for (int i = 0; i < aliveAndAffected.size(); i++) {
                int v = aliveAndAffected.get(i);
                final Cell c = vertexStatus[v].get(layer);
                if (c.removeOnNextLayer) {
                    if (c.cntChild == 1) {
                        if (vertexStatus[c.sumChild].get(layer).newParent != c.parent) {
                            vertexStatus[c.sumChild].get(layer).newParent = c.parent;
                            markVertexAffected(c.sumChild, affectedOnLayer,
                                    layer, aliveAndAffected);
                        }
                    }
                    if (c.parent != v) {
                        final Cell parentCell = vertexStatus[c.parent]
                                .get(layer);
                        parentCell.diffCntChild--;
                        parentCell.diffSumChild -= v;
                        if (c.cntChild == 1) {
                            parentCell.diffCntChild++;
                            parentCell.diffSumChild += c.sumChild;
                        }
                        markVertexAffected(c.parent, affectedOnLayer, layer,
                                aliveAndAffected);
                    }
                }
            }

            for (int v : aliveAndAffected) {
                final Cell c = getCell(v, layer);
                if (c.removeOnNextLayer) {
                    while (vertexStatus[v].size() > layer + 1) {
                        vertexStatus[v].remove(vertexStatus[v]
                                .get(vertexStatus[v].size() - 1));
                    }
                } else {
                    nextAliveAndAffected.add(v);
                    if (vertexStatus[v].size() == layer + 1) {
                        vertexStatus[v].add(new Cell(-1));
                    }
                    final Cell newCell = getCell(v, layer + 1);
                    newCell.cntChild = c.cntChild + c.diffCntChild;
                    newCell.sumChild = c.sumChild + c.diffSumChild;
                    if (c.newParent == -1) {
                        newCell.parent = c.parent;
                    } else {
                        newCell.parent = c.newParent;
                    }
                }
            }

            layer++;

            List<Integer> tmp = aliveAndAffected;
            aliveAndAffected = nextAliveAndAffected;
            nextAliveAndAffected = tmp;
        }
    }

    public Cell getCell(int vertex, int layer) {
        if (vertexStatus[vertex].size() > layer) {
            return vertexStatus[vertex].get(layer);
        }
        return null;
    }

    public List<Cell> getCellsListForVertex(int vertex) {
        return vertexStatus[vertex];
    }

    public int getCntVertices() {
        return vertexStatus.length;
    }

    private void shouldRemoveVertex(int v, int layer) {
        final Cell c = vertexStatus[v].get(layer);
        c.removeOnNextLayer = false;
        if (c.cntChild == 0) {
            c.removeOnNextLayer = true;
        } else {
            if (c.cntChild == 1 && c.parent != v
                    && vertexStatus[c.sumChild].get(layer).cntChild > 0) {
                if (randGenerator.getRandomBit(v, layer)
                        && !randGenerator.getRandomBit(c.sumChild, layer)
                        && !randGenerator.getRandomBit(c.parent, layer)) {
                    c.removeOnNextLayer = true;
                }
            }
        }
    }

    private void markAffectedOnLayer(int vertex, int layer,
                                     final List<List<Integer>> affectedOnLayer, int curLayer,
                                     List<Integer> aliveAndAffected) {
        if (curLayer == layer) {
            markVertexAffected(vertex, affectedOnLayer, curLayer,
                    aliveAndAffected);
        }
        if (lastTimeChanged[vertex] == time) {
            return;
        }
        while (layer >= affectedOnLayer.size()) {
            affectedOnLayer.add(new ArrayList<Integer>());
        }
        List<Integer> list = affectedOnLayer.get(layer);
        list.add(vertex);
    }

    private void removeEffectOfVertex(int v,
                                      final List<List<Integer>> affectedOnLayer, int curLayer,
                                      final List<Integer> aliveAndAffected) {
        final int layer = vertexStatus[v].size() - 1;
        final Cell lastCell = vertexStatus[v].get(layer);
        if (lastCell.cntChild == 1) {
            if (vertexStatus[lastCell.sumChild].size() > layer) {
                vertexStatus[lastCell.sumChild].get(layer).newParent = -1;
                markAffectedOnLayer(lastCell.sumChild, layer, affectedOnLayer,
                        curLayer, aliveAndAffected);
            }
        }
        if (lastCell.parent != v) {
            if (vertexStatus[lastCell.parent].size() <= layer) {
                return;
            }
            final Cell parentCell = vertexStatus[lastCell.parent]
                    .get(layer);
            parentCell.diffCntChild++;
            parentCell.diffSumChild += v;
            if (lastCell.cntChild == 1) {
                parentCell.diffCntChild--;
                parentCell.diffSumChild -= lastCell.sumChild;
            }
            markAffectedOnLayer(lastCell.parent, layer, affectedOnLayer,
                    curLayer,aliveAndAffected);
        }
    }

    private void markVertexAffected(int vertex,
                                    List<List<Integer>> newAffected, int curLayer,
                                    List<Integer> aliveAndAffected) {
        if (lastTimeChanged[vertex] == time) {
            // already marked as affected
            return;
        }
        lastTimeChanged[vertex] = time;
        aliveAndAffected.add(vertex);
        removeEffectOfVertex(vertex, newAffected, curLayer,
                aliveAndAffected);
    }

}
