import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathArrays;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

class NodeEmbeddingCalculator {
    private static final Logger logger = Logger.getLogger(NodeEmbeddingCalculator.class.getName());

    private final Data data;
    private final List<NodeEmbedding> nodeEmbedding;
    private final int graphSize;
    private final double learningRate;

    private final Random rng = new Random();

    NodeEmbeddingCalculator(Graph graph, int nWalks, int walkLength, int nNegativeSamples, double weightingExponent, double learningRate, int dimension) {
        this.learningRate = learningRate;
        List<RandomWalkOutput> walks = new ArrayList<>();
        for (int i = 0; i < nWalks; i++) {
            RandomWalkOutput randomWalk = graph.doRandomWalk(walkLength);
            logger.info(randomWalk.toString());
            walks.add(randomWalk);
        }
        List<Pair<Integer, Integer>> pairs = RandomWalkOutput.computePairs(walks);
        this.data = new Data(pairs, graph, nNegativeSamples, weightingExponent);
        this.nodeEmbedding = initialiseNodeEmbedding(graph, dimension);
        this.graphSize = graph.getGraphSize();
    }

    void printKeys() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < graphSize; i++) {
            builder.append("nodeId ").append(i).append(": ");
            builder.append(nodeEmbedding.get(i).printKey());
            builder.append("\n");
        }
        logger.info(builder.toString());
    }

    void writeKeys(Path outputPath) throws IOException {
        BufferedWriter br = Files.newBufferedWriter(outputPath);
        for (int i = 0; i < graphSize; i++) {
            br.write(nodeEmbedding.get(i).printKey());
            br.newLine();
        }
        br.close();
    }

    void doStochasticGradientDescent() {
        int nSteps = 0;
        while (nSteps++ < 100) { // todo convergence criterion
            Pair<Integer, Integer> positiveSample = data.samplePair();
            Collection<Integer> negativeTargetIds = data.sampleNegatives(positiveSample);
            StringBuilder builder = new StringBuilder();
            if ((nSteps % 10) == 0) {
                builder.append("iteration: " + nSteps);
                builder.append("\npositive samples: ").append(positiveSample.toString());
                builder.append("\nnegativeTargetIds: ").append(negativeTargetIds.toString());
                logger.info(builder.toString());
            }
            int keyId = positiveSample.getKey();
            doGradientStep(keyId, positiveSample.getValue(), true);
            for (int negativeValueId: negativeTargetIds) {
                doGradientStep(keyId, negativeValueId, false);
            }
        }
    }

    private void doGradientStep(int keyId, int valueId, boolean isPositive) {
        NodeEmbedding keyEmbedding = nodeEmbedding.get(keyId);
        NodeEmbedding valueEmbedding = nodeEmbedding.get(valueId);
        double logit = dotProduct(keyEmbedding.getKey(), valueEmbedding.getValue());
        double weight = 0.5 * (1.0 - FastMath.tanh(0.5 * logit));
        weight *= isPositive ? 1.0 : -1.0;
        double[] keyDerivative = scale(keyEmbedding.getKey(), learningRate * weight);
        double[] valueDerivative = scale(valueEmbedding.getValue(), learningRate * weight);
        keyEmbedding.updateKey(keyDerivative);
        valueEmbedding.updateValue(valueDerivative);
    }


    private double dotProduct(double[] list1, double[] list2) {
        return MathArrays.linearCombination(list1, list2);
    }

    private double[] scale(double[] array, double scale) {
        double[] newArray = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            newArray[i] = array[i] * scale;
        }
        return newArray;
    }

    private static List<NodeEmbedding> initialiseNodeEmbedding(Graph graph, int dimension) {
        List<NodeEmbedding> nodeEmbedding = new ArrayList<>();
        for (int nodeId = 0; nodeId < graph.getGraphSize(); nodeId++) {
            nodeEmbedding.add(new NodeEmbedding(nodeId, dimension));
        }
        return nodeEmbedding;
    }

    @Override
    public String toString() {
        return "data: " + data + "\n" +
                "nodeEmbedding: " + nodeEmbedding + "\n" +
                "graphSize: " + graphSize + "\n" +
                "learningRate: " + learningRate;
    }
}
