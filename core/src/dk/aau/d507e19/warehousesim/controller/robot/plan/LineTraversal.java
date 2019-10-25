package dk.aau.d507e19.warehousesim.controller.robot.plan;

import dk.aau.d507e19.warehousesim.SimulationApp;
import dk.aau.d507e19.warehousesim.controller.path.Line;
import dk.aau.d507e19.warehousesim.controller.robot.*;
import dk.aau.d507e19.warehousesim.controller.robot.plan.task.Task;

public class LineTraversal implements Task {

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
        if(isCompleted())
            throw new IllegalStateException("Attempting to perform a line traversal that is already completed");

        robot.setPosition(speedCalculator.getPositionAfter(ticksSinceStart));
        ticksSinceStart++;

        if(ticksSinceStart >= speedCalculator.getTotalTimeInTicks())
            doneTraversing = true;
    }

    @Override
    public boolean isCompleted() {
        return doneTraversing;
    }

    @Override
    public boolean hasFailed() {
        return false;
    }
}
