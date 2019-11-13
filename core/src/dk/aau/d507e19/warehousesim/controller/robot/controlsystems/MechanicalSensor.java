package dk.aau.d507e19.warehousesim.controller.robot.controlsystems;
import dk.aau.d507e19.warehousesim.Simulation;
import dk.aau.d507e19.warehousesim.controller.robot.RobotController;

public class MechanicalSensor extends Sensor {
    public MechanicalSensor(SensorState state, RobotController robotController) {
        super(state, robotController);
    }

    @Override
    public void update() {
        //todo implement chance of encountering mechanical failure

    }

    @Override
    public void handleFailure() {
        //todo implement handling of failure move to maintenance station?
    }
}
