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

        private Map<String, NodePair> possibleChains;
        private Stack<String> tempSourceNodes;
        private Stack<String> tempTargetNodes;


        private NodeChainIterator() {
            chains = new HashMap<>();
            chains.put(sourceNode, new NodePair(sourceNode, 0));

            toBeSearchNodes = graph.getAllNodes();
            toBeSearchNodes.remove(sourceNode);

            initialTempIterate();
        }

        @Override
        public boolean hasNext() {
            return !toBeSearchNodes.isEmpty();
        }

        @Override
        public NodeChain next() {
            // Get current source node and target node to check
            String target = tempTargetNodes.pop();
            String source = tempSourceNodes.peek();
            int totalDistance = chains.get(source).getDistance() + graph.getDistance(source, target);
            NodePair newPair = new NodePair(source, totalDistance);

            // Check if newly calculated target have a shorter distance
            if (!possibleChains.containsKey(target) || possibleChains.get(target).getDistance() > totalDistance) {
                possibleChains.put(target, newPair);
            }

            // While all target node from one source node has been searched
            while (tempTargetNodes.empty()) {

                // If tempSource is smaller than 1, all possible chain is marked.
                if (tempSourceNodes.size() <= 1) {

                    //Search fo the shortest path
                    String shortTarget = null;
                    int shortDistance = -1;

                    for (String currentTarget : possibleChains.keySet()) {
                        if (possibleChains.get(currentTarget).getDistance() < shortDistance) {
                            shortTarget = currentTarget;
                            shortDistance = possibleChains.get(currentTarget).getDistance();
                        }
                    }

                    // Add the shortest path to chains
                    chains.put(shortTarget, possibleChains.get(shortTarget));
                    toBeSearchNodes.remove(shortTarget);

                    // Initialize for next Iteration
                    initialTempIterate();

                } else {
                    // If tempSource have more than 1 element, that means not all possible chains has been listed
                    tempSourceNodes.pop();
                }

                // Get next set of possible target node
                for (String node : toBeSearchNodes) {
                    if (graph.hasEdge(node, tempSourceNodes.peek()))
                        tempTargetNodes.push(node);
                }
            }

            return generateChain(target, newPair);
        }

        public void initialTempIterate() {
            possibleChains = new HashMap<>();

            tempSourceNodes = new Stack<>();
            for (String node : chains.keySet()) {
                tempSourceNodes.push(node);
            }

            tempTargetNodes = new Stack<>();
            Set<String> edges = graph.getEdgesOfNode(tempSourceNodes.peek());

            for (String target : edges) {
                tempTargetNodes.push(target);
            }
        }

        private NodeChain generateChain(String target, NodePair previous) {
            List<NodePair> fullChain = new ArrayList<>();
            fullChain.add(previous);

            while (!fullChain.get(0).getNode().equals(sourceNode)) {
                previous = chains.get(fullChain.get(0).getNode());
                fullChain.add(0, previous);
            }

            return new NodeChain(target, fullChain);
        }
    }
}
