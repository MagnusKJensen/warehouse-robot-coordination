package dk.aau.d507e19.warehousesim;
import dk.aau.d507e19.warehousesim.controller.path.Path;
import dk.aau.d507e19.warehousesim.controller.path.Step;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.rrt.Node;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.rrt.RRT;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class RRTTest {
    private Node<GridCoordinate> tree,oneleft,oneright, oneoneright,twoleft,tworight,twooneright;
    Robot robot = Mockito.mock(Robot.class);
    private RRT rrt;
    private ArrayList<GridCoordinate> blockedNodeList = new ArrayList<>();
    private ArrayList<GridCoordinate> nodesToBeBlocked = new ArrayList<>();
    public void generateTree(){
        rrt = new RRT(robot);
        tree = new Node<GridCoordinate>(new GridCoordinate(0,0),null, false);
        oneleft = new Node<GridCoordinate>(new GridCoordinate(0,1),null, false);
        oneright = new Node<GridCoordinate>(new GridCoordinate(1,0),null, false);
        oneoneright = new Node<GridCoordinate>(new GridCoordinate(1,1), null, false);
        twoleft = new Node<GridCoordinate>(new GridCoordinate(0,2),null, false);
        tworight = new Node<GridCoordinate>(new GridCoordinate(2,0),null, false);
        twooneright = new Node<GridCoordinate>(new GridCoordinate(2,1),null, false);

        tree.setParent(oneleft);
        tree.setParent(oneright);
        oneleft.setParent(twoleft);
        oneright.setParent(tworight);
        oneright.setParent(oneoneright);
        oneright.setParent(oneoneright);
        oneoneright.setParent(twooneright);
        rrt.allNodesMap.put(tree.getData(),tree);
        rrt.allNodesMap.put(oneleft.getData(),oneleft);
        rrt.allNodesMap.put(oneright.getData(),oneright);
        rrt.allNodesMap.put(twoleft.getData(),twoleft);
        rrt.allNodesMap.put(tworight.getData(),tworight);
        rrt.allNodesMap.put(twooneright.getData(),twooneright);
        rrt.allNodesMap.put(oneoneright.getData(), oneoneright);

    }
    @Test
    public void findNearestNeighbourTest(){
        generateTree();
        rrt.shortestLengthNode = tree;
        Node<GridCoordinate> actual = rrt.findNearestNeighbour(tree,new GridCoordinate(2,3));
        assertEquals(twooneright.getData(),actual.getData());
    }
    @Test
    public void generatePathFromEmptyTest() {
        rrt = new RRT(robot);
        GridCoordinate start = new GridCoordinate(0,0);
        GridCoordinate dest = new GridCoordinate(8,8);
        ArrayList<Step> list = rrt.generateRRTPathFromEmpty(start,dest);
        assertTrue(isValidPath(start, dest, list));
        Path p = new Path(list);

    }
    @Test
    public void generatePathTest() {
        rrt = new RRT(robot);
        //todo find a way to check that tree is actually re-used
        GridCoordinate start = new GridCoordinate(0, 0);
        GridCoordinate dest1 = new GridCoordinate(6, 8);
        GridCoordinate dest2 = new GridCoordinate(12, 7);
        ArrayList<Step> list;
        //generate initial path
        list = rrt.generateRRTPathFromEmpty(start, dest1);
        assertTrue(isValidPath(start, dest1, list));
        Path p = new Path(list);
        //generate second path
        list = rrt.generateRRTPath(dest1, dest2);
        assertTrue(isValidPath(dest1, dest2, list));
        Path p2 = new Path(list);
        //generate third path
        list = rrt.generateRRTPath(dest2, start);
        assertTrue(isValidPath(dest2, start, list));
        Path p3 = new Path(list);
    }
    @Test
    public void assignBlockedNodesTest(){
        generateTree();
        //blockedNodesList
        nodesToBeBlocked.add(oneleft.getData());
        nodesToBeBlocked.add(oneright.getData());
        nodesToBeBlocked.add(twoleft.getData());

        rrt.assignBlockedNodeStatus(nodesToBeBlocked);

        assertTrue(oneleft.getBlockedStatus());
        assertTrue(oneright.getBlockedStatus());
        assertTrue(twoleft.getBlockedStatus());

        nodesToBeBlocked.remove(oneleft.getData());
        rrt.assignBlockedNodeStatus(nodesToBeBlocked);
        assertFalse(oneleft.getBlockedStatus());

    }
    public boolean isValidPath(GridCoordinate start, GridCoordinate destination, ArrayList<Step> path){
        GridCoordinate prev = start;
        assertEquals(path.get(0).getGridCoordinate(), start);
        for(Step gc: path) {
            if(!isReachable(prev, gc.getGridCoordinate())){
                return false;
            }
            prev = gc.getGridCoordinate();        }
        return true;

    }
    public boolean isReachable(GridCoordinate prev, GridCoordinate current){
        if(prev.equals(current)){
            return true;
        }

        if(Math.sqrt(Math.pow(current.getX() - prev.getX(), 2) + Math.pow(current.getY() - prev.getY(), 2)) == 1.0){
            return true;
        }
        return false;
    }
    @Test
    public void improvePathTest(){
        when(robot.getAccelerationBinSecond()).thenReturn(WarehouseSpecs.robotAcceleration / WarehouseSpecs.binSizeInMeters);
        when(robot.getDecelerationBinSecond()).thenReturn(WarehouseSpecs.robotDeceleration / WarehouseSpecs.binSizeInMeters);
        rrt = new RRT(robot);
        Node<GridCoordinate> n0 = new Node<>(new GridCoordinate(0,0),null,false);
        Node<GridCoordinate> n1 = new Node<>(new GridCoordinate(0,1),n0,false);
        Node<GridCoordinate> n2 = new Node<>(new GridCoordinate(0,2),n1,false);
        Node<GridCoordinate> n3 = new Node<>(new GridCoordinate(1,2),n2,false);
        Node<GridCoordinate> n4 = new Node<>(new GridCoordinate(1,3),n3,false);
        Node<GridCoordinate> n5 = new Node<>(new GridCoordinate(0,3),n4,false);

        //path currently bad. If we improvepath on n5, n2 should be the new parent of n5
        rrt.root = n0;
        rrt.allNodesMap.put(n0.getData(),n0);
        rrt.allNodesMap.put(n1.getData(),n1);
        rrt.allNodesMap.put(n2.getData(),n2);
        rrt.allNodesMap.put(n3.getData(),n3);
        rrt.allNodesMap.put(n4.getData(),n4);
        rrt.allNodesMap.put(n5.getData(),n5);
        rrt.allNodesMap.put(n6.getData(),n6);
        rrt.allNodesMap.put(n7.getData(),n7);
        rrt.allNodesMap.put(n8.getData(),n8
        rrt.allNodesMap.put(n9.getData(),n9);
        rrt.allNodesMap.put(n10.getData(),n10);
        ArrayList<Step> expected = new ArrayList<>();
        expected.add(new Step(n0.getData()));
        expected.add(new Step(n9.getData()));
        expected.add(new Step(n10.getData()));
        expected.add(new Step(n8.getData()));

        rrt.improvePath(n5.getData());
        assertEquals(n2,n5.getParent());
    }
    @Test
    public void makePathTest(){
        rrt = new RRT(robot);
        Node<GridCoordinate> n0 = new Node<>(new GridCoordinate(0,0),null,false);
        Node<GridCoordinate> n1 = new Node<>(new GridCoordinate(0,1),n0,false);
        assertEquals(2,rrt.makePath(n1).size());
    }
    @Test
    public void makePathBetweenTwoNodesTest(){
        rrt = new RRT(robot);
        Node<GridCoordinate> n0 = new Node<>(new GridCoordinate(0,0),null,false);
        Node<GridCoordinate> n1 = new Node<>(new GridCoordinate(0,1),n0,false);
        Node<GridCoordinate> n2 = new Node<>(new GridCoordinate(0,2),n1,false);
        assertEquals(2,rrt.makePathBetweenTwoNodes(n1,n2).size());
    }

    @Test
    public void findNodesInRadiusTest(){
        generateTree();
        Node<GridCoordinate> currentNode = rrt.allNodesMap.get(new GridCoordinate(1,1));
        ArrayList<Node<GridCoordinate>> listOfNeighbours = rrt.findNodesInRadius(currentNode.getData(), 1);
        ArrayList<Node<GridCoordinate>> actualNeighbours = new List<>();
        assert


    }

}
