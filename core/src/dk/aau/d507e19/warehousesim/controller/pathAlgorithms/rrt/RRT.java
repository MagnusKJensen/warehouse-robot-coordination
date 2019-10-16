package dk.aau.d507e19.warehousesim.controller.pathAlgorithms.rrt;

import dk.aau.d507e19.warehousesim.WarehouseSpecs;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class RRT extends RRTBase {

    public RRT(Robot robot) {
        super(robot);
    }

    public ArrayList<GridCoordinate> generateRRTPathFromEmpty(GridCoordinate start, GridCoordinate destination) {
        return super.generatePathFromEmpty(start,destination);
    }
    public ArrayList<GridCoordinate> generateRRTPath(GridCoordinate start, GridCoordinate destination){
        return super.generatePath(start,destination);
    }
}
