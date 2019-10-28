package dk.aau.d507e19.warehousesim.exception.pathExceptions;

import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.exception.pathExceptions.PathFinderException;

public class NoValidNeighborException extends PathFinderException {

    public NoValidNeighborException(Robot robot) {

        System.err.println("Robot " + robot.getRobotID()
                + " at " + robot.getApproximateGridCoordinate()
                + " has no valid neighbors.");
    }
}
