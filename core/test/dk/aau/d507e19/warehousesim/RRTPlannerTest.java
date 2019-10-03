package dk.aau.d507e19.warehousesim;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.Node;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.RRTPlanner;
import org.junit.Test;
import static org.junit.Assert.*;

public class RRTPlannerTest {

    @Test
    public void findNearestNeighbourTest(){
        RRTPlanner rrtPlanner = new RRTPlanner();
        Node<Position> tree = new Node<Position>(new Position(0,0),null);
        Node<Position> oneleft = new Node<Position>(new Position(0,1),null);
        Node<Position> oneright = new Node<Position>(new Position(1,0),null);
        Node<Position> twoleft = new Node<Position>(new Position(0,2),null);
        Node<Position> tworight = new Node<Position>(new Position(2,0),null);
        Node<Position> twooneright = new Node<Position>(new Position(2,1),null);
        tree.addChild(oneleft);
        tree.addChild(oneright);
        oneleft.addChild(twoleft);
        oneright.addChild(tworight);
        oneright.addChild(twooneright);
        rrtPlanner.shortestLengthNode = tree;

        assertSame(rrtPlanner.findNearestNeighbour(tree,new Position(2,3)),twooneright);

    }

}
