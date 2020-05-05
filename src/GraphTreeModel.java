import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class GraphTreeModel implements TreeModel {

    private Graph graph;
    private List<TreeModelListener> treeModelListeners = new ArrayList<>();

    public GraphTreeModel(Graph graph) {
        this.graph = graph;
    }

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

    public boolean addNode(String node) {
        if (graph.addNode(node)) {
            onTreeStructuredChanged();
            return true;
        } else {
            return false;
        }
    }

    public boolean removeNode(String node) {
        if (graph.removeNode(node)) {
            onTreeStructuredChanged();
            return true;
        } else {
            return false;
        }
    }

    public boolean addLink(String nodeA, String nodeB, int distance) {
        if (graph.setEdge(nodeA, nodeB, distance)) {
            onTreeStructuredChanged();
            return true;
        } else {
            return false;
        }
    }

    public boolean removeLink(String nodeA, String nodeB) {
        if (graph.unsetEdge(nodeA, nodeB)) {
            onTreeStructuredChanged();
            return true;
        } else {
            return false;
        }
    }

    public boolean hasLink(String nodeA, String nodeB) {
        return graph.hasEdge(nodeA, nodeB);
    }

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

    public boolean loadFile(String path) {
        try {
            graph = Graph.fromFile(path);
            onTreeStructuredChanged();
            return true;
        } catch (IOException ignored) {
            return false;
        }
    }

    private void onTreeStructuredChanged() {
        TreeModelEvent e = new TreeModelEvent(this, new Object[]{getRoot()});
        for (TreeModelListener listener : treeModelListeners) {
            listener.treeStructureChanged(e);
        }
    }
}
