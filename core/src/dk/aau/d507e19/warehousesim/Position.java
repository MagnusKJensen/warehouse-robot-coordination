package dk.aau.d507e19.warehousesim;

import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;

import java.util.Objects;

public class    Position {

    private float x, y;

    public Position(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public boolean isSameAs(GridCoordinate gridCoordinate) {
        final float delta = 0.0001f;
        return (Math.abs(gridCoordinate.getX() - x) < delta
                && Math.abs(gridCoordinate.getY() - y) < delta);
    }

    @Override
    public String toString() {
        return "(" +
                "x=" + x +
                ", y=" + y +
                ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        if (getClass() != o.getClass()) {
            if (o.getClass() == GridCoordinate.class)
                throw new IllegalArgumentException("Can't use .equals() to compare GridCoordinate and Position" +
                        "\n Instead use isSameAs()");

            return false;
        }

        Position position = (Position) o;
        return Float.compare(position.x, x) == 0 &&
                Float.compare(position.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
