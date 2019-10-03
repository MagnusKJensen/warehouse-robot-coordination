package dk.aau.d507e19.warehousesim.controller.robot;

import dk.aau.d507e19.warehousesim.Position;

import java.util.ArrayList;

public class Task {
    private GridCoordinate destination;
    private Action action;

    public Task(GridCoordinate destination, Action action) {
        this.destination = destination;
        this.action = action;
    }

    public GridCoordinate getDestination() {
        return destination;
    }
}
