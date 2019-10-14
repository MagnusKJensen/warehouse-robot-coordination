package dk.aau.d507e19.warehousesim.controller.robot;

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
}
