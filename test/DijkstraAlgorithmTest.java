import org.junit.Test;

import java.io.IOException;

public class DijkstraAlgorithmTest {

    @Test
    public void iterationTest() throws IOException {
        Graph graph = Graph.fromFile("test-resources/sample.lsa");
        System.out.println(graph);

        DijkstraAlgorithm dAlgo = new DijkstraAlgorithm(graph, "t");

        for(VisitedNodeInfo info : dAlgo) {
            System.out.print("Destination " + info.getNewVisitedNode() + ": ");

            for(String chain : info.getChain(info.getNewVisitedNode())) {
                System.out.print(chain + " ");
            }

            System.out.println(" Cost: " + info.distance(info.getNewVisitedNode()));
        }
    }
}
