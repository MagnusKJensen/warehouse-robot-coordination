package dk.aau.d507e19.warehousesim;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.Node;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class NodeTest {
    Node<Object> root,rootLeft,rootRight,rootLeftLeft,rootLeftLeftLeft,rootLeftLeftLeftLeft;

    @Before
    public void makeTree() {
        root = new Node<>(new Object(), null);
        rootLeft = new Node<>(new Object(), root);
        rootRight = new Node<>(new Object(), root);
        rootLeftLeft = new Node<>(new Object(), rootLeft);
        rootLeftLeftLeft = new Node<>(new Object(), rootLeftLeft);
        rootLeftLeftLeftLeft = new Node<>(new Object(), rootLeftLeftLeft);
    }

    @Test
    public void setParentTest(){
        //create node without a parent
        Node<Object> node1 = new Node<>(new Object(),null);
        assertNull(node1.getParent());
        //set root as node1's parent
        node1.setParent(root);
        assertTrue(root.getChildren().contains(node1));
        //set rootLeft to be parent instead
        node1.setParent(rootLeft);
        assertTrue(rootLeft.getChildren().contains(node1));
        assertFalse(root.getChildren().contains(node1));
    }

    @Test
    public void makeRootTest(){
        rootLeftLeftLeftLeft.makeRoot();
        assertNull(rootLeftLeftLeftLeft.getParent());
        //Assert that the old parent is now a child to the old child
        assertTrue(rootLeftLeftLeftLeft.getChildren().contains(rootLeftLeftLeft));
        assertEquals(rootLeftLeftLeftLeft,rootLeftLeftLeft.getParent());
        //Continue for the rest of the tree, until the old root is hit
        assertTrue(rootLeftLeftLeft.getChildren().contains(rootLeftLeft));
        assertEquals(rootLeftLeftLeft,rootLeftLeft.getParent());

        assertTrue(rootLeftLeft.getChildren().contains(rootLeft));
        assertEquals(rootLeftLeft,rootLeft.getParent());

        assertTrue(rootLeft.getChildren().contains(root));
        assertEquals(rootLeft,root.getParent());

        //Assert that parent and child is swapped
        assertFalse(rootLeftLeft.getChildren().contains(rootLeftLeftLeft));
        assertNotEquals(rootLeftLeft, rootLeftLeftLeft.getParent());


    }

}
