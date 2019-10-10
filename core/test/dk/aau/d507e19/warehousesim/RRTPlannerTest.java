package dk.aau.d507e19.warehousesim;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.Node;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.rrt.RRTPlanner;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Path;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class RRTPlannerTest {
    private Node<GridCoordinate> tree,oneleft,oneright,twoleft,tworight,twooneright;
    private RRTPlanner rrtPlanner = new RRTPlanner();
    public void generateTree(){
        tree = new Node<GridCoordinate>(new GridCoordinate(0,0),null);
        oneleft = new Node<GridCoordinate>(new GridCoordinate(0,1),null);
        oneright = new Node<GridCoordinate>(new GridCoordinate(1,0),null);
        twoleft = new Node<GridCoordinate>(new GridCoordinate(0,2),null);
        tworight = new Node<GridCoordinate>(new GridCoordinate(2,0),null);
        twooneright = new Node<GridCoordinate>(new GridCoordinate(2,1),null);
        tree.setParent(oneleft);
        tree.setParent(oneright);
        oneleft.setParent(twoleft);
        oneright.setParent(tworight);
        oneright.setParent(twooneright);
        rrtPlanner.allNodesMap.put(tree.getData(),tree);
        rrtPlanner.allNodesMap.put(oneleft.getData(),oneleft);
        rrtPlanner.allNodesMap.put(oneright.getData(),oneright);
        rrtPlanner.allNodesMap.put(twoleft.getData(),twoleft);
        rrtPlanner.allNodesMap.put(tworight.getData(),tworight);
        rrtPlanner.allNodesMap.put(twooneright.getData(),twooneright);

    }
    @Test
    public void findNearestNeighbourTest(){
        generateTree();
        rrtPlanner.shortestLengthNode = tree;
        Node<GridCoordinate> actual = rrtPlanner.findNearestNeighbour(tree,new GridCoordinate(2,3));
        assertEquals(twooneright.getData(),actual.getData());

    }
    @Test
    public void generateRRTPathTest() {
        GridCoordinate start = new GridCoordinate(0,0);
        GridCoordinate dest = new GridCoordinate(2,2);
        ArrayList<GridCoordinate> list = rrtPlanner.generateRRTPath(start,dest);
        assertTrue(isValidPath(start, dest, list));
        Path p = new Path(list);
        //todo make this test more advanced
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

        if(rrtPlanner.getDistanceBetweenPoints(prev, current) == 1.0){
            return true;
        }
        return false;


    }



}
