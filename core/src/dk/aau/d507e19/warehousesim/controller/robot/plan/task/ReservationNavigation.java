package dk.aau.d507e19.warehousesim.controller.robot.plan.task;

import dk.aau.d507e19.warehousesim.controller.path.Path;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.PathFinder;
import dk.aau.d507e19.warehousesim.controller.robot.*;
import dk.aau.d507e19.warehousesim.controller.server.Reservation;
import dk.aau.d507e19.warehousesim.controller.server.ReservationManager;
import dk.aau.d507e19.warehousesim.controller.server.Server;
import dk.aau.d507e19.warehousesim.controller.server.TimeFrame;
import dk.aau.d507e19.warehousesim.exception.DestinationReservedIndefinitelyException;
import dk.aau.d507e19.warehousesim.exception.NoPathFoundException;
import javafx.collections.transformation.SortedList;

import java.util.*;

public class ReservationNavigation extends Navigation {

    private RobotController robotController;
    private PathFinder pathFinder;
    private Robot robot;
    private Server server;
    private int succesiveAttemps = 1;

    public ReservationNavigation(RobotController robotController, GridCoordinate destination) {
        this(robotController, destination, UNLIMITED_RETRIES);
    }

    public ReservationNavigation(RobotController robotController, GridCoordinate destination, int maxRetries) {
        super(robotController, destination, maxRetries);
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
            handlePotentialBlock();
            return false;
        }

        // Remove previously held reservations
        setNewPath(newPath);
        updateReservations(newPath);
        succesiveAttemps = 1;
        return true;
    }

    // Asks blocking robots to move
    private void handlePotentialBlock() {
        // Ask one more neighbour for each time relocation has failed
        final int MAX_NEIGHBOURS_TO_ASK = 4;
        int neighboursToAsk = Math.min(succesiveAttemps, MAX_NEIGHBOURS_TO_ASK);

        int neighbourRadius = 3;
        ArrayList<Robot> neighbours = getNeighboursByDistance(neighbourRadius);
        Iterator<Robot> neighbourIterator = neighbours.iterator();
        while(neighbourIterator.hasNext() && neighboursToAsk > 0){
            neighbourIterator.next().getRobotController().requestMove();
            neighboursToAsk--;
        }
        succesiveAttemps += 1;
    }

    private ArrayList<Robot> getNeighboursByDistance(int maxDist) {
        ArrayList<Robot> nearestRobots = new ArrayList<>(server.getAllRobots());

        nearestRobots.removeIf((r) -> r.equals(this.robot)
                || r.getApproximateGridCoordinate().manhattanDistanceFrom(this.robot.getGridCoordinate()) > maxDist);

        Collections.sort(nearestRobots, (r1, r2) -> {
            int r1Distance = r1.getApproximateGridCoordinate().manhattanDistanceFrom(this.robot.getGridCoordinate());
            int r2Distance = r2.getApproximateGridCoordinate().manhattanDistanceFrom(this.robot.getGridCoordinate());
            return Integer.compare(r1Distance, r2Distance);
        });

        return nearestRobots;
    }


    public static Reservation createLastTileIndefiniteReservation(ArrayList<Reservation> reservations) {
        Reservation lastReservation = reservations.get(reservations.size() - 1);
        TimeFrame indefiniteTimeFrame = TimeFrame.indefiniteTimeFrameFrom(lastReservation.getTimeFrame().getStart());
        return new Reservation(lastReservation.getRobot(), lastReservation.getGridCoordinate(), indefiniteTimeFrame);
    }

    @Override
    protected boolean canInterrupt() {
        return !isMoving();
    }

}
