package dk.aau.d507e19.warehousesim.exception;

import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;

public class NextStepBlockedException extends NoPathFoundException {

    public final GridCoordinate blockedCoordinate;

    public NextStepBlockedException(GridCoordinate start, GridCoordinate dest, GridCoordinate blockedCoordinate) {
        super(start, dest, "Cannot plan path because next step is blocked");
        this.blockedCoordinate = blockedCoordinate;
    }

}
