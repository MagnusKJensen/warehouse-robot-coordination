package dk.aau.d507e19.warehousesim.controller.robot.plan;

import dk.aau.d507e19.warehousesim.SimulationApp;
import dk.aau.d507e19.warehousesim.TickTimer;
import dk.aau.d507e19.warehousesim.WarehouseSpecs;
import dk.aau.d507e19.warehousesim.controller.robot.Order;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.robot.Status;
import dk.aau.d507e19.warehousesim.storagegrid.product.Product;

import java.util.ArrayList;

public class Delivery implements Action {
    private Robot robot;
    private TickTimer tickTimer;
    private Order order;

    public Delivery(Robot robot, Order order) {
        this.robot = robot;
        this.order = order;
        tickTimer = new TickTimer(WarehouseSpecs.robotPickUpSpeedInSeconds * SimulationApp.TICKS_PER_SECOND);
    }

    @Override
    public void perform() {
        tickTimer.decrement();
        if (tickTimer.isDone()){
            robot.deliverBin();
            removeProductsFromBin();
        }


    }

    private void removeProductsFromBin() {
        for(int i = 0; i < order.getAmount(); ++i){
            robot.getBin().removeProduct(order.getProduct());
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
