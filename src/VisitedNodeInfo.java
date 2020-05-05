import java.util.*;

public class VisitedNodeInfo {

    private final String visitedNode;
    private final String sourceNode;
    private final Set<String> visitedNodes;
    private final Set<String> newDiscoverNodes;
    private final Map<String, DijkstraAlgorithm.NodePair> chains;

    public VisitedNodeInfo(
            String visitedNode, String sourceNode,
            Set<String> visitedNodes, Set<String> newDiscoverNodes,
            Map<String, DijkstraAlgorithm.NodePair> chains) {
        this.visitedNode = visitedNode;
        this.sourceNode = sourceNode;
        this.visitedNodes = visitedNodes;
        this.newDiscoverNodes = newDiscoverNodes;
        this.chains = chains;
    }

    public String getSourceNode() {
        return sourceNode;
    }

    public String getNewVisitedNode() {
        return visitedNode;
    }

    public int distance(String targetNode) {
        return chains.get(targetNode).getDistance();
    }

    public Set<String> getAllVisitedNodes() {
        return new HashSet<>(visitedNodes);
    }

    public Set<String> getNewDiscoverNodes() {
        return new HashSet<>(newDiscoverNodes);
    }

    public List<String> getChain(String targetNode) {
        List<String> chainPath = new ArrayList<>();
        chainPath.add(targetNode);

        String currentNode = targetNode;
        while(!currentNode.equals(sourceNode)) {
            currentNode = chains.get(currentNode).getNode();
            chainPath.add(0, currentNode);
        }

        return chainPath;
    }
}
