package dk.aau.d507e19.warehousesim;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.rrt.Node;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.rrt.RRT;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.rrt.RRTPlanner;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Path;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import org.junit.Test;

import java.util.ArrayList;

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
    public void generateRRTPathTest() {
        GridCoordinate start = new GridCoordinate(0,0);
        GridCoordinate dest = new GridCoordinate(8,8);
        ArrayList<GridCoordinate> list = rrt.generateRRTPath(start,dest);
        assertTrue(isValidPath(start, dest, list));
        Path p = new Path(list);

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



}
