package dk.aau.d507e19.warehousesim;
import dk.aau.d507e19.warehousesim.controller.path.Step;
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

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.when;

public class RRTStarTest {
    Robot robot = Mockito.mock(Robot.class);
    Server server = Mockito.mock(Server.class);
    RobotController robotController = Mockito.mock(RobotController.class);
    RRT rrt;
    RRTStar rrtStar;

    @Before
    public void initiateRobotController(){
        when(robotController.getRobot()).thenReturn(robot);
        when(robotController.getServer()).thenReturn(server);
    }

    @Test
    public void generatePathTest(){
        Robot robot = Mockito.mock(Robot.class);
        when(robot.getAccelerationBinSecond()).thenReturn(WarehouseSpecs.robotAcceleration / WarehouseSpecs.binSizeInMeters);
        when(robot.getDecelerationBinSecond()).thenReturn(WarehouseSpecs.robotDeceleration / WarehouseSpecs.binSizeInMeters);
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
        Robot robot = Mockito.mock(Robot.class);
        when(robot.getAccelerationBinSecond()).thenReturn(WarehouseSpecs.robotAcceleration / WarehouseSpecs.binSizeInMeters);
        when(robot.getDecelerationBinSecond()).thenReturn(WarehouseSpecs.robotDeceleration / WarehouseSpecs.binSizeInMeters);
        RRTStar rrtStar = new RRTStar(robotController);
        GridCoordinate start = new GridCoordinate(0, 0);
        GridCoordinate dest1 = new GridCoordinate(15, 10);
        ArrayList<Step> list;
        list = rrtStar.generatePathFromEmpty(start,dest1);
        RRTTest test = new RRTTest();
        assertTrue(test.isValidPath(start,dest1,list));
    }

}
