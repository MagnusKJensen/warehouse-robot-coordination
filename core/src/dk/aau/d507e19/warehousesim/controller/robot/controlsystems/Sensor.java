package dk.aau.d507e19.warehousesim.controller.robot.controlsystems;

import dk.aau.d507e19.warehousesim.Simulation;
import dk.aau.d507e19.warehousesim.controller.robot.RobotController;

import java.util.Random;

public abstract class Sensor {
    SensorState state;
    RobotController robotController;
    Random random;

    public Sensor(SensorState state, RobotController robotController) {
        this.state = state;
        this.robotController = robotController;
        this.random = new Random(Simulation.RANDOM_SEED + robotController.getRobot().getRobotID());
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
