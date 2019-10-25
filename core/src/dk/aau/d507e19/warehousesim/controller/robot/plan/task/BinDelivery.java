package dk.aau.d507e19.warehousesim.controller.robot.plan.task;

import dk.aau.d507e19.warehousesim.WarehouseSpecs;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.robot.RobotController;
import dk.aau.d507e19.warehousesim.controller.server.order.Order;
import dk.aau.d507e19.warehousesim.storagegrid.BinTile;

import java.util.ArrayList;

public class BinDelivery implements Task {
    private Order order;
    private RobotController robotController;
    private Robot robot;
    private GridCoordinate binCoords;

    private ArrayList<Task> subTasks = new ArrayList<>();
    private boolean completed = false;
    private boolean isPlanned = false;

    public BinDelivery(Order order, GridCoordinate binCoords) {
        this.order = order;
        this.binCoords = binCoords;
    }

    public void setRobot(Robot robot){
        this.robotController = robot.getRobotController();
        this.robot = robot;
    }

    private void planTasks() {
        // Pickup
        subTasks.add(new Navigation(robotController, binCoords));
        subTasks.add(new TimedAction(() -> robot.pickUpBin(), WarehouseSpecs.robotPickUpSpeedInSeconds));

        // Delivery
        subTasks.add(new Navigation(robotController, order.getPicker().getGridCoordinate()));
        subTasks.add(new TimedAction(() -> robot.deliverBinToPicker(), WarehouseSpecs.robotPickUpSpeedInSeconds));

        // Bin return
        subTasks.add(new Navigation(robotController, binCoords));
        subTasks.add(new TimedAction(() -> robot.putDownBin(), WarehouseSpecs.robotPickUpSpeedInSeconds));

        isPlanned = true;
    }

    @Override
    public void perform() {
        if(!isPlanned)
            planTasks();

        if(isCompleted())
            throw new RuntimeException("Cannot perform BinDelivery that is already completed");

        Task currentTask = subTasks.get(0);
        currentTask.perform();

        if(currentTask.isCompleted()){
            subTasks.remove(0);

            if(subTasks.isEmpty())
                complete();
        }
    }

    private void complete() {
        completed = true;
        robotController.getServer().getReservationManager().removeBinReservation(binCoords);
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
}
