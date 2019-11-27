package dk.aau.d507e19.warehousesim.controller.robot.plan;

import dk.aau.d507e19.warehousesim.controller.path.Line;
import dk.aau.d507e19.warehousesim.controller.robot.*;
import dk.aau.d507e19.warehousesim.controller.robot.plan.task.Task;

import java.sql.Time;

public class LineTraversal implements Task {

    private Robot robot;
    private Line line;

    private SpeedCalculator speedCalculator;
    private long ticksSinceStart = 0;
    private boolean doneTraversing = false;
    private int distance;

    public LineTraversal(Robot robot, Line line) {
        this.line = line;
        setRobot(robot);
        this.speedCalculator = new SpeedCalculator(robot, line);
        this.distance = line.getLength();
    }

    @Override
    public void perform() {
        if(isCompleted())
            throw new IllegalStateException("Attempting to perform a line traversal that is already completed");
        ticksSinceStart++;

        robot.updatePosition(speedCalculator.getPositionAfter(ticksSinceStart),
                speedCalculator.getSpeedAfter(ticksSinceStart));

        if(ticksSinceStart >= speedCalculator.getTotalTimeInTicks()){
            doneTraversing = true;
        }
    }

    @Override
    public boolean isCompleted() {
        return doneTraversing;
    }

    @Override
    public boolean hasFailed() {
        return false;
    }

    @Override
    public void setRobot(Robot robot) {
        this.robot = robot;
    }

    @Override
    public boolean interrupt() {
        return false;
    }

    @Override
    public boolean canInterrupt() {
        return false;
    }

    public int getDistance() {
        return distance;
    }
}
