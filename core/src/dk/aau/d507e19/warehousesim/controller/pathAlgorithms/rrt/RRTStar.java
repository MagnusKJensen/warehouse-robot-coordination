package dk.aau.d507e19.warehousesim.controller.pathAlgorithms.rrt;

import dk.aau.d507e19.warehousesim.controller.path.Step;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.robot.RobotController;
import dk.aau.d507e19.warehousesim.controller.server.Server;

import java.util.ArrayList;

public class RRTStar extends RRTBase {

    public RRTStar(RobotController robotController) {
        super(robotController);
    }

    public ArrayList<Step> generatePathFromEmpty(GridCoordinate start, GridCoordinate destination){
        return super.generatePathFromEmpty(start,destination);
    }

    public ArrayList<Step> generatePath(GridCoordinate start, GridCoordinate destination){
        //make sure we have a path
        return super.generatePath(start,destination);
    }

    @Override
    protected void growUntilPathFound(GridCoordinate destination){
        //growUntilAllNodesFound();
        boolean foundPath = false;
        //Run until a route is found
        while (!foundPath) {
            //grow tree by one each time(maybe inefficient?)
            growRRT(root, 1);
            //every time a new node is added, check to see if we can improve it
            improvePath(latestNode.getData());
            //check if any of the nodes in the vicinity can be rewired to reduce their cost
            //rewire(latestNode);
            foundPath = doesNodeExist(destination);
        }
    }
}
