package dk.aau.d507e19.warehousesim.controller.robot.plan;

import dk.aau.d507e19.warehousesim.SimulationApp;
import dk.aau.d507e19.warehousesim.controller.path.Line;
import dk.aau.d507e19.warehousesim.controller.robot.*;

public class LineTraversal implements Action {

    private Robot robot;
    private GridCoordinate start, destination;
    private Direction direction;

    private int totalDistance;
    private float breakingDistance;
    private float distanceTraveled;

    private SpeedCalculator speedCalculator;
    private long ticksSinceStart = 0;
    private boolean doneTraversing = false;

    public LineTraversal(Robot robot, Line line) {
        this.robot = robot;
        this.speedCalculator = new SpeedCalculator(robot, line);
        this.breakingDistance = speedCalculator.getBreakingDistance();
    }

    @Override
    public void perform() {
        if(isDone())
            throw new IllegalStateException("Attempting to perform a line traversal that is already completed");

        robot.setPosition(speedCalculator.getPositionAfter(ticksSinceStart));
        ticksSinceStart++;

        if(ticksSinceStart >= speedCalculator.getTotalTimeInTicks())
            doneTraversing = true;

        /*
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
        }*/
    }
/*
    private boolean shouldDecelerate() {
        return (totalDistance - distanceTraveled) <= breakingDistance && robot.getCurrentSpeed() > robot.getMinimumSpeed();
    }

    private boolean shouldAccelerate() {
        return robot.getCurrentSpeed() < robot.getMaxSpeedBinsPerSecond() &&
                (totalDistance - distanceTraveled) > breakingDistance;
    }*/

    @Override
    public boolean isDone() {
        return doneTraversing; //(distanceTraveled >= (float) totalDistance); // todo Imprecise? Compare with float delta
    }

    @Override
    public Status getStatus() {
        if(robot.isCarrying()) return Status.TASK_ASSIGNED_CARRYING;
        else return Status.BUSY;
    }
}
