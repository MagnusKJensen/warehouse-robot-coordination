package dk.aau.d507e19.warehousesim.controller.robot.plan.task;

import dk.aau.d507e19.warehousesim.controller.path.Line;
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


    public EmergencyStop(RobotController robotController) {
        this.robotController = robotController;
        this.reservationManager = this.robotController.getServer().getReservationManager();
    }


    @Override
    public void perform() {
        if(destination==null){
            destination = calcDestination(calcDistance(this.robotController.getRobot()));
            lineTraversal = new LineTraversal(this.robotController.getRobot(),this.robotController.getRobot().getCurrentPosition(),destination,calcDistance(this.robotController.getRobot()));
            //make sure that other robots know that we're about to stop at destination.
            //todo @bau make emergencyStop reserve the tiles it drives
            //make indefinite reservation for the emergency stop tile from current time
            Reservation reservation = new Reservation(this.robotController.getRobot(),destination,TimeFrame.indefiniteTimeFrameFrom(this.robotController.getServer().getTimeInTicks()));
            if(reservationManager.hasConflictingReservations(reservation)){
                //force all the other robots to replan their routes
                forceReplan(reservation,reservationManager.getConflictingReservations(reservation));
                reservationManager.reserve(reservation);
            }else{
                reservationManager.reserve(reservation);
            }

        }
        if(!lineTraversal.isCompleted()){
            lineTraversal.perform();
        }else{
            complete();
        }
    }

    private void forceReplan(Reservation reservation, ArrayList<Reservation> conflictingReservations) {
        for(Reservation r : conflictingReservations){
            //remove all of this robots reservations
            reservationManager.removeReservationsBy(r.getRobot());
            //make robot stop immediately todo @bau, make a new task that stops at a free location instead of just emergency stopping
            r.getRobot().getRobotController().assignImmediateTask(new EmergencyStop(r.getRobot().getRobotController()));
            //todo @bau is this needed?
            r.getRobot().getRobotController().getTasks().get(0).perform();
        }
    }

    private float calcDistance(Robot robot){
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
        return (float) (Math.pow(currentSpeed,2)/(2*deceleration));
    }
    private GridCoordinate calcDestination(float distanceTravelled){
        GridCoordinate currentPosition = this.robotController.getRobot().getNextGridCoordinate();
        Direction direction = this.robotController.getRobot().getDirection();

        if(this.robotController.getRobot().getCurrentSpeed()==0)
            return this.robotController.getRobot().getNextGridCoordinate();

        int rounded  = (int)Math.ceil(distanceTravelled);
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
