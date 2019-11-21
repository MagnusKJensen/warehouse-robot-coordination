package dk.aau.d507e19.warehousesim.controller.robot.plan.task;

import dk.aau.d507e19.warehousesim.controller.robot.*;
import dk.aau.d507e19.warehousesim.controller.robot.plan.LineTraversal;
import dk.aau.d507e19.warehousesim.controller.server.Reservation;
import dk.aau.d507e19.warehousesim.controller.server.ReservationManager;
import dk.aau.d507e19.warehousesim.controller.server.TimeFrame;

import java.util.ArrayList;

public class EmergencyStop implements Task {
    RobotController robotController;
    GridCoordinate destination;
    boolean finished = false;
    LineTraversal lineTraversal;
    ReservationManager reservationManager;
    private float distanceToBrake,distanceToDrive;


    public EmergencyStop(RobotController robotController) {
        this.robotController = robotController;
        this.reservationManager = this.robotController.getServer().getReservationManager();
    }

    @Override
    public void perform() {
        if(!robotController.isMoving()){
            complete();
            return;
        }

        if(destination==null){
            reservationManager.removeReservationsBy(robotController.getRobot());

            destination = calcDestination(getMinimumBreakDistance(this.robotController.getRobot()));
            lineTraversal = new LineTraversal(this.robotController.getRobot(),this.robotController.getRobot().getCurrentPosition(),destination,distanceToDrive);
            //make sure that other robots know that we're about to stop at destination.
            //todo @bau make emergencyStop reserve the tiles it drives


            reserveEmergencyStopPath();
        }
        if(!lineTraversal.isCompleted()){
            lineTraversal.perform();
        }else{
            complete();
        }
    }

    private void reserveEmergencyStopPath() {
        ArrayList<Reservation> reservations
                = MovementPredictor.calculateReservations(robotController.getRobot(), lineTraversal.getSpeedCalculator(), robotController.getServer().getTimeInTicks());
        robotController.getServer().getReservationManager().reserve(reservations);

        //make indefinite reservation for the emergency stop tile from current time
        Reservation reservation = new Reservation(this.robotController.getRobot(),destination, TimeFrame.indefiniteTimeFrameFrom(this.robotController.getServer().getTimeInTicks()));
        if(reservationManager.hasConflictingReservations(reservation)){
            //force all the other robots to replan their routes
            forceReplan(reservation,reservationManager.getConflictingReservations(reservation));
            reservationManager.reserve(reservation);
        }else{
            reservationManager.reserve(reservation);
        }
    }

    private void forceReplan(Reservation reservation, ArrayList<Reservation> conflictingReservations) {
        for(Reservation r : conflictingReservations){
            r.getRobot().getRobotController().requestEmergencyStop();
        }
    }

    private float getMinimumBreakDistance(Robot robot){
        float currentSpeed = robot.getCurrentSpeed();
        //if currentSpeed is 0 we might still have to move??
        if(currentSpeed == 0){
            switch(robot.getDirection()){
                case NORTH: return (float) (Math.ceil(robot.getCurrentPosition().getY()) - robot.getCurrentPosition().getY());
                case SOUTH: return (float) (robot.getCurrentPosition().getY() - Math.floor(robot.getCurrentPosition().getY()));
                case WEST: return (float) (Math.ceil(robot.getCurrentPosition().getX()) - robot.getCurrentPosition().getX());
                case EAST: return (float) (robot.getCurrentPosition().getX() - Math.floor(robot.getCurrentPosition().getX()));
            }
        }
        float deceleration = robot.getDecelerationBinSecond();
        //formula to find stopping dis: v^2 /2a src(https://physics.stackexchange.com/questions/3818/stopping-distance-frictionless)
        //v = curr speed, a = acceleration/deceleration
        distanceToBrake = (float) (Math.pow(currentSpeed,2)/(2*deceleration));
        if(robot.getRobotID() == 18) System.out.println(distanceToBrake + ", " + robot.getCurrentPosition());
        return distanceToBrake;
    }
    private GridCoordinate calcDestination(float distanceTravelled){
        GridCoordinate destination;
        GridCoordinate currentAdjustedPosition = this.robotController.getRobot().getNextGridCoordinate();
        Direction direction = this.robotController.getRobot().getDirection();

        if(this.robotController.getRobot().getCurrentSpeed()==0){
            destination = robotController.getRobot().getNextGridCoordinate();
            switch (direction){
                case NORTH:
                    distanceToDrive = destination.getY()-this.robotController.getRobot().getCurrentPosition().getY();
                    break;
                case SOUTH:
                    distanceToDrive = this.robotController.getRobot().getCurrentPosition().getY() - destination.getY();
                    break;
                case EAST:
                    distanceToDrive = destination.getX()-this.robotController.getRobot().getCurrentPosition().getX();
                    break;
                case WEST:
                    distanceToDrive = this.robotController.getRobot().getCurrentPosition().getX()- destination.getX();
                    break;
            }
            return destination;
        }

        int rounded  = (int)Math.ceil(distanceTravelled);
        switch (direction){
            case NORTH:
                distanceToDrive = currentAdjustedPosition.getY()+rounded - this.robotController.getRobot().getCurrentPosition().getY();
                return new GridCoordinate(currentAdjustedPosition.getX(),currentAdjustedPosition.getY()+rounded);
            case SOUTH:
                distanceToDrive = this.robotController.getRobot().getCurrentPosition().getY() - (currentAdjustedPosition.getY()-rounded);
                return new GridCoordinate(currentAdjustedPosition.getX(),currentAdjustedPosition.getY()-rounded);
            case EAST:
                distanceToDrive = currentAdjustedPosition.getX()+rounded - this.robotController.getRobot().getCurrentPosition().getX();
                return new GridCoordinate(currentAdjustedPosition.getX()+rounded,currentAdjustedPosition.getY());
            case WEST:
                distanceToDrive = this.robotController.getRobot().getCurrentPosition().getX() - (currentAdjustedPosition.getX()-rounded);
                return new GridCoordinate(currentAdjustedPosition.getX()-rounded,currentAdjustedPosition.getY());
        }
        return null;
    }


    @Override
    public boolean isCompleted() {
        return finished;
    }
    private void complete(){
        finished = true;
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
