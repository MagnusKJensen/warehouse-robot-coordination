package dk.aau.d507e19.warehousesim.controller.robot.plan.task;

import dk.aau.d507e19.warehousesim.Simulation;
import dk.aau.d507e19.warehousesim.TimeUtils;
import dk.aau.d507e19.warehousesim.controller.path.Path;
import dk.aau.d507e19.warehousesim.controller.path.Step;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.PathFinder;
import dk.aau.d507e19.warehousesim.controller.robot.*;
import dk.aau.d507e19.warehousesim.controller.server.Reservation;
import dk.aau.d507e19.warehousesim.controller.server.ReservationManager;
import dk.aau.d507e19.warehousesim.controller.server.Server;
import dk.aau.d507e19.warehousesim.controller.server.TimeFrame;
import dk.aau.d507e19.warehousesim.exception.NoPathFoundException;

import java.util.ArrayList;
import java.util.Random;

public class StepAsideNavigator extends Navigation{

    private PathFinder pathFinder;
    private Server server;
    private Random rand = new Random(Simulation.RANDOM_SEED + robot.getRobotID());

    public StepAsideNavigator(RobotController robotController, GridCoordinate destination) {
        this(robotController, destination, UNLIMITED_RETRIES);
    }

    public StepAsideNavigator(RobotController robotController, GridCoordinate destination, int maxRetries) {
        super(robotController, destination, maxRetries);
        pathFinder = robotController.getPathFinder();
        server = robotController.getServer();
        setTicksBetweenRetries(TimeUtils.secondsToTicks(2.5f));
    }

    @Override
    boolean planPath() {
        int distanceFromDestination = robot.getGridCoordinate().manhattanDistanceFrom(destination);
        if(!destination.equals(robot.getGridCoordinate())
                && server.getReservationManager().isReservedIndefinitely(destination)
                && distanceFromDestination < 3){
            askOccupyingRobotToMove(destination);
            return false;
        }

        Path newPath;
        try {
            newPath = pathFinder.calculatePath(robot.getGridCoordinate(), destination);
        } catch (NoPathFoundException e) {
            e.printStackTrace();
            return false;
        }

        server.getReservationManager().removeReservationsBy(robot);
        newPath = stopAtCollision(newPath);
        setNewPath(newPath);
        reservePath(newPath, true);

        return false;
    }

    private Path stopAtCollision(Path newPath) {
        if(newPath.getFullPath().size() == 1) return attemptRandomExtension(newPath);

        ArrayList<Reservation> fullPathReservations = MovementPredictor.calculateReservations(robot, newPath, server.getTimeInTicks(), 0);
        fullPathReservations.add(createLastTileIndefiniteReservation(fullPathReservations));
        ArrayList<Step> shortenedPathSteps = new ArrayList<>();

        for(Reservation reservation : fullPathReservations){
            if(!server.getReservationManager().hasConflictingReservations(reservation)) {
                // If no collisions are found, then add this step to the path
                shortenedPathSteps.add(new Step(reservation.getGridCoordinate()));
            }else{
                if(server.getReservationManager().isReservedIndefinitely(reservation.getGridCoordinate()))
                    askOccupyingRobotToMove(reservation.getGridCoordinate());

                // If collisions are found the path must stop here
                if(shortenedPathSteps.size() == 1)
                    return attemptRandomExtension(Path.oneStepPath(shortenedPathSteps.get(0)));

                // Create the shortened path and the corresponding reservations
                Path shortenedPath = new Path(shortenedPathSteps);
                ArrayList<Reservation> shortenedReservations = MovementPredictor.calculateReservations(robot, shortenedPath, server.getTimeInTicks(), 0);
                shortenedReservations.add(createLastTileIndefiniteReservation(shortenedReservations));

                // If there is any conflicts for the new path then shorten it even more
                if(server.getReservationManager().hasConflictingReservations(shortenedReservations)){
                    shortenedPathSteps.remove(shortenedPathSteps.size() - 1);
                    shortenedPath = new Path(shortenedPathSteps);
                    return stopAtCollision(shortenedPath);
                }

                // Otherwise attempt to do a random move in the end to
                shortenedPath = attemptRandomExtension(shortenedPath);
                return shortenedPath;
            }
        }

        // No collision found return original path
        return newPath;
    }

    private Path attemptRandomExtension(Path shortenedPath) {
        Step lastStep = shortenedPath.getLastStep();

        Direction[] directions = randomizeArray(Direction.values());
        for(Direction dir : directions){
            Step newLastStep = new Step(lastStep.getX() + dir.xDir, lastStep.getY() + dir.yDir);
            if(!server.getGridBounds().isWithinBounds(newLastStep.getGridCoordinate()))
                continue;

            ArrayList<Step> extendedSteps = new ArrayList<>(shortenedPath.getFullPath());
            extendedSteps.add(newLastStep);
            Path extendedPath = new Path(extendedSteps);

            ArrayList<Reservation> reservations;
            reservations = MovementPredictor.calculateReservations(robot, extendedPath, server.getTimeInTicks(), 0);
            reservations.add(createLastTileIndefiniteReservation(reservations));

            if(!server.getReservationManager().hasConflictingReservations(reservations)){

                // Janky fix for RRT
                try {
                    pathFinder.calculatePath(extendedSteps.get(extendedSteps.size() - 2).getGridCoordinate(), extendedSteps.get(extendedSteps.size() - 1).getGridCoordinate());
                } catch (NoPathFoundException e) {
                    e.printStackTrace();
                }
                return extendedPath;
            }

        }
        return shortenedPath;
    }

    private void reservePath(Path path, boolean reserveLastTileIndefinitely) {
        Server server = robotController.getServer();

        if(path.getFullPath().size() == 1){
            reserveCurrentTileIndefinitely();
            return;
        }

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

    private Reservation createLastTileIndefiniteReservation(ArrayList<Reservation> reservations) {
        Reservation lastReservation = reservations.get(reservations.size() - 1);
        TimeFrame indefiniteTimeFrame = TimeFrame.indefiniteTimeFrameFrom(lastReservation.getTimeFrame().getStart());
        return new Reservation(lastReservation.getRobot(), lastReservation.getGridCoordinate(), indefiniteTimeFrame);
    }

    @Override
    boolean canInterrupt() {
        return !isMoving();
    }

    public Direction[] randomizeArray(Direction[] array){
        for (int i=0; i<array.length; i++) {
            int randomPosition = rand.nextInt(array.length);
            Direction temp = array[i];
            array[i] = array[randomPosition];
            array[randomPosition] = temp;
        }

        return array;
    }

}
