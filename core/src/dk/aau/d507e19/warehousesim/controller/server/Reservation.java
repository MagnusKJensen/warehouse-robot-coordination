package dk.aau.d507e19.warehousesim.controller.server;

import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;

public class Reservation {

    private final TimeFrame timeFrame;
    private final Robot robot;
    private final GridCoordinate gridCoordinate;

    public Reservation(Robot robot, GridCoordinate gridCoordinate, TimeFrame timeFrame) {
        this.timeFrame = timeFrame;
        this.robot = robot;
        this.gridCoordinate = gridCoordinate;
    }

    public TimeFrame getTimeFrame() {
        return timeFrame;
    }

    public Robot getRobot() {
        return robot;
    }

    public GridCoordinate getGridCoordinate() {
        return gridCoordinate;
    }

    @Override
    public String toString() {
        return "Reservation: " +
                "from " + timeFrame.getStart() + " to " + timeFrame.getEnd() +
                ", gridCoordinate = " + gridCoordinate.getX() + ", " + gridCoordinate.getY();
    }
}
