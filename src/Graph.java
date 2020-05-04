import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Graph {


    private Map<String, Map<String, Integer>> nodes;

    /**
     * Initialize a new graph.
     */
    public Graph() {
        nodes = new HashMap<>();
    }

    /**
     * Add a new node.
     *
     * @param node Name of the new node
     * @return True if successfully added; False if node already exists.
     */
    public boolean addNode(String node) {
        if(hasNode(node)) return false;

        nodes.put(node, new HashMap<>());
        return true;
    }

    /**
     * Remove an existing node.
     *
     * @param node Name of the node to be removed
     * @return True if successfully removed; False if node does not exist.
     */
    public boolean removeNode(String node) {
        if(!hasNode(node)) return false;

        for(String nodeLinked : nodes.get(node).keySet()) {
            unsetEdge(node, nodeLinked);
        }
        nodes.remove(node);
        return true;
    }

    /**
     * Set distance of an edge from one node to another, bidirectionally.
     *
     * @param nodeA A node
     * @param nodeB Another node
     * @param distance Distance between two edge
     * @return True if edge is set successfully; False if node(s) does not exists.
     */
    public boolean setEdge(String nodeA, String nodeB, int distance) {
        if(!hasNode(nodeA) || !hasNode(nodeB)) return false;

        nodes.get(nodeA).put(nodeB, distance);
        nodes.get(nodeB).put(nodeA, distance);
        return true;
    }

    /**
     * Delete the edge of two nodes, bidirectionally.
     *
     * @param nodeA A node
     * @param nodeB Another node
     * @return True if edge is unset successfully; False if edge or node(s) does not exists.
     */
    public boolean unsetEdge(String nodeA, String nodeB) {
        if(!hasEdge(nodeA, nodeB)) return false;

        nodes.get(nodeA).remove(nodeB);
        nodes.get(nodeB).remove(nodeA);
        return true;
    }

    public boolean hasNode(String node) {
        return nodes.containsKey(node);
    }

    public boolean hasEdge(String nodeA, String nodeB) {
        if(!hasNode(nodeA) || !hasNode(nodeB)) return false;

        if(nodes.get(nodeA).containsKey(nodeB) && nodes.get(nodeB).containsKey(nodeA)) return true;

        throw new RuntimeException("Bidirectional graph has directional edge");
    }

    public static Graph fromFile(String file) throws SecurityException, IOException {
        Graph graph = new Graph();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;

        while((line = reader.readLine()) != null) {
            // Skip empty lines
            if(line.trim().isEmpty()) continue;

            String[] segment = line.split(" ");
            graph.addNode(segment[0].substring(0, segment[0].length() - 1)); // Remove colon

            for(int i = 1; i < segment.length; i++) {
                String[] link = segment[i].split(":");

                graph.addNode(link[0]);
                graph.setEdge(segment[0], segment[i], Integer.parseInt(link[1]));
            }
        }
        return graph;
    }
}
