package dk.aau.d507e19.warehousesim.controller.pathAlgorithms.rrt;

import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;

import java.util.ArrayList;

public class RRTStar extends RRTBase {
    public RRTStar(Robot robot) {
        super(robot);
    }

    public ArrayList<GridCoordinate> generatePathFromEmpty(GridCoordinate start, GridCoordinate destination){
        super.generatePathFromEmpty(start,destination);
        improveEntirePath(allNodesMap.get(destination));
        return makePath(allNodesMap.get(destination));
    }

    public ArrayList<GridCoordinate> generatePath(GridCoordinate start, GridCoordinate destination){
        //make sure we have a path
        super.generatePath(start,destination);
        improveEntirePath(allNodesMap.get(destination));
        return makePath(allNodesMap.get(destination));
    }

    //@Override
    /*protected void growUntilPathFound(GridCoordinate destination){
        boolean foundPath = false;
        //Run until a route is found
        while (!foundPath) {
            //grow tree by one each time(maybe inefficient?)
            growRRT(root, 1);
            //every time a new node is added, check to see if we can improve it
            improvePath(latestNode.getData());
            foundPath = doesNodeExist(destination);
        }
    }*/
}
