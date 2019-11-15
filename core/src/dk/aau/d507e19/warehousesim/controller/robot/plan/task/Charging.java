package dk.aau.d507e19.warehousesim.controller.robot.plan.task;

import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.robot.RobotController;
import dk.aau.d507e19.warehousesim.controller.robot.Status;
import dk.aau.d507e19.warehousesim.controller.robot.controlsystems.SensorState;
import dk.aau.d507e19.warehousesim.storagegrid.ChargingTile;

public class Charging implements Task {
    final private int moveThreshhold = 5;
    private GridCoordinate destination;
    private RobotController robotController;
    private boolean completed,failed;
    private Navigation navigation;
    private ChargingTile chargingTile;

    public Charging(RobotController robotController) {
        this.robotController = robotController;
    }

    public ChargingTile getChargingTile() {
        return chargingTile;
    }

    @Override
    public void perform() {
        if(navigation==null){
            chargingTile = findAvailableCharger();
            if(chargingTile != null){
                destination = chargingTile.getGridCoordinate();
                navigation = Navigation.getInstance(robotController,destination);
                chargingTile.setReserved(true);
            }else return;
        }
        if(!navigation.isCompleted()){
            navigation.perform();
        }
        if (navigation.isCompleted()){
            if(robotController.getControlSystemManager().getBattery().getState().equals(SensorState.NOMINAL)){
                complete();
            }
        if (navigation.hasFailed())
            fail();
        }
    }
    public void complete(){
        this.completed = true;
        this.robotController.getRobot().setCurrentStatus(Status.AVAILABLE);
        chargingTile.setReserved(false);
    }
    public void fail(){
        this.failed =true;
    }
    public ChargingTile findAvailableCharger(){
        for(ChargingTile charger : robotController.getServer().getAvailableChargers()){
            //if we find one the navigate to it?
            return charger;
        }
        return null;
    }

    @Override
    public boolean isCompleted() {
        return completed;
    }

    @Override
    public boolean hasFailed() {
        return failed;
    }

    @Override
    public void setRobot(Robot robot) {
        this.robotController = robot.getRobotController();
    }

    @Override
    public boolean interrupt() {
        return canInterrupt();
    }
    private boolean isMoving(){
        System.out.println(robotController.getRobot().getCurrentSpeed());
        if(robotController.getRobot().getCurrentSpeed()!=0)
            return true;
        return false;
    }
    @Override
    public boolean canInterrupt() {
        if(this.robotController.getControlSystemManager().getBattery().getBatteryLevel()> moveThreshhold && !this.robotController.isCharging() &&!isMoving())
            return true;

        return false;
    }
}
