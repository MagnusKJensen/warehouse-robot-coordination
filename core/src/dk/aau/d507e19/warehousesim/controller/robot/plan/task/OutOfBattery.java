package dk.aau.d507e19.warehousesim.controller.robot.plan.task;

import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.robot.RobotController;

public class OutOfBattery implements Task {
    RobotController robotController;
    EmergencyStop emergencyStop;

    public OutOfBattery(RobotController robotController) {
        this.robotController = robotController;
        this.emergencyStop = new EmergencyStop(this.robotController);
    }

    @Override
    public void perform() {
        if(!emergencyStop.isCompleted()){
            emergencyStop.perform();
        }
        //need to finish navigation to the tile we're headed towards
    }

    @Override
    public boolean isCompleted() {
        return false;
    }

    @Override
    public boolean hasFailed() {
        return false;
    }

    @Override
    public void setRobot(Robot robot) {

    }

    @Override
    public boolean interrupt() {
        return false;
    }

    @Override
    public boolean canInterrupt() {
        return false;
    }
}
