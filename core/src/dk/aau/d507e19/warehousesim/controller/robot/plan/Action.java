package dk.aau.d507e19.warehousesim.controller.robot.plan;

import dk.aau.d507e19.warehousesim.controller.robot.Status;

public interface Action {

    void perform();
    boolean isDone();
    Status getStatus();
}
