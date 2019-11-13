package dk.aau.d507e19.warehousesim.exception;

import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;

public class NextStepBlockedException extends NoPathFoundException {

    public final GridCoordinate blockedCoordinate;

    public NextStepBlockedException(GridCoordinate start, GridCoordinate dest) {
        super(start, dest, "Cannot plan path because next step is blocked");
        blockedCoordinate = dest;
    }

}
