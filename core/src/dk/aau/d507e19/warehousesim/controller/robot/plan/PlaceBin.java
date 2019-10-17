package dk.aau.d507e19.warehousesim.controller.robot.plan;

import dk.aau.d507e19.warehousesim.SimulationApp;
import dk.aau.d507e19.warehousesim.TickTimer;
import dk.aau.d507e19.warehousesim.WarehouseSpecs;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.robot.Status;

public class PlaceBin implements Action {

    private Robot robot;
    private TickTimer tickTimer;

    public PlaceBin(Robot robot) {
        this.robot = robot;
        this.tickTimer = new TickTimer(WarehouseSpecs.robotPickUpSpeedInSeconds * SimulationApp.TICKS_PER_SECOND);
    }

    @Override
    public void perform() {
        tickTimer.decrement();
        if(tickTimer.isDone()) {
            robot.putDownBin();
        }
    }

    @Override
    public boolean isDone() {
        return tickTimer.isDone();
    }

    @Override
    public Status getStatus() {
        return Status.TASK_ASSIGNED_CARRYING;
    }
}
