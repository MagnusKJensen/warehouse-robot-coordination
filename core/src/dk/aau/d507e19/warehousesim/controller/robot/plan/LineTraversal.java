package dk.aau.d507e19.warehousesim.controller.robot.plan;

import dk.aau.d507e19.warehousesim.SimulationApp;
import dk.aau.d507e19.warehousesim.controller.path.Line;
import dk.aau.d507e19.warehousesim.controller.robot.*;

public class LineTraversal implements Action {

    private Robot robot;
    private Line line;

    private SpeedCalculator speedCalculator;
    private long ticksSinceStart = 0;
    private boolean doneTraversing = false;

    public LineTraversal(Robot robot, Line line) {
        this.line = line;
        this.robot = robot;
        this.speedCalculator = new SpeedCalculator(robot, line);
    }

    @Override
    public void perform() {
        if(isDone())
            throw new IllegalStateException("Attempting to perform a line traversal that is already completed");

        robot.setPosition(speedCalculator.getPositionAfter(ticksSinceStart));
        ticksSinceStart++;

        if(ticksSinceStart >= speedCalculator.getTotalTimeInTicks())
            doneTraversing = true;
    }

    @Override
    public boolean isDone() {
        return doneTraversing;
    }

    @Override
    public Status getStatus() {
        if(robot.isCarrying()) return Status.TASK_ASSIGNED_CARRYING;
        else return Status.BUSY;
    }
}
