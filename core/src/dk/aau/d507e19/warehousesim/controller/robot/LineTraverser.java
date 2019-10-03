package dk.aau.d507e19.warehousesim.controller.robot;

public class LineTraverser {

    private Robot robot;
    private Direction direction;
    private int totalDistance;
    private float distanceTraveled = 0f;

    public LineTraverser(GridCoordinate startCoordinate, GridCoordinate destinationCoordinate, Robot robot) {
        this.robot = robot;
        this.direction = getDirection(startCoordinate.getX(), startCoordinate.getY(),
                destinationCoordinate.getX(), destinationCoordinate.getY());
        totalDistance = getTotalDistance(startCoordinate, destinationCoordinate);
    }

    private Direction getDirection(int startX, int startY, int destinationX, int destinationY) {
        if (startX < destinationX)
            return Direction.EAST;
        if (startX > destinationX)
            return Direction.WEST;
        if (startY < destinationY)
            return Direction.NORTH;
        if (startY > destinationY)
            return Direction.SOUTH;

        throw new IllegalArgumentException("Destination coordinate must be different from start coordinate");
    }

    private int getTotalDistance(GridCoordinate startCoordinate, GridCoordinate destinationCoordinate) {
        if (direction == Direction.EAST || direction == Direction.WEST) {
            return Math.abs(destinationCoordinate.getX() - startCoordinate.getX());
        } else {
            return Math.abs(destinationCoordinate.getY() - startCoordinate.getY());
        }
    }


    public void traverse() {
        if (shouldAccelerate()) {
            robot.accelerate();
        } else if (shouldDecelerate()) {
            robot.decelerate();
        }

        float remainingDistance = (totalDistance - distanceTraveled);
        if (remainingDistance <= robot.getCurrentSpeed()) {
            robot.move(remainingDistance * direction.xDir, remainingDistance * direction.yDir);
            distanceTraveled = totalDistance;
        } else {
            robot.move(0.1f * direction.xDir, 0.1f * direction.yDir);
            distanceTraveled += 0.1f * direction.xDir + 0.1f * direction.yDir;
        }

    }


    private boolean shouldDecelerate() {
        return false;
    }

    public boolean shouldAccelerate() {
        return false;
    }

    public boolean destinationReached() {
        return (distanceTraveled >= (float) totalDistance);
    }


}
