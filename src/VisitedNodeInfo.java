import java.util.*;

/**
 * Visited node information.
 * <p>
 * This information includes the last visited node, the source node, a set of
 * visited nodes, a set of newly discovered nodes and a chain map.
 */
public class VisitedNodeInfo {

    private final String visitedNode;
    private final String sourceNode;
    private final Set<String> visitedNodes;
    private final Set<String> newDiscoverNodes;
    private final Map<String, DijkstraAlgorithm.NodePair> chains;

    /**
     * Constructor of a visited node information, with necessary node information.
     *
     * @param visitedNode      The last visited node
     * @param sourceNode       The source node
     * @param visitedNodes     A set of visited nodes
     * @param newDiscoverNodes A set of newly discovered nodes
     * @param chains           The trace-back chain map
     */
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

    /**
     * @return the source node.
     */
    public String getSourceNode() {
        return sourceNode;
    }

    /**
     * @return The last visited node
     */
    public String getNewVisitedNode() {
        return visitedNode;
    }

    /**
     * Get the distance from the source node to the target node.
     * @param targetNode the node to be checked
     * @return the distance between the source node and the target node
     */
    public int distance(String targetNode) {
        return chains.get(targetNode).getDistance();
    }

    /**
     * @return A set of visited nodes.
     */
    public Set<String> getAllVisitedNodes() {
        return new HashSet<>(visitedNodes);
    }

    /**
     * @return A set of newly discovered nodes.
     */
    public Set<String> getNewDiscoverNodes() {
        return new HashSet<>(newDiscoverNodes);
    }

    /**
     * Return a chain from source node to the target node.
     *
     * @param targetNode the node to be checked
     * @return A list of node, which is the chain in order from source node to target node.
     */
    public List<String> getChain(String targetNode) {
        List<String> chainPath = new ArrayList<>();
        chainPath.add(targetNode);

        String currentNode = targetNode;
        while (!currentNode.equals(sourceNode)) {
            currentNode = chains.get(currentNode).getNode();
            chainPath.add(0, currentNode);
        }

        return chainPath;
    }
}
