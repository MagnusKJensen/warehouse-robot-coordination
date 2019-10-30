package dk.aau.d507e19.warehousesim;
import dk.aau.d507e19.warehousesim.controller.path.Step;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.rrt.Node;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.rrt.RRT;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.rrt.RRTStar;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.robot.RobotController;
import dk.aau.d507e19.warehousesim.controller.server.Server;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class RRTStarTest {
    private Robot robot = Mockito.mock(Robot.class);
    private Server server = Mockito.mock(Server.class);
    private RobotController robotController = Mockito.mock(RobotController.class);


    @Before
    public void initiateRobotController(){
        when(robot.getAccelerationBinSecond()).thenReturn(WarehouseSpecs.robotAcceleration / WarehouseSpecs.binSizeInMeters);
        when(robot.getDecelerationBinSecond()).thenReturn(WarehouseSpecs.robotDeceleration / WarehouseSpecs.binSizeInMeters);
        when(robotController.getRobot()).thenReturn(robot);
        when(robotController.getServer()).thenReturn(server);
    }

    @Test
    public void generatePathTest(){
        RRTStar rrtStar = new RRTStar(robotController);
        RRT rrt = new RRT(robotController);
        GridCoordinate start = new GridCoordinate(0, 0);
        GridCoordinate dest1 = new GridCoordinate(10, 10);
        GridCoordinate dest2 = new GridCoordinate(2, 3);
        ArrayList<Step> rrtList,rrtStarList;
        //generate both paths
        rrtList = rrt.generateRRTPath(start,dest1);
        rrtStarList = rrtStar.generatePath(start,dest1);
        System.out.println(rrtList.size() + " : " + rrtStarList.size());
        //can fail if somehow random == optimized (low chance)
        assertNotEquals(rrtList,rrtStarList);
        RRTTest test = new RRTTest();
        assertTrue(test.isValidPath(start,dest1,rrtStarList));
        rrtStarList = rrtStar.generatePath(dest1,dest2);
        assertTrue(test.isValidPath(dest1,dest2,rrtStarList));
        rrtStarList = rrtStar.generatePath(dest2,start);
        assertTrue(test.isValidPath(dest2,start,rrtStarList));

        /*for (Step gc: rrtList){
            System.out.println(gc.getGridCoordinate());
        }
        for (Step gc: rrtStarList){
            System.out.println(gc.getGridCoordinate());
        }*/


    }
    @Test
    public void generatePathFromEmptyTest(){
        RRTStar rrtStar = new RRTStar(robotController);
        GridCoordinate start = new GridCoordinate(0, 0);
        GridCoordinate dest1 = new GridCoordinate(15, 10);
        ArrayList<Step> list;
        list = rrtStar.generatePathFromEmpty(start,dest1);
        RRTTest test = new RRTTest();
        assertTrue(test.isValidPath(start,dest1,list));
    }

    @Test
    public void testAttemptOptimise(){
        //create a tree
        Node<GridCoordinate> n0 = new Node<>(new GridCoordinate(1,0),null,false);
        Node<GridCoordinate> n1 = new Node<>(new GridCoordinate(2,0),n0,false);
        Node<GridCoordinate> n2 = new Node<>(new GridCoordinate(2,1),n1,false);
        Node<GridCoordinate> n3 = new Node<>(new GridCoordinate(2,2),n2,false);
        Node<GridCoordinate> n4 = new Node<>(new GridCoordinate(2,3),n3,false);
        Node<GridCoordinate> n5 = new Node<>(new GridCoordinate(1,3),n4,false);
        Node<GridCoordinate> n6 = new Node<>(new GridCoordinate(0,0),n0,false);
        Node<GridCoordinate> n7 = new Node<>(new GridCoordinate(0,1),n6,false);
        Node<GridCoordinate> n8 = new Node<>(new GridCoordinate(0,2),n7,false);
        Node<GridCoordinate> n9 = new Node<>(new GridCoordinate(1,1),n6,false);
        Node<GridCoordinate> n10 = new Node<>(new GridCoordinate(1,2),n7,false);
        RRTStar rrtStar = new RRTStar(robotController);
        rrtStar.allNodesMap.put(n0.getData(),n0);
        rrtStar.allNodesMap.put(n1.getData(),n1);
        rrtStar.allNodesMap.put(n2.getData(),n2);
        rrtStar.allNodesMap.put(n3.getData(),n3);
        rrtStar.allNodesMap.put(n4.getData(),n4);
        rrtStar.allNodesMap.put(n5.getData(),n5);
        rrtStar.allNodesMap.put(n6.getData(),n6);
        rrtStar.allNodesMap.put(n7.getData(),n7);
        rrtStar.allNodesMap.put(n8.getData(),n8);
        rrtStar.allNodesMap.put(n9.getData(),n9);
        rrtStar.allNodesMap.put(n10.getData(),n10);


        //set root and destinationNode
        rrtStar.root = n0;
        rrtStar.destinationNode = n5;
        ArrayList<Step> oldPath = rrtStar.makePath(n5);
        rrtStar.attemptOptimise();
        assertNotEquals(oldPath,rrtStar.getPath());
        /*System.out.println("OLD PATH");
        for(Step s : oldPath){
            System.out.println(s.getGridCoordinate());
        }
        System.out.println("NEW PATH");
        for(Step s : rrtStar.getPath()){
            System.out.println(s.getGridCoordinate());
        }*/
    }
    @Test
    public void testFindTurns(){
        RRTStar rrtStar = new RRTStar(robotController);
        Node<GridCoordinate> n0 = new Node<>(new GridCoordinate(0,0),null,false);
        Node<GridCoordinate> n1 = new Node<>(new GridCoordinate(0,1),null,false);
        Node<GridCoordinate> n2 = new Node<>(new GridCoordinate(1,1),null,false);
        Node<GridCoordinate> n3 = new Node<>(new GridCoordinate(2,1),null,false);
        Node<GridCoordinate> n4 = new Node<>(new GridCoordinate(2,0),null,false);
        Node<GridCoordinate> n5 = new Node<>(new GridCoordinate(3,0),null,false);
        Node<GridCoordinate> n6 = new Node<>(new GridCoordinate(3,1),null,false);
        ArrayList<Node<GridCoordinate>> path = new ArrayList<>(), turnNodes = new ArrayList<>();
        path.add(n0);
        path.add(n1);
        path.add(n2);
        path.add(n3);
        path.add(n4);
        path.add(n5);
        path.add(n6);
        turnNodes.add(n1);
        turnNodes.add(n3);
        turnNodes.add(n4);
        turnNodes.add(n5);
        ArrayList<Node<GridCoordinate>> foundTurns = rrtStar.findTurns(path);
        assertFalse(foundTurns.contains(n0));
        assertEquals(turnNodes,rrtStar.findTurns(path));
    }

}
