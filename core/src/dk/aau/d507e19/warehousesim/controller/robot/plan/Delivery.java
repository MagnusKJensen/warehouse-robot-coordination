package dk.aau.d507e19.warehousesim.controller.robot.plan;

import dk.aau.d507e19.warehousesim.SimulationApp;
import dk.aau.d507e19.warehousesim.TickTimer;
import dk.aau.d507e19.warehousesim.WarehouseSpecs;
import dk.aau.d507e19.warehousesim.controller.robot.Order;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;

public class Delivery implements Action {
    private Robot robot;
    private TickTimer tickTimer;

    public Delivery(Robot robot) {
        this.robot = robot;
        tickTimer = new TickTimer(WarehouseSpecs.robotPickUpSpeedInSeconds * SimulationApp.TICKS_PER_SECOND);
    }

    @Override
    public void perform() {
        tickTimer.decrement();
        if (tickTimer.isDone())
            robot.deliverBin();
    }

    @Override
    public boolean isDone() {
        return tickTimer.isDone();
    }
}
