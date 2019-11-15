package dk.aau.d507e19.warehousesim.controller.robot.plan.task;

import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.robot.RobotController;

public class OutOfBattery implements Task {
    RobotController robotController;
    Navigation navigation;
    GridCoordinate destination;

    public OutOfBattery(RobotController robotController) {
        this.robotController = robotController;
    }

    @Override
    public void perform() {
        if(navigation == null){
            //todo prevent teleporting make this better
            destination = this.robotController.getRobot().getApproximateGridCoordinate();
            navigation = Navigation.getInstance(this.robotController,destination);
        }
        if(!navigation.isCompleted()){
            navigation.perform();
        }
        //need to finish navigation to the tile we're headed towards
    }

    @Override
    public boolean isCompleted() {
        return false;
    }

    @Override
    public boolean hasFailed() {
        return false;
    }

    @Override
    public void setRobot(Robot robot) {

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
