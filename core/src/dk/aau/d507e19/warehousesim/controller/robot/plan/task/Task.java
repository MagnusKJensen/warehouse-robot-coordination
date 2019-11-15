package dk.aau.d507e19.warehousesim.controller.robot.plan.task;

import dk.aau.d507e19.warehousesim.controller.robot.Robot;

public interface Task {

    void perform();

    boolean isCompleted();

    boolean hasFailed();

    void setRobot(Robot robot);

    boolean interrupt();

    boolean canInterrupt();
}
