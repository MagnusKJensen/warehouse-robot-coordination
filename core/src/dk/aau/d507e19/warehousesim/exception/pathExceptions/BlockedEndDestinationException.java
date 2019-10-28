package dk.aau.d507e19.warehousesim.exception.pathExceptions;

import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.exception.pathExceptions.PathFinderException;

public class BlockedEndDestinationException extends PathFinderException {
    public BlockedEndDestinationException(Robot robot, int closedListSize) {
        System.err.println("Robot " + robot.getRobotID()
                + " at " + robot.getApproximateGridCoordinate()
                + " has a blocked end destination and could not find a path. ClosedList size: "
                + closedListSize);
    }
}
