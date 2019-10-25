package dk.aau.d507e19.warehousesim.controller.robot.plan.task;

import dk.aau.d507e19.warehousesim.TickTimer;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;

public class TimedAction implements Task {

    private Runnable action;
    private TickTimer tickTimer;

    public TimedAction(Runnable action, long timeDelay) {
        this.action = action;
        tickTimer = new TickTimer(timeDelay);
    }

    @Override
    public void perform() {
        tickTimer.decrement();

        if(tickTimer.isDone())
            action.run();
    }

    @Override
    public boolean isCompleted() {
        return tickTimer.isDone();
    }

    @Override
    public boolean hasFailed() {
        return false;
    }

    @Override
    public void setRobot(Robot robot) {
        // todo
    }
}
