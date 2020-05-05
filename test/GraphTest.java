import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class GraphTest {

    @Test
    public void graphCreationTest() {
        Graph graph = new Graph();

        assertEquals(0, graph.getAllNodes().size());
        assertFalse(graph.hasNode("A"));
        assertFalse(graph.removeNode("A"));
        assertFalse(graph.setEdge("A", "B", 10));
        assertNull(graph.getEdgesOfNode("A"));

        graph.addNode("A");
        graph.addNode("B");
        graph.setEdge("A", "B", 10);
        System.out.println(graph);

        assertEquals(2, graph.getAllNodes().size());
        assertEquals(10, graph.getDistance("A", "B"));
        assertEquals(1, graph.getEdgesOfNode("A").size());
        assertEquals(1, graph.getEdgesOfNode("B").size());
        assertFalse(graph.addNode("A"));
        assertTrue(graph.hasEdge("A", "B"));

        graph.removeNode("B");

        assertEquals(0, graph.getEdgesOfNode("A").size());
        assertEquals(1, graph.getAllNodes().size());
        assertEquals(-1, graph.getDistance("A", "B"));
        assertFalse(graph.unsetEdge("A", "B"));
        assertFalse(graph.hasEdge("A", "B"));
    }

    @Test
    public void graphImportTest() throws IOException {
        Graph graph = Graph.fromFile("test-resources/sample.lsa");

        System.out.println(graph);

        assertEquals(3, graph.getEdgesOfNode("t").size());
        assertEquals(3, graph.getEdgesOfNode("u").size());
        assertEquals(5, graph.getEdgesOfNode("v").size());
        assertEquals(3, graph.getEdgesOfNode("w").size());
        assertEquals(4, graph.getEdgesOfNode("x").size());
        assertEquals(4, graph.getEdgesOfNode("y").size());
        assertEquals(2, graph.getEdgesOfNode("z").size());

        assertEquals(7, graph.getAllNodes().size());
    }
}
