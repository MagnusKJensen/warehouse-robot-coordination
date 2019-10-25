package dk.aau.d507e19.warehousesim.controller.robot.plan.task;

public interface Task {

    void perform();

    boolean isCompleted();

    boolean hasFailed();

}
