package dk.aau.d507e19.warehousesim.controller.robot.controlsystems;

public abstract class Sensor {
    SensorState state;

    public SensorState getState() {
        return state;
    }

    public void setState(SensorState state) {
        this.state = state;
    }

    abstract public void update();
    abstract public void handleFailure();

}
