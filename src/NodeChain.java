import java.util.ArrayList;
import java.util.List;

public class NodeChain {

    private final String targetNode;
    private final List<DijkstraAlgorithm.NodePair> chain;

    public NodeChain(String targetNode, List<DijkstraAlgorithm.NodePair> chain) {
        this.targetNode = targetNode;
        this.chain = chain;
    }

    public String getSourceNode() {
        return chain.get(0).getNode();
    }

    public String getTargetNode() {
        return targetNode;
    }

    public int nodeCount() {
        return chain.size() + 1;
    }

    public int distance() {
        return chain.get(chain.size() - 1).getDistance();
    }

    public String getNode(int index) {
        if(index > chain.size()|| index < 0)
            throw new IndexOutOfBoundsException("Index out of range 0-" + chain.size());

        if(index == chain.size()) return targetNode;
        return chain.get(index).getNode();
    }

    public List<String> getAllNodes() {
        List<String> allNodes = new ArrayList<>();
        for(DijkstraAlgorithm.NodePair pair : chain) {
            allNodes.add(pair.getNode());
        }

        allNodes.add(targetNode);
        return allNodes;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for(DijkstraAlgorithm.NodePair pair: chain) {
            builder.append(pair.getNode()).append(">");
        }
        builder.append(targetNode);
        return builder.toString();
    }
}
