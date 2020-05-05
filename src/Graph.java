import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A bidirectional Graph formed by nodes and edges.
 */
public class Graph {

    private final Map<String, Map<String, Integer>> nodes;

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
        if (hasNode(node)) return false;

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
        if (!hasNode(node)) return false;

        for (String nodeLinked : nodes.get(node).keySet().toArray(new String[0])) {
            unsetEdge(node, nodeLinked);
        }
        nodes.remove(node);
        return true;
    }

    /**
     * Set distance of an edge from one node to another, bidirectionally.
     *
     * @param nodeA    A node
     * @param nodeB    Another node
     * @param distance Distance between two edge
     * @return True if edge is set successfully; False if node(s) does not exists.
     */
    public boolean setEdge(String nodeA, String nodeB, int distance) {
        if (!hasNode(nodeA) || !hasNode(nodeB)) return false;

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
        if (!hasEdge(nodeA, nodeB)) return false;

        nodes.get(nodeA).remove(nodeB);
        nodes.get(nodeB).remove(nodeA);
        return true;
    }

    /**
     * Check if the node exists.
     *
     * @param node the node to be checked
     * @return True when node exists; otherwise false
     */
    public boolean hasNode(String node) {
        return nodes.containsKey(node);
    }

    /**
     * Check if the edge exists.
     *
     * @param nodeA A node
     * @param nodeB Another node
     * @return True if the two nodes has existing edge; otherwise false
     */
    public boolean hasEdge(String nodeA, String nodeB) {
        if (!hasNode(nodeA) || !hasNode(nodeB)) return false;

        return getEdgesOfNode(nodeA).contains(nodeB) && getEdgesOfNode(nodeB).contains(nodeA);

    }

    /**
     * Get distance between two nodes.
     *
     * @param nodeA A node
     * @param nodeB Another node
     * @return -1 if the node or edge not exists; otherwise return the distance between the two nodes.
     */
    public int getDistance(String nodeA, String nodeB) {
        if (!hasEdge(nodeA, nodeB)) return -1;

        return nodes.get(nodeA).get(nodeB);
    }

    /**
     * Get all nodes of the graph.
     *
     * @return A set of nodes.
     */
    public Set<String> getAllNodes() {
        return new HashSet<>(nodes.keySet());
    }

    /**
     * Get all linked nodes of a specific node.
     *
     * @param node A node
     * @return A set of nodes linked with the node provided; Null if the required node does not exists.
     */
    public Set<String> getEdgesOfNode(String node) {
        if (!hasNode(node)) return null;
        return new HashSet<>(nodes.get(node).keySet());
    }

    /**
     * Get a set of isolated nodes with no edges linked to.
     *
     * @return A set of isolated nodes
     */
    public Set<String> getIsolatedNodes() {
        Set<String> isoNodes = getAllNodes();
        for(String node : nodes.keySet()) {
            if(nodes.get(node).isEmpty())
                isoNodes.remove(node);
        }

        return isoNodes;
    }

    /**
     * Turn the graph into String format.
     * Usually the graph will be in LSA format.
     *
     * @return A String in LSA format
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (String node : getAllNodes()) {
            builder.append(node).append(": ");
            for (String edge : getEdgesOfNode(node)) {
                builder.append(edge).append(':').append(getDistance(node, edge)).append(' ');
            }
            builder.append('\n');
        }

        return builder.toString();
    }

    /**
     * Created a graph from a file imported.
     *
     * @param file file name
     * @return A new graph created from the file provided
     * @throws SecurityException Occurs when file do not have read permission
     * @throws IOException       Occurs when file not found / error occurs while reading / format error
     */
    public static Graph fromFile(String file) throws SecurityException, IOException {
        Graph graph = new Graph();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;

        while ((line = reader.readLine()) != null) {
            // Skip empty lines
            if (line.trim().isEmpty()) continue;

            String[] segment = line.split(" ");
            String startNode = segment[0].substring(0, segment[0].length() - 1); // Remove colon
            graph.addNode(startNode);

            for (int i = 1; i < segment.length; i++) {
                String[] link = segment[i].split(":");
                if (link.length != 2) throw new IOException("The LSA graph does not have a correct format.");

                graph.addNode(link[0]);
                graph.setEdge(startNode, link[0], Integer.parseInt(link[1]));
            }
        }
        reader.close();
        return graph;
    }

}
