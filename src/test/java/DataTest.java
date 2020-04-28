import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class DataTest {
    private Data data;

    @Before
    public void setUp() {
        List<Pair<Integer, Integer>> pairs = new ArrayList<>();
        pairs.add(new ImmutablePair<>(1,2));
        Node node0 = new Node(Collections.emptySet(), 0);
        Set<Integer> set = new HashSet<>();
        set.add(2);
        Node node1 = new Node(set, 1);
        Node node2 = new Node(Collections.emptySet(), 2);
        List<Node> nodes = new ArrayList<>();
        nodes.add(node0);
        nodes.add(node1);
        nodes.add(node2);
        Graph graph = new Graph(nodes, 1.0, 1.0);
        int nNegativeSamples = 1;
        double weightingExponent = 1.0;
        data = new Data(pairs, graph, nNegativeSamples, weightingExponent);
    }

    @Test
    public void sampleNegatives_ShouldTerminate() {
        Pair<Integer, Integer> startingPair = new ImmutablePair<>(1, 2);
        data.sampleNegatives(startingPair);
    }
}
