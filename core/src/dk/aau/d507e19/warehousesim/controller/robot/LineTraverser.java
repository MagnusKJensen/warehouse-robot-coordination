package dk.aau.d507e19.warehousesim.controller.robot;

import dk.aau.d507e19.warehousesim.Simulation;
import dk.aau.d507e19.warehousesim.SimulationApp;
import dk.aau.d507e19.warehousesim.WarehouseSpecs;

public class LineTraverser {

    private Robot robot;
    private Direction direction;
    private int totalDistance;
    private float distanceTraveled = 0f;
    private float breakingDistance;

    public LineTraverser(GridCoordinate startCoordinate, GridCoordinate destinationCoordinate, Robot robot) {
        this.robot = robot;
        this.direction = getDirection(startCoordinate.getX(), startCoordinate.getY(),
                destinationCoordinate.getX(), destinationCoordinate.getY());
        totalDistance = getTotalDistance(startCoordinate, destinationCoordinate);
        this.breakingDistance = breakingDistance();
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
        // TODO: 04/10/2019 This doesn't work, if the tick rate is too low. 
        if (remainingDistance <= robot.getCurrentSpeed() / (float)SimulationApp.TICKS_PER_SECOND) {
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

    public boolean shouldAccelerate() {
        return robot.getCurrentSpeed() < robot.getMaxSpeedBinsPerSecond() &&
                (totalDistance - distanceTraveled) > breakingDistance;
    }

    public boolean destinationReached() {
        return (distanceTraveled >= (float) totalDistance);
    }

    private float calculateAchievableSpeed(){
        float achievableSpeed =
                (float) Math.sqrt((2 * robot.getAccelerationBinSecond() * robot.getDecelerationBinSecond() * totalDistance)
                /(robot.getAccelerationBinSecond() + robot.getDecelerationBinSecond()));
        if(achievableSpeed >= robot.getMaxSpeedBinsPerSecond()){
            return robot.getMaxSpeedBinsPerSecond();
        }

        return achievableSpeed;
    }

    private float breakingDistance(){
        float breakingTime = calculateAchievableSpeed() / robot.getDecelerationBinSecond();
        float breakingDistance = (float) (0.5f * robot.getDecelerationBinSecond() * Math.pow(breakingTime, 2));
        return breakingDistance;
    }

}
