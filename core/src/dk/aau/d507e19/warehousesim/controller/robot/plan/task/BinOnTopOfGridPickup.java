package dk.aau.d507e19.warehousesim.controller.robot.plan.task;

import dk.aau.d507e19.warehousesim.Simulation;
import dk.aau.d507e19.warehousesim.TimeUtils;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.robot.RobotController;
import dk.aau.d507e19.warehousesim.storagegrid.product.Bin;


public class BinOnTopOfGridPickup implements Task {
    public RobotController robotController;
    Robot robot;
    GridCoordinate binLocation;
    Navigation subNavigation;
    TimedAction subPickup;
    Bin bin;
    private boolean completed = false;

    public BinOnTopOfGridPickup(RobotController robotController, GridCoordinate binLocation, Bin bin) {
        this.robotController = robotController;
        this.robot = this.robotController.getRobot();
        this.bin = bin;
        this.binLocation = binLocation;
        this.subNavigation = Navigation.getInstance(this.robotController,binLocation);
        this.subPickup = new TimedAction(() -> this.robotController.getRobot().setBin(bin), TimeUtils.secondsToTicks(Simulation.getWarehouseSpecs().robotPickUpSpeedInSeconds));
    }


    @Override
    public void perform() {
        if(!subNavigation.isCompleted()){
            subNavigation.perform();
        }else if(!subPickup.isCompleted()){
            subPickup.perform();
        }else{
            complete();
        }

    }
    @Override
    public boolean isCompleted() {
        return completed;
    }
    private void complete(){
        completed = true;
    }

    @Override
    public boolean hasFailed() {
        return false;
    }

    @Override
    public void setRobot(Robot robot) {
        this.robotController = robot.getRobotController();
        this.robot = robot;
    }

    @Override
    public boolean interrupt() {
        return false;
    }

    @Override
    public boolean canInterrupt() {
        return false;
    }
}
