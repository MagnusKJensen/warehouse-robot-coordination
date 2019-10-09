package dk.aau.d507e19.warehousesim;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.Node;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.RRTPlanner;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class RRTPlannerTest {
    private Node<GridCoordinate> tree,oneleft,oneright,twoleft,tworight,twooneright;
    private RRTPlanner rrtPlanner = new RRTPlanner();

    @Before
    public void generateTree(){
        tree = new Node<GridCoordinate>(new GridCoordinate(0,0),null);
        oneleft = new Node<GridCoordinate>(new GridCoordinate(0,1),null);
        oneright = new Node<GridCoordinate>(new GridCoordinate(1,0),null);
        twoleft = new Node<GridCoordinate>(new GridCoordinate(0,2),null);
        tworight = new Node<GridCoordinate>(new GridCoordinate(2,0),null);
        twooneright = new Node<GridCoordinate>(new GridCoordinate(2,1),null);
        tree.addChild(oneleft);
        tree.addChild(oneright);
        oneleft.addChild(twoleft);
        oneright.addChild(tworight);
        oneright.addChild(twooneright);
        rrtPlanner.allNodesMap.put(tree.getData(),tree);
        rrtPlanner.allNodesMap.put(oneleft.getData(),oneleft);
        rrtPlanner.allNodesMap.put(oneright.getData(),oneright);
        rrtPlanner.allNodesMap.put(twoleft.getData(),twoleft);
        rrtPlanner.allNodesMap.put(tworight.getData(),tworight);
        rrtPlanner.allNodesMap.put(twooneright.getData(),twooneright);

    }
    @Test
    public void findNearestNeighbourTest(){
        rrtPlanner.shortestLengthNode = tree;
        Node<GridCoordinate> actual = rrtPlanner.findNearestNeighbour(tree,new GridCoordinate(2,3));
        assertEquals(twooneright.getData(),actual.getData());

    }
    @Test
    public void generateRRTPathTest() throws InterruptedException {
        GridCoordinate start = new GridCoordinate(0,0);
        GridCoordinate dest = new GridCoordinate(9,8);
        ArrayList<GridCoordinate> list = rrtPlanner.generateRRTPath(start,dest);
        for (GridCoordinate gc : list){
            System.out.println(gc.toString());
        }
        //todo make this test more advanced
    }

}
