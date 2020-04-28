import com.google.common.collect.ImmutableSet;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

public class Graph {
    private static final Logger logger = Logger.getLogger(Graph.class.getName());

    private final List<Node> nodes;
    private final Random rng;
    private final double returnParameter;
    private final double inOutParameter;

     Graph(List<Node> nodes, double returnParameter, double inOutParameter) {
        this.nodes = nodes;
        this.returnParameter = returnParameter;
        this.inOutParameter = inOutParameter;
        this.rng = new Random();
    }

    static Graph loadFromFile(String fileName, double returnParameter, double inOutParameter) throws IOException {
        Path inputPath = Paths.get(fileName);
        BufferedReader br = Files.newBufferedReader(inputPath);
        String line;
        int lineCount = -1;
        int graphSize = 0;
        List<Node> nodes = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            lineCount++;
            if (lineCount == 0) {
                graphSize = Integer.parseInt(line);
            } else {
                int nodeId = lineCount - 1;
                if (line.equals("")) {
                    nodes.add(new Node(Collections.emptySet(), nodeId));
                    continue;
                }
                String[] nodeIds = line.split(" ");
                Integer[] connectedNodesMutable = new Integer[nodeIds.length];
                for (int i = 0; i < nodeIds.length; i++) {
                    int readNodeId = Integer.parseInt(nodeIds[i]);
                    if (readNodeId >= graphSize || readNodeId < 0) {
                        throw new IllegalArgumentException("illegal node id added. nodeid: " + nodeId
                                + "  graphSize: " + graphSize);
                    }
                    connectedNodesMutable[i] = readNodeId;
                }
                Set<Integer> connectedNodes = ImmutableSet.copyOf(connectedNodesMutable);
                nodes.add(new Node(connectedNodes, nodeId));
            }
        }
        while (nodes.size() < graphSize) {
            nodes.add(new Node(Collections.emptySet(), nodes.size()));
        }
        return new Graph(nodes, returnParameter, inOutParameter);
    }

    /*
    Assume walkLength > 0
     */
    RandomWalkOutput doRandomWalk(int walkLength) {
        int nNodesTraversed = 0;
        RandomWalkOutput randomWalkOutput = new RandomWalkOutput();

        int startingNodeId = rng.nextInt(nodes.size());
        Node startingNode = getNode(startingNodeId);
        randomWalkOutput.addNodeId(startingNodeId);
        if (startingNode.getConnectedNodes().isEmpty()) {
            return randomWalkOutput;
        }

        List<Integer> connectedNodes = startingNode.getConnectedNodesAsList();
        int currentNodeId = connectedNodes.get(rng.nextInt(connectedNodes.size()));
        Node currentNode = getNode(currentNodeId);
        randomWalkOutput.addNodeId(currentNodeId);
        nNodesTraversed++;

        Node previousNode = startingNode;
        while (!currentNode.getConnectedNodes().isEmpty() && ++nNodesTraversed <= walkLength) {
            int newNodeId = doRandomWalkStep(previousNode, currentNode);
            randomWalkOutput.addNodeId(newNodeId);
            previousNode = currentNode;
            currentNode = getNode(newNodeId);
        }
        return randomWalkOutput;
    }

    private int doRandomWalkStep(Node previousNode, Node currentNode) {
        Set<Integer> previousConnectionIds = previousNode.getConnectedNodes();
        Set<Integer> currentConnectionsIdsMinusPrevious = removeElement(currentNode.getConnectedNodes(), previousNode.getNodeId());

        List<Integer> nodesConnectedToBoth = getIntersectionAsList(currentConnectionsIdsMinusPrevious, previousConnectionIds);
        List<Integer> nodesConnectedToCurrentOnly = removeSecondFromFirstAsList(currentConnectionsIdsMinusPrevious, previousConnectionIds);
        int nConnectedBoth = nodesConnectedToBoth.size();
        int nConnectedCurrentOnly = nodesConnectedToCurrentOnly.size();

        double normalisation = 1.0 / returnParameter + nConnectedBoth + nConnectedCurrentOnly / inOutParameter;

        float prob = rng.nextFloat();
        if (prob < 1.0 / returnParameter / normalisation) {
            return previousNode.getNodeId();
        }
        if (prob < (1.0 / returnParameter + nConnectedBoth) / normalisation) {
            int i = rng.nextInt(nConnectedBoth);
            return nodesConnectedToBoth.get(i);
        }
        int i = rng.nextInt(nConnectedCurrentOnly);
        return nodesConnectedToCurrentOnly.get(i);
    }

    private Set<Integer> removeElement(Set<Integer> set, int elementToRemove) {
        Set<Integer> difference = new HashSet<>(set);
        difference.remove(elementToRemove);
        return difference;
    }

    private List<Integer> getIntersectionAsList(Set<Integer> set1, Set<Integer> set2) {
        Set<Integer> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        return new ArrayList<>(intersection);
    }

    private List<Integer> removeSecondFromFirstAsList(Set<Integer> set1, Set<Integer> set2) {
        Set<Integer> difference = new HashSet<>(set1);
        difference.removeAll(set2);
        return new ArrayList<>(difference);
    }

    private Node getNode(int nodeId) {
        return nodes.get(nodeId);
    }

    int getGraphSize() {
        return nodes.size();
    }

    Set<Integer> getNeighbourhood(int nodeId) {
        return getNode(nodeId).getConnectedNodes();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("return parameter: ").append(returnParameter).append(",  ");
        builder.append("in-out parameter: ").append(inOutParameter).append("\n");
        for (Node node: nodes) {
            builder.append(node.toString()).append("\n");
        }
        return builder.toString();
    }
}
