import lombok.Getter;
import org.apache.commons.math3.util.MathArrays;

import java.util.Arrays;
import java.util.Random;

class NodeEmbedding {
    private final int nodeId;
    @Getter private double[] key;
    @Getter private double[] value;

    private static final Random rng = new Random();

    NodeEmbedding(int nodeId, int dimension) {
        this.nodeId = nodeId;
        this.key = initialiseEmbedding(dimension);
        this.value = initialiseEmbedding(dimension);
    }

    void updateKey(double[] update) {
        double[] updatedVector = MathArrays.ebeAdd(key, update);
        key = updatedVector;
    }

    void updateValue(double[] update) {
        double[] updatedVector = MathArrays.ebeAdd(value, update);
        value = updatedVector;
    }

    private static double[] initialiseEmbedding(int dimension) {
        double[] embedding = new double[dimension];
        for (int i = 0; i < dimension; i++) {
            embedding[i] = rng.nextDouble();
        }
        return embedding;
    }

    String printKey() {
        return Arrays.toString(key);
    }

    @Override
    public String toString() {
        return "nodeId: " + nodeId + "  key: " + Arrays.toString(key) + "  value: " + Arrays.toString(value);
    }
}
