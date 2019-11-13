package dk.aau.d507e19.warehousesim.controller.robot;

import dk.aau.d507e19.warehousesim.Position;
import dk.aau.d507e19.warehousesim.storagegrid.GridBounds;

import java.util.ArrayList;
import java.util.Objects;

public class GridCoordinate {

    private int x, y;

    @Override
    public String toString() {
        return "GridCoordinate{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    public GridCoordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GridCoordinate that = (GridCoordinate) o;
        return x == that.x &&
                y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }


    public boolean isNeighbourOf(GridCoordinate potentialNeighbour) {
        boolean isConnectedHorizontally = this.x == potentialNeighbour.x + 1 || this.x == potentialNeighbour.x - 1;
        boolean isConnectedVertically = this.y == potentialNeighbour.y + 1 || this.y == potentialNeighbour.y - 1;
        return (isConnectedHorizontally && potentialNeighbour.y == this.y) ||
                (isConnectedVertically && potentialNeighbour.x == this.x);
    }

    public Position toPosition() {
        return new Position(getX(), getY());
    }

    // Works only with straight lines
    public int distanceFrom(GridCoordinate comparedCoordinate) {
        if(this.getX() != comparedCoordinate.getX() && this.getY() != comparedCoordinate.getY())
            throw new IllegalArgumentException("Coordinates must differ in only one axis");

        int distanceX = Math.abs(getX() - comparedCoordinate.getX());
        if(distanceX != 0)
            return distanceX;

        int distanceY = Math.abs(getY() - comparedCoordinate.getY());
        if(distanceY != 0)
            return distanceY;

        return 0;
    }

    public int manhattanDistanceFrom(GridCoordinate comparedCoordinate){
        // distance = abs(ydistance) + abs(xdistance)
        return Math.abs(this.getX() - comparedCoordinate.getX()) + Math.abs(this.getY() - comparedCoordinate.getY());
    }

    public ArrayList<GridCoordinate> getNeighbours(GridBounds bounds){
        ArrayList<GridCoordinate> neighbours = new ArrayList<>();
        for(Direction dir : Direction.values()){
            GridCoordinate neighbour = new GridCoordinate(this.getX() + dir.xDir, this.getY() + dir.yDir);
            if(bounds.isWithinBounds(neighbour)) neighbours.add(neighbour);
        }
        return neighbours;
    }
}
