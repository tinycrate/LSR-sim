import org.junit.Test;

import static org.junit.Assert.*;

public class GraphTest {

    @Test
    public void GraphTest() throws InterruptedException {
        Graph graph = new Graph();

        assertEquals(0, graph.getAllNodes().size());
        assertFalse(graph.hasNode("A"));
        assertFalse(graph.removeNode("A"));
        assertFalse(graph.setEdge("A", "B", 10));
        assertNull(graph.getEdgesOfNode("A"));

        graph.addNode("A");
        graph.addNode("B");

        graph.setEdge("A", "B", 10);

        assertEquals(2, graph.getAllNodes().size());
        assertEquals(10, graph.getDistance("A", "B"));
        assertEquals(1, graph.getEdgesOfNode("A").size());
        assertEquals(1, graph.getEdgesOfNode("B").size());

        graph.unsetEdge("A", "B");

        assertEquals(0, graph.getEdgesOfNode("A").size());
        assertFalse(graph.unsetEdge("A", "B"));

        graph.removeNode("B");

        assertEquals(1, graph.getAllNodes().size());
    }
}
