package dk.aau.d507e19.warehousesim;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.rrt.Node;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.rrt.RRT;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Path;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class RRTTest {
    private Node<GridCoordinate> tree,oneleft,oneright,twoleft,tworight,twooneright;
    private RRT rrt = new RRT(null);
    public void generateTree(){
        tree = new Node<GridCoordinate>(new GridCoordinate(0,0),null, false);
        oneleft = new Node<GridCoordinate>(new GridCoordinate(0,1),null, false);
        oneright = new Node<GridCoordinate>(new GridCoordinate(1,0),null, false);
        twoleft = new Node<GridCoordinate>(new GridCoordinate(0,2),null, false);
        tworight = new Node<GridCoordinate>(new GridCoordinate(2,0),null, false);
        twooneright = new Node<GridCoordinate>(new GridCoordinate(2,1),null, false);
        tree.setParent(oneleft);
        tree.setParent(oneright);
        oneleft.setParent(twoleft);
        oneright.setParent(tworight);
        oneright.setParent(twooneright);
        rrt.allNodesMap.put(tree.getData(),tree);
        rrt.allNodesMap.put(oneleft.getData(),oneleft);
        rrt.allNodesMap.put(oneright.getData(),oneright);
        rrt.allNodesMap.put(twoleft.getData(),twoleft);
        rrt.allNodesMap.put(tworight.getData(),tworight);
        rrt.allNodesMap.put(twooneright.getData(),twooneright);

    }
    @Test
    public void findNearestNeighbourTest(){
        generateTree();
        rrt.shortestLengthNode = tree;
        Node<GridCoordinate> actual = rrt.findNearestNeighbour(tree,new GridCoordinate(2,3));
        assertEquals(twooneright.getData(),actual.getData());

    }
    @Test
    public void generateRRTPathFromEmptyTest() {
        GridCoordinate start = new GridCoordinate(0,0);
        GridCoordinate dest = new GridCoordinate(8,8);
        ArrayList<GridCoordinate> list = rrt.generateRRTPathFromEmpty(start,dest);
        assertTrue(isValidPath(start, dest, list));
        Path p = new Path(list);

    }
    @Test
    public void generateRRTPathTest() {
        //todo find a way to check that tree is actually re-used
        GridCoordinate start = new GridCoordinate(0, 0);
        GridCoordinate dest1 = new GridCoordinate(6, 8);
        GridCoordinate dest2 = new GridCoordinate(12, 7);
        ArrayList<GridCoordinate> list;
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
    public boolean isValidPath(GridCoordinate start, GridCoordinate destination, ArrayList<GridCoordinate> path){
        GridCoordinate prev = start;
        assertEquals(path.get(0), start);
        for(GridCoordinate gc: path) {
            if(!isReachable(prev, gc)){
                return false;
            }
            prev = gc;        }
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
        Node<GridCoordinate> n0 = new Node<>(new GridCoordinate(0,0),null,false);
        Node<GridCoordinate> n1 = new Node<>(new GridCoordinate(1,0),n0,false);
        Node<GridCoordinate> n2 = new Node<>(new GridCoordinate(2,0),n1,false);
        Node<GridCoordinate> n3 = new Node<>(new GridCoordinate(2,1),n2,false);
        Node<GridCoordinate> n4 = new Node<>(new GridCoordinate(1,1),n3,false);
        Node<GridCoordinate> n5 = new Node<>(new GridCoordinate(0,1),n0,false);
        Node<GridCoordinate> n6 = new Node<>(new GridCoordinate(0,2),n5,false);
        Node<GridCoordinate> n7 = new Node<>(new GridCoordinate(1,2),n4,false);
        rrt.allNodesMap.put(n0.getData(),n0);
        rrt.allNodesMap.put(n1.getData(),n1);
        rrt.allNodesMap.put(n2.getData(),n2);
        rrt.allNodesMap.put(n3.getData(),n3);
        rrt.allNodesMap.put(n4.getData(),n4);
        rrt.allNodesMap.put(n5.getData(),n5);
        rrt.allNodesMap.put(n6.getData(),n6);
        rrt.allNodesMap.put(n7.getData(),n7);
        ArrayList<GridCoordinate> list = rrt.generateRRTPath(n0.getData(),n7.getData());
        //assert that the correct route has been found
        assertEquals(list.size()-1,n7.stepsToRoot());
        rrt.improvePath(n7.getData());
        assertNotEquals(list,rrt.generateRRTPath(n0.getData(),n7.getData()));
        list = rrt.generateRRTPath(n0.getData(),n7.getData());
        assertEquals(list.size()-1,n7.stepsToRoot());
    }



}
