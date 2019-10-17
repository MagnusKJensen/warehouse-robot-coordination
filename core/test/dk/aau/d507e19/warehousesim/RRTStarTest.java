package dk.aau.d507e19.warehousesim;
import dk.aau.d507e19.warehousesim.controller.path.Step;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.rrt.RRT;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.rrt.RRTStar;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class RRTStarTest {
    RRTStar rrtStar = new RRTStar(null);
    RRT rrt = new RRT(null);

    @Test
    public void generatePathTest(){
        GridCoordinate start = new GridCoordinate(0, 0);
        GridCoordinate dest1 = new GridCoordinate(10, 10);
        GridCoordinate dest2 = new GridCoordinate(2, 3);
        ArrayList<Step> rrtList,rrtStarList;
        //generate both paths
        rrtList = rrt.generateRRTPath(start,dest1);
        rrtStarList = rrtStar.generatePath(start,dest1);
        //can fail if somehow random == optimized (low chance)
        assertNotEquals(rrtList,rrtStarList);
        RRTTest test = new RRTTest();
        assertTrue(test.isValidPath(start,dest1,rrtStarList));
        rrtStarList = rrtStar.generatePath(dest1,dest2);
        assertTrue(test.isValidPath(dest1,dest2,rrtStarList));
        rrtStarList = rrtStar.generatePath(dest2,start);
        assertTrue(test.isValidPath(dest2,start,rrtStarList));

        /*for (GridCoordinate gc: rrtList){
            System.out.println(gc);
        }
        for (GridCoordinate gc: rrtStarList){
            System.out.println(gc);
        }*/

    }
    @Test
    public void generatePathFromEmptyTest(){
        GridCoordinate start = new GridCoordinate(0, 0);
        GridCoordinate dest1 = new GridCoordinate(15, 10);
        ArrayList<Step> list;
        list = rrtStar.generatePathFromEmpty(start,dest1);
        RRTTest test = new RRTTest();
        assertTrue(test.isValidPath(start,dest1,list));
    }

}
