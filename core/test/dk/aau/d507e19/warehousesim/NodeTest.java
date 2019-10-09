package dk.aau.d507e19.warehousesim;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.Node;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class NodeTest {
    Node<GridCoordinate> root,oneleft,oneright,twoleft,twoleftleft, twoleftleftleft;


    @Before
    public void makeTree(){
        root = new Node<>(new GridCoordinate(0,0),null);
        oneleft = new Node<>(new GridCoordinate(1,0),null);
        twoleft = new Node<>(new GridCoordinate(1,1),null);
        oneright = new Node<>(new GridCoordinate(0,1),null);
        twoleftleft = new Node<>(new GridCoordinate(1,2),null);
        twoleftleftleft = new Node<>(new GridCoordinate(2,2),null);
        root.addChild(oneleft);
        root.addChild(oneright);
        oneleft.addChild(twoleft);
        twoleft.addChild(twoleftleft);
        twoleftleft.addChild(twoleftleftleft);
    }
}