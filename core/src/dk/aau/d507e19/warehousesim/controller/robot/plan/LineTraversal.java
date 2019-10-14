package dk.aau.d507e19.warehousesim.controller.robot.plan;

import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;

public class LineTraversal implements Action {

    private Robot robot;
    private GridCoordinate start, destination;

    public LineTraversal(Robot robot, GridCoordinate start, GridCoordinate destination) {
        this.robot = robot;
        this.start = start;
        this.destination = destination;
    }

    @Override
    public void perform() {

    }

    @Override
    public boolean isDone() {

        return false;
    }
}
