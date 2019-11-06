package dk.aau.d507e19.warehousesim.controller.robot.plan.task;

import dk.aau.d507e19.warehousesim.controller.path.Path;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.PathFinder;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.MovementPredictor;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.robot.RobotController;
import dk.aau.d507e19.warehousesim.controller.server.Reservation;
import dk.aau.d507e19.warehousesim.controller.server.ReservationManager;
import dk.aau.d507e19.warehousesim.controller.server.Server;
import dk.aau.d507e19.warehousesim.controller.server.TimeFrame;
import dk.aau.d507e19.warehousesim.exception.DestinationReservedIndefinitelyException;
import dk.aau.d507e19.warehousesim.exception.NoPathFoundException;

import java.util.ArrayList;

public class ReservationNavigation extends Navigation {

    private RobotController robotController;
    private PathFinder pathFinder;
    private Robot robot;
    private Server server;

    private boolean isCompleted = false;

    public ReservationNavigation(RobotController robotController, GridCoordinate destination) {
        super(robotController, destination);
        this.robotController = robotController;
        this.pathFinder = robotController.getPathFinder();
        this.robot = robotController.getRobot();
        this.server = robotController.getServer();
    }

    // Returns true if a valid path is found
    protected boolean planPath() {
        GridCoordinate start = robot.getApproximateGridCoordinate();

        Path newPath;
        try {
            newPath = robotController.getPathFinder().calculatePath(start, destination);
        } catch (DestinationReservedIndefinitelyException e) {
            askOccupyingRobotToMove(e.getDest());
            return false;
        } catch (NoPathFoundException e) {
            // Path planning failed - Retrying after delay
            //System.out.println("Path finding failed. Start : " + e.getStart() + " Destination : " + e.getDest()
            //+ "at time : " + server.getTimeInTicks());
            return false;
        }

        // Remove previously held reservations
        setNewPath(newPath);
        updateReservations(newPath);
        return true;
    }

    private void updateReservations(Path newPath) {
        // Remove old reservations
        server.getReservationManager().removeReservationsBy(robot);

        // Add reservations from new path
        if(pathFinder.accountsForReservations()){
            if (newPath.getFullPath().size() > 1)
                reservePath(newPath, true);
            else
                reserveCurrentTileIndefinitely();
        }
    }

    private void reservePath(Path path, boolean reserveLastTileIndefinitely) {
        Server server = robotController.getServer();
        ArrayList<Reservation> reservations = MovementPredictor.calculateReservations(robot, path, server.getTimeInTicks(), 0);

        if (reserveLastTileIndefinitely) { // Replace last reservation with an indefinite one
            Reservation lastReservation = reservations.get(reservations.size() - 1);
            TimeFrame indefiniteTimeFrame = TimeFrame.indefiniteTimeFrameFrom(lastReservation.getTimeFrame().getStart());
            Reservation indefiniteReservation = new Reservation
                    (lastReservation.getRobot(), lastReservation.getGridCoordinate(), indefiniteTimeFrame);
            reservations.remove(reservations.size() - 1);
            reservations.add(indefiniteReservation);
        }

        server.getReservationManager().reserve(reservations);
    }

    private void reserveCurrentTileIndefinitely() {
        Server server = robotController.getServer();
        ReservationManager reservationManager = server.getReservationManager();
        reservationManager.reserve(robot, robot.getGridCoordinate(), TimeFrame.indefiniteTimeFrameFrom(server.getTimeInTicks()));
    }

    private void complete() {
        isCompleted = true;
    }

    private Reservation createLastTileIndefiniteReservation(ArrayList<Reservation> reservations) {
        Reservation lastReservation = reservations.get(reservations.size() - 1);
        TimeFrame indefiniteTimeFrame = TimeFrame.indefiniteTimeFrameFrom(lastReservation.getTimeFrame().getStart());
        return new Reservation(lastReservation.getRobot(), lastReservation.getGridCoordinate(), indefiniteTimeFrame);
    }

    @Override
    protected boolean canInterrupt() {
        return !isMoving();
    }

    @Override
    public boolean isCompleted() {
        return isCompleted;
    }

    @Override
    public boolean hasFailed() {
        return false;
    }

}
