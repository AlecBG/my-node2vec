import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.logging.Logger;

class Data {
    private static final Logger logger = Logger.getLogger(Data.class.getName());

    private final List<Pair<Integer, Integer>> pairs;
    private final Graph graph;
    private final int nNegativeSamples;
    @Getter private final double[] targetWeights;

    private final Random rng = new Random();

    Data(List<Pair<Integer, Integer>> pairs, Graph graph, int nNegativeSamples, double weightingExponent) {
        this.pairs = pairs;
        this.graph = graph;
        this.nNegativeSamples = nNegativeSamples;
        int[] targetCounts = computeTargetCounts(pairs, graph);
        this.targetWeights = computeTargetWeights(targetCounts, weightingExponent);
    }

    Pair<Integer, Integer> samplePair() {
        int pairId = rng.nextInt(pairs.size());
        return pairs.get(pairId);
    }

    Collection<Integer> sampleNegatives(Pair<Integer, Integer> pair) {
        Collection<Integer> negativeTargets = new ArrayList<>(nNegativeSamples);
        int sourceId = pair.getKey();
        Set<Integer> connectedNodes = graph.getNeighbourhood(sourceId);
        int sampleTargetId = sampleNodeId();
        int nSamples = Math.min(nNegativeSamples, graph.getGraphSize() - connectedNodes.size() - 1);

        while (negativeTargets.size() < nSamples) {
            if (!connectedNodes.contains(sampleTargetId)) {
                negativeTargets.add(sampleTargetId);
            }
            sampleTargetId = sampleNodeId();
        }
        return negativeTargets;
    }

    private int sampleNodeId(){
        float number = rng.nextFloat();
        int size = targetWeights.length;
        int id = size / 2 - 1;
        int below = 0;
        int above = size - 1;
        while (!(number > (id == 0 ? 0 : targetWeights[id -1]) && number < targetWeights[id])) {
            if (number > targetWeights[id]) {
                below = id;
                id = (above + 1 + id) / 2;
            } else {
                above = id;
                id = (below + id) / 2;
            }
        }
        return id;
    }

    private static double[] computeTargetWeights(int[] targetCounts, double weightingExponent) {
        double[] targetWeighting = new double[targetCounts.length];
        for (int i = 0; i < targetWeighting.length; i++) {
            targetWeighting[i] = Math.pow(targetCounts[i], weightingExponent);
        }
        normalise(targetWeighting);
        return targetWeighting;
    }

    private static int[] computeTargetCounts(List<Pair<Integer, Integer>> pairs, Graph graph) {
        int[] targetCounts = new int[graph.getGraphSize()];
        for (int i = 0; i < targetCounts.length - 1; i++) { // Add prior
            targetCounts[i] = 1;
        }
        for (Pair<Integer, Integer> pair: pairs) {
            int targetId = pair.getValue();
            targetCounts[targetId]++;
        }
        return targetCounts;
    }

    private static void normalise(double[] array) {
        for (int i = 1; i < array.length; i++) {
            array[i] += array[i - 1];
        }
        for (int i = 0; i < array.length; i++) {
            array[i] = array[i] / array[array.length - 1];
        }
    }

    @Override
    public String toString() {
           return "pairs: " + pairs;
    }
}
