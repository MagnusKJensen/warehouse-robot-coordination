package dk.aau.d507e19.warehousesim.controller.path;

import dk.aau.d507e19.warehousesim.Position;
import dk.aau.d507e19.warehousesim.controller.robot.Direction;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;

public class Line {

    private final Direction direction;
    private final int length;
    private GridCoordinate start, end;

    public Line(GridCoordinate start, GridCoordinate end) {
        this.start = start;
        this.end = end;
        this.direction = determineDirection(start, end);
        this.length = calculateLength(start, end, direction);
    }

    private static Direction determineDirection(GridCoordinate start, GridCoordinate end) {
        if (start.getX() < end.getX())
            return Direction.EAST;
        if (start.getX() > end.getX())
            return Direction.WEST;
        if (start.getY() < end.getY())
            return Direction.NORTH;
        if (start.getY() > end.getY())
            return Direction.SOUTH;

        throw new IllegalArgumentException("Destination coordinate must be different from start coordinate");
    }

    public Direction getDirection() {
        return direction;
    }

    public GridCoordinate getStart() {
        return start;
    }

    public GridCoordinate getEnd() {
        return end;
    }

    public int getLength() {
        return length;
    }

    private static int calculateLength(GridCoordinate startCoordinate, GridCoordinate destinationCoordinate, Direction direction) {
        if (direction == Direction.EAST || direction == Direction.WEST) {
            return Math.abs(destinationCoordinate.getX() - startCoordinate.getX());
        } else {
            return Math.abs(destinationCoordinate.getY() - startCoordinate.getY());
        }
    }

    public float distanceFromStart(Position position) {
        float distanceFromStart;
        if (direction == Direction.EAST || direction == Direction.WEST) {
             distanceFromStart = Math.abs(position.getX() - start.getX());
        } else {
            distanceFromStart = Math.abs(position.getY() - start.getY());
        }
        return distanceFromStart;
    }
}
