import lombok.Getter;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

class RandomWalkOutput {
    @Getter private final List<Integer> nodeIds = new ArrayList<>();

    void addNodeId(int nodeId) {
        nodeIds.add(nodeId);
    }

    static List<Pair<Integer, Integer>> computePairs(List<RandomWalkOutput> walks) {
        List<Pair<Integer, Integer>> pairs = new ArrayList<>();
        for (RandomWalkOutput walk: walks) {
            List<Integer> nodeIds = walk.getNodeIds();
            for (int i = 0; i < nodeIds.size() - 1; i++) {
                pairs.add(new ImmutablePair<>(nodeIds.get(i), nodeIds.get(i + 1)));
            }
        }
        return pairs;
    }

    @Override
    public String toString() {
        return nodeIds.toString();
    }
}
