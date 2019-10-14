package dk.aau.d507e19.warehousesim.controller.robot.plan;

import dk.aau.d507e19.warehousesim.TickTimer;

public class Pause implements Action {

    private long pauseTime;
    private TickTimer timer;

    public Pause(long pauseTime) {
        this.pauseTime = pauseTime;
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
}
