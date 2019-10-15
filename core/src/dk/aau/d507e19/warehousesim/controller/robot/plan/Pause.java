package dk.aau.d507e19.warehousesim.controller.robot.plan;

import dk.aau.d507e19.warehousesim.TickTimer;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.robot.Status;

public class Pause implements Action {

    private long pauseTime;
    private TickTimer timer;
    private Robot robot;

    public Pause(long pauseTime, Robot robot) {
        this.pauseTime = pauseTime;
        this.robot = robot;
        timer = new TickTimer(pauseTime);
    }

    public long getRemainingTime(){
        return timer.getRemainingTicks();
    }

    @Override
    public void perform() {
        timer.decrement();
    }

    @Override
    public boolean isDone() {
        timer.isDone();
        return false;
    }

    @Override
    public Status getStatus() {
        if(robot.isCarrying()) return Status.TASK_ASSIGNED_CARRYING;
        else return Status.BUSY;
    }
}
