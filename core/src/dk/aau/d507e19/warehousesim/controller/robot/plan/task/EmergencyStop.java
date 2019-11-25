package dk.aau.d507e19.warehousesim.controller.robot.plan.task;

import dk.aau.d507e19.warehousesim.Position;
import dk.aau.d507e19.warehousesim.controller.robot.*;
import dk.aau.d507e19.warehousesim.controller.robot.plan.LineTraversal;
import dk.aau.d507e19.warehousesim.controller.server.Reservation;
import dk.aau.d507e19.warehousesim.controller.server.ReservationManager;

import java.util.ArrayList;

public class EmergencyStop implements Task {
    RobotController robotController;
    GridCoordinate destination;
    boolean finished = false;
    LineTraversal lineTraversal;
    ReservationManager reservationManager;
    private float distanceToBrake;


    public EmergencyStop(RobotController robotController) {
        this.robotController = robotController;
        this.reservationManager = this.robotController.getServer().getReservationManager();
    }

    @Override
    public void perform() {
        if (!robotController.isMoving()) {
            complete();
            return;
        }

        if (destination == null) {
            reservationManager.removeReservationsBy(robotController.getRobot());

            destination = calcDestination(getMinimumBrakeDistance(this.robotController.getRobot()));
            float distanceToDrive = destination.distanceFrom(robotController.getRobot().getCurrentPosition());

            if (distanceToDrive < 0.001f) {
                robotController.getRobot().updatePosition(destination.toPosition(), 0f);
                robotController.reserveCurrentSpot();
                complete();
                return;
            }else{
                lineTraversal = new LineTraversal(this.robotController.getRobot(), this.robotController.getRobot().getCurrentPosition(), destination, distanceToDrive);
                reserveEmergencyStopPath();
                //make sure that other robots know that we're about to stop at destination.
            }
        }
        if (!lineTraversal.isCompleted()) {
            lineTraversal.perform();
        } else {
            complete();
        }
    }

    private void reserveEmergencyStopPath() {

        ArrayList<Reservation> reservations
                = MovementPredictor.calculateReservations(robotController.getRobot(), lineTraversal.getSpeedCalculator(), robotController.getServer().getTimeInTicks());
        robotController.getServer().getReservationManager().reserve(reservations);

        //make indefinite reservation for the emergency stop tile from current time
        Reservation lastTileReservation = ReservationNavigation.createLastTileIndefiniteReservation(reservations);
        reservations.add(lastTileReservation);

        if (reservationManager.hasConflictingReservations(reservations)) {
            //force all the other robots to replan their routes
            forceReplan(reservationManager.getConflictingReservations(reservations));
            reservationManager.reserve(lastTileReservation);
        } else {
            reservationManager.reserve(lastTileReservation);
        }
    }

    private void forceReplan(ArrayList<Reservation> conflictingReservations) {
        for (Reservation r : conflictingReservations) {
            r.getRobot().getRobotController().emergencyStop();
        }
    }

    private float getMinimumBrakeDistance(Robot robot) {
        float currentSpeed = robot.getCurrentSpeed();
        //if currentSpeed is 0 we might still have to move??
        if (currentSpeed == 0) {
            switch (robot.getDirection()) {
                case NORTH:
                    return (float) (Math.ceil(robot.getCurrentPosition().getY()) - robot.getCurrentPosition().getY());
                case SOUTH:
                    return (float) (robot.getCurrentPosition().getY() - Math.floor(robot.getCurrentPosition().getY()));
                case WEST:
                    return (float) (Math.ceil(robot.getCurrentPosition().getX()) - robot.getCurrentPosition().getX());
                case EAST:
                    return (float) (robot.getCurrentPosition().getX() - Math.floor(robot.getCurrentPosition().getX()));
            }
        }
        float deceleration = robot.getDecelerationBinSecond();
        //formula to find stopping dis: v^2 /2a src(https://physics.stackexchange.com/questions/3818/stopping-distance-frictionless)
        //v = curr speed, a = acceleration/deceleration
        distanceToBrake = (float) (Math.pow(currentSpeed, 2) / (2 * deceleration));
        return distanceToBrake;
    }


    private GridCoordinate calcDestination(float minBreakingDistance) {
        final float delta = 0.0001f;
        minBreakingDistance -= delta;
        minBreakingDistance = Math.max(minBreakingDistance, 0f);

        Direction direction = this.robotController.getRobot().getDirection();
        Robot robot = robotController.getRobot();

        float possibleStopX = robot.getCurrentPosition().getX() + (direction.xDir * minBreakingDistance);
        ;
        float possibleStopY = robot.getCurrentPosition().getY() + (direction.yDir * minBreakingDistance);
        Position stoppingPosition = new Position(possibleStopX, possibleStopY);
        GridCoordinate stoppingTile = GridCoordinate.getNextGridCoordinate(stoppingPosition, direction);
        return stoppingTile;
    }


    @Override
    public boolean isCompleted() {
        return finished;
    }

    private void complete() {
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
