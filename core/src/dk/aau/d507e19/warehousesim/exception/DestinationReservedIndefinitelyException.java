package dk.aau.d507e19.warehousesim.exception;

import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;

public class DestinationReservedIndefinitelyException extends NoPathFoundException {
    public DestinationReservedIndefinitelyException(GridCoordinate start, GridCoordinate destination) {
        super(start, destination, "Cannot find path to destination, as it is reserved indefinitely");
    }
}
