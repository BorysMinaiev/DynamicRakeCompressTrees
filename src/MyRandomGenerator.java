import java.util.*;

/**
 * Created by Borys Minaiev
 */
public class MyRandomGenerator {
    private final Random[] rand;
    private final List<Boolean>[] generatedBits;

    @SuppressWarnings("unchecked")
    public MyRandomGenerator(int n, int seed) {
        final Random rnd = new Random(seed);
        rand = new Random[n];
        for (int i = 0; i < rand.length; i++) {
            rand[i] = new Random(rnd.nextInt());
        }
        generatedBits = new List[n];
        for (int i = 0; i < n; i++) {
            generatedBits[i] = new ArrayList<>();
        }
    }

    public boolean getRandomBit(int vertex, int layer) {
        List<Boolean> list = generatedBits[vertex];
        while (list.size() <= layer) {
            list.add(rand[vertex].nextBoolean());
        }
        return list.get(layer);
    }
}
