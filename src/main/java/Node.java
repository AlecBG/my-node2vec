import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import lombok.Getter;

public class Node {
    @Getter private final Set<Integer> connectedNodes;
    @Getter private final int nodeId;

    Node(Set<Integer> connectedNodes, int nodeId) {
        this.connectedNodes = connectedNodes;
        this.nodeId = nodeId;
    }

    List<Integer> getConnectedNodesAsList() {
        return new ArrayList<>(connectedNodes);
    }

    @Override
    public String toString() {
        return nodeId + ": " + connectedNodes;
    }
}
