package dk.aau.d507e19.warehousesim.exception;

import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;

public class PathFindingTimedOutException extends NoPathFoundException {

    public PathFindingTimedOutException(GridCoordinate start, GridCoordinate destination, int iterations) {
        super(start, destination,
                "Algorithm exceeded iteration limit of : " + iterations + " iterations before finding a valid path");
    }

}
