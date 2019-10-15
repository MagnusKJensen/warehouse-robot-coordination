package dk.aau.d507e19.warehousesim.controller.robot.plan;

import dk.aau.d507e19.warehousesim.SimulationApp;
import dk.aau.d507e19.warehousesim.controller.robot.Direction;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;

public class LineTraversal implements Action {

    private Robot robot;
    private GridCoordinate start, destination;
    private Direction direction;

    private int totalDistance;
    private float breakingDistance;
    private float distanceTraveled;

    private SpeedCalculator speedCalculator;

    public LineTraversal(Robot robot, GridCoordinate start, GridCoordinate destination) {
        this.robot = robot;
        this.direction = getDirection(start.getX(), start.getY(),
                destination.getX(), destination.getY());
        this.totalDistance = getTotalDistance(start, destination, direction);
        this.speedCalculator = new SpeedCalculator(robot, totalDistance);
        this.breakingDistance = speedCalculator.getBreakingDistance();
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

    private int getTotalDistance(GridCoordinate startCoordinate, GridCoordinate destinationCoordinate, Direction direction) {
        if (direction == Direction.EAST || direction == Direction.WEST) {
            return Math.abs(destinationCoordinate.getX() - startCoordinate.getX());
        } else {
            return Math.abs(destinationCoordinate.getY() - startCoordinate.getY());
        }
    }

    @Override
    public void perform() {
        if (shouldAccelerate()) {
            robot.accelerate();
        } else if (shouldDecelerate()) {
            robot.decelerate();
        }

        float remainingDistance = (totalDistance - distanceTraveled);
        if (remainingDistance <= robot.getCurrentSpeed() / (float) SimulationApp.TICKS_PER_SECOND) {
            robot.move(remainingDistance * direction.xDir, remainingDistance * direction.yDir);
            distanceTraveled = totalDistance;
        } else {
            float currentSpeed = robot.getCurrentSpeed() / (float)SimulationApp.TICKS_PER_SECOND;
            robot.move(currentSpeed * direction.xDir, currentSpeed * direction.yDir);
            distanceTraveled += Math.abs(currentSpeed * direction.xDir + currentSpeed * direction.yDir);
        }
    }

    private boolean shouldDecelerate() {
        return (totalDistance - distanceTraveled) <= breakingDistance && robot.getCurrentSpeed() > robot.getMinimumSpeed();
    }

    private boolean shouldAccelerate() {
        return robot.getCurrentSpeed() < robot.getMaxSpeedBinsPerSecond() &&
                (totalDistance - distanceTraveled) > breakingDistance;
    }

    @Override
    public boolean isDone() {
        return (distanceTraveled >= (float) totalDistance); // todo Imprecise? Compare with float delta
    }
}
