package dk.aau.d507e19.warehousesim.controller.robot;

public class Task {
    private GridCoordinate destination;
    private RoboAction roboAction;

    public Task(GridCoordinate destination, RoboAction roboAction) {
        this.destination = destination;
        this.roboAction = roboAction;
    }

    public GridCoordinate getDestination() {
        return destination;
    }

    public RoboAction getRoboAction() {
        return roboAction;
    }
}
