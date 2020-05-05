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
        private final Map<String, NodePair> chains;
        private final Set<String> visitedNodes;

        private String currentNode;

        private NodeChainIterator() {
            chains = new HashMap<>();
            chains.put(sourceNode, new NodePair(sourceNode, 0));

            visitedNodes = new HashSet<>();
            visitedNodes.add(sourceNode);

            currentNode = sourceNode;
        }

        @Override
        public boolean hasNext() {
            return visitedNodes.size() != graph.getAllNodes().size();
        }

        @Override
        public VisitedNodeInfo next() {
            // Discover next possible node
            Set<String> newDiscoveredNodes = new HashSet<>();
            Set<String> possibleNodes = graph.getEdgesOfNode(currentNode);

            possibleNodes.removeAll(visitedNodes);
            for(String posNode: possibleNodes) {
                int newDistance = chains.get(currentNode).getDistance() + graph.getDistance(currentNode, posNode);
                if(!chains.containsKey(posNode) || chains.get(posNode).getDistance() > newDistance) {
                    chains.put(posNode, new NodePair(currentNode, newDistance));
                    newDiscoveredNodes.add(posNode);
                }
            }

            // Find the shortest path among the discovered nodes
            String shortestNode = null;
            Set<String> discoveredNodes = new HashSet<>(chains.keySet());
            discoveredNodes.removeAll(visitedNodes);

            int shortestDistance = Integer.MAX_VALUE;
            for (String node : discoveredNodes) {
                if (chains.get(node).getDistance() < shortestDistance) {
                    shortestNode = node;
                }
            }

            // Add the shortest node as (next) visited node
            visitedNodes.add(shortestNode);

            VisitedNodeInfo vni = new VisitedNodeInfo(
                    currentNode,
                    sourceNode,
                    visitedNodes,
                    newDiscoveredNodes,
                    chains);

            // change the current node to the next visited node
            currentNode = shortestNode;
            return vni;
        }

    }
}
