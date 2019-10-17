package dk.aau.d507e19.warehousesim.controller.path;

import dk.aau.d507e19.warehousesim.Position;
import dk.aau.d507e19.warehousesim.controller.robot.Direction;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;

import java.util.ArrayList;
import java.util.Objects;

public class Line {

    private final Direction direction;
    private final int length;
    private GridCoordinate start, end;

    public Line(GridCoordinate start, GridCoordinate end) {
        this.start = start;
        this.end = end;
        if(start.equals(end))
            throw new RuntimeException("A line cannot start and end at the same point");
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

    public ArrayList<GridCoordinate> toCoordinates() {
        ArrayList<GridCoordinate> coordinates = new ArrayList<>();
        for (int i = 0; i < length + 1; i++) {
            int xChange = i * direction.xDir;
            int yChange = i * direction.yDir;
            coordinates.add(new GridCoordinate(start.getX() + xChange, start.getY() + yChange));
        }
        return coordinates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return length == line.length &&
                direction == line.direction &&
                start.equals(line.start) &&
                end.equals(line.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(direction, length, start, end);
    }
}
