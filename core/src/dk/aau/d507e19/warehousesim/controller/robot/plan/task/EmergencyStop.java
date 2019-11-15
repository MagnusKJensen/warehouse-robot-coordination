package dk.aau.d507e19.warehousesim.controller.robot.plan.task;

import dk.aau.d507e19.warehousesim.controller.robot.Direction;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.robot.RobotController;

public class EmergencyStop implements Task {
    RobotController robotController;
    GridCoordinate destination;


    public EmergencyStop(RobotController robotController) {
        this.robotController = robotController;
    }


    @Override
    public void perform() {
        if(destination==null){
            destination = calcDestination(this.robotController.getRobot());
        }
    }
    private GridCoordinate calcDestination(Robot robot){
        GridCoordinate dest = null;
        GridCoordinate currentPosition = robot.getApproximateGridCoordinate();
        float currentSpeed = robot.getCurrentSpeed();
        float deceleration = robot.getDecelerationBinSecond();
        Direction direction = robot.getDirection();
        //formula to find stopping dis: v^2 /2a src(https://physics.stackexchange.com/questions/3818/stopping-distance-frictionless)
        //v = curr speed, a = acceleration/deceleration
        double distanceTravelled = Math.pow(currentSpeed,2)/deceleration;
        //need to round this up - if we add 0.5 its always rounded up correctly
        int rounded = (int)Math.round(distanceTravelled+0.5);
        switch (direction){
            case NORTH:
                return new GridCoordinate(currentPosition.getX(),currentPosition.getY()+rounded);
            case SOUTH:
                return new GridCoordinate(currentPosition.getX(),currentPosition.getY()-rounded);
            case EAST:
                return new GridCoordinate(currentPosition.getX()+rounded,currentPosition.getY());
            case WEST:
                return new GridCoordinate(currentPosition.getX()-rounded,currentPosition.getY());
        }
        return dest;
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
