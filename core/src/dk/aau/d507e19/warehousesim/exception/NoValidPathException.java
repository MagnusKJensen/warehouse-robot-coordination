package dk.aau.d507e19.warehousesim.exception;

import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;

public class NoValidPathException extends NoPathFoundException {

    public NoValidPathException(GridCoordinate start, GridCoordinate destination, String s) {
        super(start, destination, "No valid path exists to the destination\n" + s);
    }

}
