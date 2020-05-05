import java.util.*;

/**
 * Java implementation of Dijkstra's algorithm
 * <p>
 * Graph are used to calculate the shortest path with Dijkstra's algorithm
 */
public class DijkstraAlgorithm implements Iterable<NodeChain> {

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
    public Iterator<NodeChain> iterator() {
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
    private class NodeChainIterator implements Iterator<NodeChain> {

        /**
         * The chain of the path routed.
         * <p>
         * Format in Key: TargetNode, Value: NodePair(PreviousNode, TotalDistance)
         */
        private Map<String, NodePair> chains;
        private Set<String> toBeSearchNodes;

        private NodeChainIterator() {
            chains = new HashMap<>();
            chains.put(sourceNode, new NodePair(sourceNode, 0));

            toBeSearchNodes = graph.getAllNodes();
            toBeSearchNodes.remove(sourceNode);

        }

        @Override
        public boolean hasNext() {
            return !toBeSearchNodes.isEmpty();
        }

        @Override
        public NodeChain next() {
            Map<String, NodePair> possibleChains = new HashMap<>();

            // List all the possible next node step
            for(String sourceNode : chains.keySet()) {
                Set<String> possibleEdges = graph.getEdgesOfNode(sourceNode);
                possibleEdges.removeAll(chains.keySet());
                for (String currentNode : possibleEdges) {
                    int newDistance = chains.get(sourceNode).getDistance() + graph.getDistance(sourceNode, currentNode);

                    if (!possibleChains.containsKey(currentNode) || possibleChains.get(currentNode).getDistance() > newDistance) {
                        possibleChains.put(currentNode, new NodePair(sourceNode, newDistance));
                    }
                }
            }

            // Find the shortest path among the possible path
            String shortestNode = null;
            int shortestDistance = Integer.MAX_VALUE;
            for(String currentNode : possibleChains.keySet()) {
                if(possibleChains.get(currentNode).getDistance() < shortestDistance) {
                    shortestNode = currentNode;
                }
            }


            chains.put(shortestNode, possibleChains.get(shortestNode));
            toBeSearchNodes.remove(shortestNode);

            return generateChain(shortestNode);
        }

        private NodeChain generateChain(String target) {
            List<NodePair> fullChain = new ArrayList<>();
            fullChain.add(chains.get(target));

            while (!fullChain.get(0).getNode().equals(sourceNode)) {
                fullChain.add(0, chains.get(fullChain.get(0).getNode()));
            }

            return new NodeChain(target, fullChain);
        }
    }
}
