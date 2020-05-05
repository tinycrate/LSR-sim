import org.junit.Test;

import java.io.IOException;

public class DijkstraAlgorithmTest {

    @Test
    public void iterationTest() throws IOException {
        Graph graph = Graph.fromFile("test-resources/sample.lsa");
        System.out.println(graph);

        DijkstraAlgorithm dAlgo = new DijkstraAlgorithm(graph, "t");

        for(NodeChain chain : dAlgo) {
            System.out.print("Destination " + chain.getTargetNode() + ": ");
            System.out.print(chain.toString());
            System.out.println(" Cost: " + chain.distance());
        }
    }
}
