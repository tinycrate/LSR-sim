import java.util.*;

/**
 * Java implementation of Dijkstra's algorithm
 * <p>
 * Graph are used to calculate the shortest path with Dijkstra's algorithm
 */
public class DijkstraAlgorithm implements Iterable<VisitedNodeInfo> {

    private final Graph graph;
    private final String sourceNode;

    /**
     * Use Dijkstra's algorithm with graph and a source node
     *
     * @param graph      the graph
     * @param sourceNode Source node of graph
     * @throws IllegalArgumentException Exception throws when source node does not exist in graph
     */
    public DijkstraAlgorithm(Graph graph, String sourceNode) throws IllegalArgumentException {
        if (!graph.getAllNodes().contains(sourceNode)) {
            throw new IllegalArgumentException("Source node does not exist in graph.");
        }
        this.graph = graph;
        this.sourceNode = sourceNode;
    }

    @Override
    public Iterator<VisitedNodeInfo> iterator() {
        return new NodeChainIterator();
    }

    /**
     * A pair of node and distance from the source node.
     */
    public static class NodePair {
        private final String node;
        private final int distance;

        /**
         * Create a node pair with target node and distance
         *
         * @param node     target node
         * @param distance distance between source node and target node
         */
        private NodePair(String node, int distance) {
            this.node = node;
            this.distance = distance;
        }

        /**
         * Get the node
         *
         * @return the node
         */
        public String getNode() {
            return node;
        }

        /**
         * Get the distance between the source node and target node
         *
         * @return distance
         */
        public int getDistance() {
            return distance;
        }
    }

    /**
     * Iterator for generating node chain
     */
    private class NodeChainIterator implements Iterator<VisitedNodeInfo> {

        /**
         * The chain of the path routed.
         * <p>
         * Format in Key: TargetNode, Value: NodePair(PreviousNode, TotalDistance)
         */
        private final Map<String, NodePair> map;
        private final Set<String> visitedNodes;

        private String currentNode;

        private NodeChainIterator() {
            map = new HashMap<>();
            map.put(sourceNode, new NodePair(sourceNode, 0));

            visitedNodes = new HashSet<>();

            currentNode = sourceNode;
        }

        @Override
        public boolean hasNext() {
            return visitedNodes.size() != graph.getAllNodes().size();
        }

        @Override
        public VisitedNodeInfo next() {
            visitedNodes.add(currentNode);

            // Discover next possible node
            Set<String> newDiscoveredNodes = new HashSet<>();
            Set<String> possibleNodes = graph.getEdgesOfNode(currentNode);

            possibleNodes.removeAll(visitedNodes);
            for(String posNode: possibleNodes) {
                int newDistance = map.get(currentNode).getDistance() + graph.getDistance(currentNode, posNode);
                if(!map.containsKey(posNode) || map.get(posNode).getDistance() > newDistance) {
                    map.put(posNode, new NodePair(currentNode, newDistance));
                    newDiscoveredNodes.add(posNode);
                }
            }

            // Record down the current visiting node and newly discovered nodes
            VisitedNodeInfo vni = new VisitedNodeInfo(
                    currentNode,
                    sourceNode,
                    visitedNodes,
                    newDiscoveredNodes,
                    map);


            // === Choosing the next node to be visited ===
            // Find the shortest path among the discovered nodes
            String shortestNode = null;
            Set<String> discoveredNodes = new HashSet<>(map.keySet());
            discoveredNodes.removeAll(visitedNodes);

            int shortestDistance = Integer.MAX_VALUE;
            for (String node : discoveredNodes) {
                if (map.get(node).getDistance() < shortestDistance) {
                    shortestNode = node;
                    shortestDistance = map.get(shortestNode).getDistance();
                }
            }

            // change the current node to the next visited node
            currentNode = shortestNode;
            return vni;
        }

    }
}
