package dk.aau.d507e19.warehousesim;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.Node;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.RRTPlanner;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import org.junit.Before;
import org.junit.Test;

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
    }
    @Test
    public void findNearestNeighbourTest(){
        rrtPlanner.shortestLengthNode = tree;
        assertSame(rrtPlanner.findNearestNeighbour(tree,new GridCoordinate(2,3)),twooneright);
    }
    @Test
    public void generateRRTPathTest() throws InterruptedException {
        Robot robot = new Robot(new Position(0,0));
        GridCoordinate dest = new GridCoordinate(22,27);
        List<GridCoordinate> list = rrtPlanner.generateRRTPath(robot,dest);
        for(GridCoordinate gc : list){
            System.out.println(gc.toString());
        }
        //todo make this test more advanced
    }

}
