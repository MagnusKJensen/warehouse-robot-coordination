package dk.aau.d507e19.warehousesim.controller.robot.controlsystems;

import dk.aau.d507e19.warehousesim.controller.robot.RobotController;

public abstract class Sensor {
    SensorState state;
    RobotController robotController;

    public Sensor(SensorState state, RobotController robotController) {
        this.state = state;
        this.robotController = robotController;
    }

    public SensorState getState() {
        return state;
    }

    public void setState(SensorState state) {
        this.state = state;
    }

    abstract public void update();

    abstract public void handleFailure();

}
