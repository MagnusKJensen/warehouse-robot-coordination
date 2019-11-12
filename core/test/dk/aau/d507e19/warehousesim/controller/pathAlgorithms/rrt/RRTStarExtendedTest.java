package dk.aau.d507e19.warehousesim.controller.pathAlgorithms.rrt;

import dk.aau.d507e19.warehousesim.RunConfigurator;
import dk.aau.d507e19.warehousesim.Simulation;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.robot.RobotController;
import dk.aau.d507e19.warehousesim.controller.server.Server;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import static org.junit.Assert.*;

import java.util.ArrayList;

import static org.mockito.Mockito.when;

public class RRTStarExtendedTest {
    private Robot robot = Mockito.mock(Robot.class);
    private Server server = Mockito.mock(Server.class);
    private RobotController robotController = Mockito.mock(RobotController.class);


    @Before
    public void initiateRobotController(){
        RunConfigurator.setDefaultRunConfiguration();
        when(robot.getAccelerationBinSecond()).thenReturn(Simulation.getWarehouseSpecs().robotAcceleration / Simulation.getWarehouseSpecs().binSizeInMeters);
        when(robot.getDecelerationBinSecond()).thenReturn(Simulation.getWarehouseSpecs().robotDeceleration / Simulation.getWarehouseSpecs().binSizeInMeters);
        when(robotController.getRobot()).thenReturn(robot);
        when(robotController.getServer()).thenReturn(server);
    }

    @Test
    public void attemptOptimiseTest1(){
        RRTStarExtended rrtStarExtended = new RRTStarExtended(robotController);
        //make a 3x3 tree with suboptimal path 0,0 -> 0,2: (0,0) -> (1,0) -> (1,1) -> (1,2) -> (0,2)
        Node<GridCoordinate> n0 = new Node<>(new GridCoordinate(0,0),null,false);
        Node<GridCoordinate> n1 = new Node<>(new GridCoordinate(0,1),n0,false);
        Node<GridCoordinate> n3 = new Node<>(new GridCoordinate(1,0),n0,false);
        Node<GridCoordinate> n4 = new Node<>(new GridCoordinate(2,0),n3,false);
        Node<GridCoordinate> n5 = new Node<>(new GridCoordinate(1,1),n3,false);
        Node<GridCoordinate> n6 = new Node<>(new GridCoordinate(1,2),n5,false);
        Node<GridCoordinate> n7 = new Node<>(new GridCoordinate(2,1),n4,false);
        Node<GridCoordinate> n8 = new Node<>(new GridCoordinate(2,2),n7,false);
        Node<GridCoordinate> n2 = new Node<>(new GridCoordinate(0,2),n6,false);
        //add all the ndoes to allnodesmap
        rrtStarExtended.allNodesMap.put(n0.getData(),n0);
        rrtStarExtended.allNodesMap.put(n1.getData(),n1);
        rrtStarExtended.allNodesMap.put(n2.getData(),n2);
        rrtStarExtended.allNodesMap.put(n3.getData(),n3);
        rrtStarExtended.allNodesMap.put(n4.getData(),n4);
        rrtStarExtended.allNodesMap.put(n5.getData(),n5);
        rrtStarExtended.allNodesMap.put(n6.getData(),n6);
        rrtStarExtended.allNodesMap.put(n7.getData(),n7);
        rrtStarExtended.allNodesMap.put(n8.getData(),n8);
        //set root and dest
        rrtStarExtended.root=n0;
        rrtStarExtended.destinationNode=n2;
        //make the optimal path: (0,0) -> (0,1) -> (0,2)
        ArrayList<Node<GridCoordinate>> optimal = new ArrayList<>();
        optimal.add(n0);
        optimal.add(n1);
        optimal.add(n2);
        //call optimise function
        rrtStarExtended.optimalPathOptimise(n2);
        ArrayList<Node<GridCoordinate>> actual = rrtStarExtended.getNodesInPath(n2);
        assertEquals(optimal,actual);
    }
    @Test
    public void attemptOptimiseTest2(){
        RRTStarExtended rrtStarExtended = new RRTStarExtended(robotController);
        //make a 3x3 tree with suboptimal path 0,0 -> 2,2: (0,0) -> (1,0) -> (1,1) -> (2,1) -> (2,2) -> (1,2)
        Node<GridCoordinate> n0 = new Node<>(new GridCoordinate(0,0),null,false);
        Node<GridCoordinate> n1 = new Node<>(new GridCoordinate(0,1),n0,false);
        Node<GridCoordinate> n2 = new Node<>(new GridCoordinate(0,2),n1,false);
        Node<GridCoordinate> n3 = new Node<>(new GridCoordinate(1,0),n0,false);
        Node<GridCoordinate> n4 = new Node<>(new GridCoordinate(2,0),n3,false);
        Node<GridCoordinate> n5 = new Node<>(new GridCoordinate(1,1),n3,false);
        Node<GridCoordinate> n7 = new Node<>(new GridCoordinate(2,1),n5,false);
        Node<GridCoordinate> n8 = new Node<>(new GridCoordinate(2,2),n7,false);
        Node<GridCoordinate> n6 = new Node<>(new GridCoordinate(1,2),n8,false);

        //add all the ndoes to allnodesmap
        rrtStarExtended.allNodesMap.put(n0.getData(),n0);
        rrtStarExtended.allNodesMap.put(n1.getData(),n1);
        rrtStarExtended.allNodesMap.put(n2.getData(),n2);
        rrtStarExtended.allNodesMap.put(n3.getData(),n3);
        rrtStarExtended.allNodesMap.put(n4.getData(),n4);
        rrtStarExtended.allNodesMap.put(n5.getData(),n5);
        rrtStarExtended.allNodesMap.put(n6.getData(),n6);
        rrtStarExtended.allNodesMap.put(n7.getData(),n7);
        rrtStarExtended.allNodesMap.put(n8.getData(),n8);
        //set root and dest
        rrtStarExtended.root=n0;
        rrtStarExtended.destinationNode=n6;
        ArrayList<Node<GridCoordinate>> optimal = new ArrayList<>();
        optimal.add(n0);
        optimal.add(n1);
        optimal.add(n2);
        optimal.add(n6);
        rrtStarExtended.optimalPathOptimise(n6);
        ArrayList<Node<GridCoordinate>> actual = rrtStarExtended.getNodesInPath(n6);
        assertEquals(optimal,actual);
    }
}
