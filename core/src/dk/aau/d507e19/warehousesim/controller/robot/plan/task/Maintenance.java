package dk.aau.d507e19.warehousesim.controller.robot.plan.task;

import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.robot.RobotController;
import dk.aau.d507e19.warehousesim.controller.robot.Status;
import dk.aau.d507e19.warehousesim.controller.robot.controlsystems.SensorState;
import dk.aau.d507e19.warehousesim.controller.server.Server;
import dk.aau.d507e19.warehousesim.storagegrid.ChargingTile;
import dk.aau.d507e19.warehousesim.storagegrid.MaintenanceTile;

public class Maintenance implements Task {
    private GridCoordinate destination;
    private Server server;
    private RobotController robotController;
    private boolean completed,failed;
    private Navigation navigation;
    private MaintenanceTile maintenanceTile;

    public Maintenance(RobotController robotController) {
        this.robotController = robotController;
    }

    public MaintenanceTile getMaintenanceTile() {
        return maintenanceTile;
    }

    @Override
    public void perform() {
        if(navigation==null){
            maintenanceTile = findAvailableMaintenance();
            if(maintenanceTile != null){
                destination = maintenanceTile.getGridCoordinate();
                navigation = Navigation.getInstance(robotController,destination);
                maintenanceTile.setReserved(true);
            }else return;
        }
        if(!navigation.isCompleted()){
            navigation.perform();
        }
        if (navigation.isCompleted()){
            if(robotController.getControlSystemManager().getMechanicalSensor().getState().equals(SensorState.NOMINAL)){
                complete();
            }
            if (navigation.hasFailed())
                fail();
        }
    }

    public void complete(){
        this.completed = true;
        this.robotController.getRobot().setCurrentStatus(Status.AVAILABLE);
        maintenanceTile.setReserved(false);
    }
    public void fail(){
        this.failed = true;
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
        return false;
    }

    @Override
    public boolean canInterrupt() {
        return false;
    }

    public MaintenanceTile findAvailableMaintenance(){
        for(MaintenanceTile maintenanceTile : robotController.getServer().getAvailableMaintenance()){
            //if we find one the navigate to it
            return maintenanceTile;
        }
        return null;
    }
}
