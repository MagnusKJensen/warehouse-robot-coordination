package dk.aau.d507e19.warehousesim.controller.robot.plan.task;

import dk.aau.d507e19.warehousesim.Simulation;
import dk.aau.d507e19.warehousesim.TimeUtils;
import dk.aau.d507e19.warehousesim.WarehouseSpecs;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.robot.RobotController;
import dk.aau.d507e19.warehousesim.controller.server.order.Order;
import dk.aau.d507e19.warehousesim.storagegrid.product.Product;

import java.util.ArrayList;

public class BinDelivery implements Task {
    private Order order;
    private RobotController robotController;
    private Robot robot;
    private GridCoordinate binCoords;
    private ArrayList<Product> productsToPick;
    private int distanceForDelivery = 0;

    private ArrayList<Task> subTasks = new ArrayList<>();
    private boolean completed = false;
    private boolean isPlanned = false;

    public BinDelivery(Order order, GridCoordinate binCoords, ArrayList<Product> productsToPick) {
        this.order = order;
        this.binCoords = binCoords;
        this.productsToPick = productsToPick;
    }

    public void setRobot(Robot robot) {
        this.robotController = robot.getRobotController();
        this.robot = robot;
        planTasks();
    }

    @Override
    public boolean interrupt() {
        if (!subTasks.isEmpty()) {
           return subTasks.get(0).interrupt();
        }
        return false;
    }

    private void planTasks() {
        // Pickup
        subTasks.add(Navigation.getInstance(robotController, binCoords));
        subTasks.add(new TimedAction(() -> robot.pickUpBin(), TimeUtils.secondsToTicks(Simulation.getWarehouseSpecs().robotPickUpSpeedInSeconds)));

        // Delivery
        subTasks.add(Navigation.getInstance(robotController, order.getPicker().getGridCoordinate()));
        subTasks.add(new TimedAction(() -> robot.deliverBinToPicker(order.getPicker().getGridCoordinate(), productsToPick), TimeUtils.secondsToTicks(Simulation.getWarehouseSpecs().robotDeliverToPickInSeconds)));

        // Bin return
        subTasks.add(Navigation.getInstance(robotController, binCoords));
        subTasks.add(new TimedAction(() -> robot.putDownBin(), TimeUtils.secondsToTicks(Simulation.getWarehouseSpecs().robotPickUpSpeedInSeconds)));

        isPlanned = true;
    }

    @Override
    public void perform() {
        if (!isPlanned)
            planTasks();

        if (isCompleted())
            throw new RuntimeException("Cannot perform BinDelivery that is already completed");

        Task currentTask = subTasks.get(0);
        currentTask.perform();

        if (currentTask.isCompleted()) {
            subTasks.remove(0);

            if (subTasks.isEmpty())
                complete();
        }
    }

    private void complete() {
        completed = true;
        robotController.getServer().getReservationManager().removeBinReservation(binCoords);
        robotController.getRobot().incrementDeliveriesCompleted();
        robotController.getRobot().addToDistanceTraveled(distanceForDelivery);
    }

    @Override
    public boolean isCompleted() {
        return completed;
    }

    @Override
    public boolean hasFailed() {
        return false;
    }

    public GridCoordinate getBinCoords() {
        return binCoords;
    }

    public Order getOrder() {
        return order;
    }
}
