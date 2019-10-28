package dk.aau.d507e19.warehousesim.exception;

import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;

public class NoPathFoundException extends Exception {

    private GridCoordinate start, dest;

    public NoPathFoundException(GridCoordinate start, GridCoordinate dest) {
        super("Could not find path from " + start + " to " + dest);
        this.start = start;
        this.dest = dest;
    }

    public NoPathFoundException(String message, GridCoordinate start, GridCoordinate dest) {
        super("Could not find path from " + start + " to " + dest + "\n" + message);
        this.start = start;
        this.dest = dest;
    }

    public GridCoordinate getStart() {
        return start;
    }

    public GridCoordinate getDest() {
        return dest;
    }
}
