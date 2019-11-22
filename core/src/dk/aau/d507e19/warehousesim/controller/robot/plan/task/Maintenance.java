package dk.aau.d507e19.warehousesim.controller.robot.plan.task;

import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.robot.RobotController;
import dk.aau.d507e19.warehousesim.controller.robot.Status;
import dk.aau.d507e19.warehousesim.controller.robot.controlsystems.SensorState;
import dk.aau.d507e19.warehousesim.controller.server.Server;
import dk.aau.d507e19.warehousesim.storagegrid.ChargingTile;
import dk.aau.d507e19.warehousesim.storagegrid.MaintenanceTile;
import dk.aau.d507e19.warehousesim.storagegrid.product.Bin;

import java.util.ArrayList;
import java.util.Iterator;

public class Maintenance implements Task {
    private GridCoordinate destination;
    private Server server;
    private RobotController robotController;
    private boolean completed, failed, hasReAssignedTasks = false;
    private Navigation navigation;
    private MaintenanceTile maintenanceTile;
    private EmergencyStop emergencyStop;

    public Maintenance(RobotController robotController) {
        this.robotController = robotController;
        this.emergencyStop = new EmergencyStop(this.robotController);
        if(robotController.getRobot().getRobotID()==6){
            System.out.println();
        }

    }

    public MaintenanceTile getMaintenanceTile() {
        return maintenanceTile;
    }

    @Override
    public void perform() {
        if (emergencyStop.isCompleted()) {
            if(!hasReAssignedTasks){
                handleOldTasks();
                hasReAssignedTasks = true;
            }

            if (navigation == null) {
                maintenanceTile = findAvailableMaintenance();
                if (maintenanceTile != null) {
                    destination = maintenanceTile.getGridCoordinate();
                    navigation = Navigation.getInstance(robotController, destination);
                    maintenanceTile.setReserved(true);
                } else return;
            }
            if (!navigation.isCompleted()) {
                navigation.perform();
            }
            if (navigation.isCompleted()) {
                if (robotController.getControlSystemManager().getMechanicalSensor().getState().equals(SensorState.NOMINAL)) {
                    complete();
                }
                if (navigation.hasFailed())
                    fail();
            }
        //if emergency stop is not completed we perform it
        } else {
            emergencyStop.perform();
        }
    }

    public void complete() {
        this.completed = true;
        this.robotController.getRobot().setCurrentStatus(Status.AVAILABLE);
        maintenanceTile.setReserved(false);
    }

    public void fail() {
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

    public MaintenanceTile findAvailableMaintenance() {
        for (MaintenanceTile maintenanceTile : robotController.getServer().getAvailableMaintenance()) {
            //if we find one the navigate to it
            return maintenanceTile;
        }
        return null;
    }
    public void handleOldTasks(){
        Iterator<Task> iterator = robotController.getTasks().iterator();
        ArrayList<Task> tasksToRemove = new ArrayList<>();
        while(iterator.hasNext()){
            Task t = iterator.next();
            if(t instanceof BinDelivery){
                tasksToRemove.add(t);
                handleBinDelivery((BinDelivery) t);

            }else if(t instanceof Charging){
                ((Charging) t).resetNavigation();
            }
        }
        robotController.getTasks().removeAll(tasksToRemove);
    }
    private void handleBinDelivery(BinDelivery binDelivery){
        Robot replacementBot = findRobotToPerformTask();
        if(!robotController.getRobot().isCarrying()){
            //if interrupted during navigation we can just give the BinDelivery to the replacement bot
            if(containsBinOnTopOfGridPickup(binDelivery.getSubTasks())){
                BinOnTopOfGridPickup old = getBinOnTopOffGridPickup(binDelivery.getSubTasks());
                BinOnTopOfGridPickup updated = new BinOnTopOfGridPickup(replacementBot.getRobotController(),old.binLocation,old.bin);
                binDelivery.clearAllSubTasks();
                binDelivery.getSubTasks().add(updated);
                binDelivery.setRobot(replacementBot);
                removePickUpTask(binDelivery);
                reAssignTask(binDelivery,replacementBot);

            }else{
                binDelivery.clearAllSubTasks();
                binDelivery.setRobot(replacementBot);
                reAssignTask(binDelivery,replacementBot);
            }

        }else{
            //todo @bau implement proper handling when a robot emergency stops while its carrying a bin
            //we are carrying a bin right now, meaning we need to drop it and give it to someone else
            Bin bin = robotController.getRobot().getBin();
            robotController.getRobot().ignorantPutDownBin();
            binDelivery.clearAllSubTasks();
            binDelivery.setRobot(replacementBot);
            binDelivery.getSubTasks().add(0,new BinOnTopOfGridPickup(replacementBot.getRobotController(),this.robotController.getRobot().getApproximateGridCoordinate(),bin));
            removePickUpTask(binDelivery);
            reAssignTask(binDelivery,replacementBot);
        }

    }
    private void removePickUpTask(BinDelivery delivery){
        Iterator<Task> iterator = delivery.getSubTasks().listIterator();
        int i =0;
        while (iterator.hasNext()){
            if(iterator.next() instanceof TimedAction){
                delivery.getSubTasks().remove(i);
                delivery.getSubTasks().remove(i-1);
                break;
            }
            i++;
        }
    }
    private void reAssignTask(BinDelivery task, Robot replacementRobot){
        //assign the task to replacementRobot instead of us.
        replacementRobot.getRobotController().getTasks().add(task);
    }
    private Robot findRobotToPerformTask(){
        //todo use task allocator for this
        //get the first robot that is not us
        for(Robot r : robotController.getServer().getAllRobots()){
            if(!r.equals(robotController.getRobot()) && r.getRobotController().getTasks().size() < 2){
                return r;
            }
        }
        return robotController.getServer().getAllRobots().get(7);
    }
    private BinOnTopOfGridPickup getBinOnTopOffGridPickup(ArrayList<Task> subTasks) {
        for(Task t : subTasks){
            if(t instanceof BinOnTopOfGridPickup)
                return (BinOnTopOfGridPickup) t;
        }
        return null;
    }
    private boolean containsBinOnTopOfGridPickup(ArrayList<Task> subTasks){
        for(Task t : subTasks){
            if(t instanceof BinOnTopOfGridPickup){
                return true;
            }
        }return false;
    }
}