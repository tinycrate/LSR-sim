import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * This class implements the TreeModel which is used to interact with javax's JTree
 * for interactive displaying and editing of nodes and links
 * This class bridges the gap between our `Graph` model to the JTree's model
 */
public class GraphTreeModel implements TreeModel {

    private Graph graph;
    private List<TreeModelListener> treeModelListeners = new ArrayList<>();

    /**
     * Construct the TreeModel to be used with a JTree
     *
     * @param graph The Graph structure to be displayed
     */
    public GraphTreeModel(Graph graph) {
        this.graph = graph;
    }

    /**
     * Exposed structure of the an edge of the graph (See Graph)
     * It will be displayed on the UI as a link
     */
    public static class Edge {
        public final String srcNodeName;
        public final String destNodeName;
        public final int distance;

        public Edge(String srcNodeName, String destNodeName, int distance) {
            this.srcNodeName = srcNodeName;
            this.destNodeName = destNodeName;
            this.distance = distance;
        }

        @Override
        public String toString() {
            return destNodeName + " : " + distance;
        }
    }

    /**
     * Exposed structure of the a node of the graph (See Graph)
     * It will be displayed on the UI as a router
     */
    public static class Node {
        public final String name;
        public final String[] edges;

        public Node(String name, String[] edges) {
            this.name = name;
            this.edges = edges;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    /**
     * The root of the graph as required by JTree
     * Stores all nodes in a graph for display
     * The root itself will be hidden from the UI
     */
    public static class Root {
        public final String[] nodes;

        public Root(String[] nodes) {
            this.nodes = nodes;
        }

        @Override
        public String toString() {
            return "Topology";
        }
    }

    /* The methods below will be invoked by JTree for tree display */

    @Override
    public Object getRoot() {
        Root root = new Root(graph.getAllNodes().toArray(new String[0]));
        Arrays.sort(root.nodes);
        return root;
    }

    @Override
    public Object getChild(Object parent, int index) {
        if (parent instanceof Root) {
            String nodeName = ((Root) parent).nodes[index];
            Node node = new Node(nodeName, graph.getEdgesOfNode(nodeName).toArray(new String[0]));
            Arrays.sort(node.edges);
            return node;
        }
        if (parent instanceof Node) {
            Node node = (Node) parent;
            String srcNodeName = node.name;
            String destNodeName = node.edges[index];
            return new Edge(srcNodeName, destNodeName, graph.getDistance(srcNodeName, destNodeName));
        }
        throw new UnsupportedOperationException("Get child of unsupported object");
    }

    @Override
    public int getChildCount(Object parent) {
        if (parent instanceof Root) return ((Root) parent).nodes.length;
        if (parent instanceof Node) return ((Node) parent).edges.length;
        if (parent instanceof Edge) return 0;
        throw new UnsupportedOperationException("Get child count of unsupported object");
    }

    @Override
    public boolean isLeaf(Object node) {
        return node instanceof Edge;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        throw new UnsupportedOperationException("Direct editing is not supported");
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        if (parent instanceof Root) {
            return Arrays.binarySearch(((Root) parent).nodes, ((Node) child).name);
        }
        if (parent instanceof Node) {
            return Arrays.binarySearch(((Node) parent).edges, ((Edge) child).destNodeName);
        }
        throw new UnsupportedOperationException("Get index of child of unsupported object");
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        treeModelListeners.add(l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        treeModelListeners.remove(l);
    }

    /* The methods below can be used to modify the internal graph structure of the model.
    * Modifying the internal graph structure without using the methods below will cause
    * the JTree to not be updated automatically
    * */

    /**
     * Adds a node to the internal graph structure
     *
     * @param node The name of the node
     * @return true if successful, false if a node has already existed
     */
    public boolean addNode(String node) {
        if (graph.addNode(node)) {
            onTreeStructuredChanged();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Removes a node from the internal graph structure
     *
     * @param node The name the node
     * @return true if successful, false if the node does not exist
     */
    public boolean removeNode(String node) {
        if (graph.removeNode(node)) {
            onTreeStructuredChanged();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Creates a link between two nodes in the internal graph structure
     * Any existing link will be replaced with the specified distance
     * The link is bidirectional
     *
     * @param nodeA First node
     * @param nodeB Second node
     * @param distance The distance of the nodes
     * @return true if successful, false if either node does not exist
     */
    public boolean addLink(String nodeA, String nodeB, int distance) {
        if (graph.setEdge(nodeA, nodeB, distance)) {
            onTreeStructuredChanged();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Remove a link between two nodes in the internal graph structure
     * The link is bidirectional hence it will be removed from both ends
     *
     * @param nodeA First node
     * @param nodeB Second node
     * @return true if successful, false if either node does not exist
     */
    public boolean removeLink(String nodeA, String nodeB) {
        if (graph.unsetEdge(nodeA, nodeB)) {
            onTreeStructuredChanged();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Check if a link exist between two nodes
     *
     * @param nodeA First node
     * @param nodeB Second node
     * @return true if the link exist, false otherwise
     */
    public boolean hasLink(String nodeA, String nodeB) {
        return graph.hasEdge(nodeA, nodeB);
    }

    /**
     * Calls the internal graph structure to save a graph
     *
     * @param path The path to be saved
     * @return true if successful
     */
    public boolean saveFile(String path) {
        try {
            FileWriter writer = new FileWriter(path);
            writer.write(graph.toString());
            writer.close();
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    /**
     * Calls the internal graph structure to load a graph
     *
     * @param path The path to the graph
     * @return true if successful
     */
    public boolean loadFile(String path) {
        try {
            graph = Graph.fromFile(path);
            onTreeStructuredChanged();
            return true;
        } catch (IOException ignored) {
            return false;
        }
    }

    /**
     * Replace the internal graph structure with a blank one
     */
    public void clearGraph() {
        graph = new Graph();
        onTreeStructuredChanged();
    }

    /**
     * Gets the internal graph structure
     * It should never be modified
     *
     * @return The internal Graph structure
     */
    public Graph getGraph () {
        return graph;
    }

    private void onTreeStructuredChanged() {
        TreeModelEvent e = new TreeModelEvent(this, new Object[]{getRoot()});
        for (TreeModelListener listener : treeModelListeners) {
            listener.treeStructureChanged(e);
        }
    }
}
