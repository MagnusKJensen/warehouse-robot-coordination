package dk.aau.d507e19.warehousesim;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.Node;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.RRTPlanner;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import org.junit.Before;
import org.junit.Test;

import javax.print.attribute.standard.Destination;

import static org.junit.Assert.*;

public class RRTPlannerTest {
    private Node<Position> tree,oneleft,oneright,twoleft,tworight,twooneright;
    private RRTPlanner rrtPlanner = new RRTPlanner();

    @Before
    public void generateTree(){
        tree = new Node<Position>(new Position(0,0),null);
        oneleft = new Node<Position>(new Position(0,1),null);
        oneright = new Node<Position>(new Position(1,0),null);
        twoleft = new Node<Position>(new Position(0,2),null);
        tworight = new Node<Position>(new Position(2,0),null);
        twooneright = new Node<Position>(new Position(2,1),null);
        tree.addChild(oneleft);
        tree.addChild(oneright);
        oneleft.addChild(twoleft);
        oneright.addChild(tworight);
        oneright.addChild(twooneright);
    }

    @Test
    public void findNearestNeighbourTest(){
        rrtPlanner.shortestLengthNode = tree;
        assertSame(rrtPlanner.findNearestNeighbour(tree,new Position(2,3)),twooneright);
    }

    @Test
    public void generateRRTPathTest(){
        Robot robot = new Robot(new Position(0,0));
        Position dest = new Position(21,4);
        rrtPlanner.generateRRTPath(robot,dest);
    }

}
