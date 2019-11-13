package dk.aau.d507e19.warehousesim.controller.robot.plan.task;

import dk.aau.d507e19.warehousesim.controller.robot.Robot;

public class Maintenance implements Task {
    @Override
    public void perform() {

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
}
