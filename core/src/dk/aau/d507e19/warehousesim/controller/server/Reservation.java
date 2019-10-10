package dk.aau.d507e19.warehousesim.controller.server;

import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;

import java.sql.Time;

public class Reservation {

    private final TimeFrame timeFrame;
    private final Robot robot;
    private final GridCoordinate gridCoordinate;

    public Reservation(TimeFrame timeFrame, Robot robot, GridCoordinate gridCoordinate) {
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
}
