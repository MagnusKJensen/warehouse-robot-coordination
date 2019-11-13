package dk.aau.d507e19.warehousesim.controller.pathAlgorithms;

import dk.aau.d507e19.warehousesim.TimeUtils;
import dk.aau.d507e19.warehousesim.controller.path.Path;
import dk.aau.d507e19.warehousesim.controller.path.Step;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.MovementPredictor;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.robot.plan.task.ReservationNavigation;
import dk.aau.d507e19.warehousesim.controller.server.Reservation;
import dk.aau.d507e19.warehousesim.controller.server.Server;
import dk.aau.d507e19.warehousesim.exception.DestinationReservedIndefinitelyException;
import dk.aau.d507e19.warehousesim.exception.NoPathFoundException;
import java.util.ArrayList;

public class OneStepWaitingPathFinder implements PathFinder {

    private static final long WAITING_INTERVAL_TICKS = TimeUtils.secondsToTicks(0.4f);
    private static final long MAX_WAIT_TIME_TICKS = TimeUtils.secondsToTicks(120f);
    private Robot robot;
    private Server server;

    public OneStepWaitingPathFinder(Robot robot, Server server) {
        this.robot = robot;
        this.server = server;
    }

    @Override
    public Path calculatePath(GridCoordinate start, GridCoordinate destination) throws NoPathFoundException {
        if(!destination.isNeighbourOf(start))
            throw new IllegalArgumentException("Destination coordinate must be immediate neighbour of start coordinate");

        if(server.getReservationManager().isReservedIndefinitely(destination))
            throw new DestinationReservedIndefinitelyException(start, destination);

        ArrayList<Reservation> reservations = new ArrayList<>();
        ArrayList<Step> waitingSteps = new ArrayList<>();
        // Add initial steps
        waitingSteps.add(new Step(start));

        long ticksSpentWaiting = 0;
        while(ticksSpentWaiting < MAX_WAIT_TIME_TICKS) {
            ArrayList<Step> fullPathSteps = new ArrayList<>(waitingSteps);
            fullPathSteps.add(new Step(destination));
            Path path = new Path(fullPathSteps);

            reservations = MovementPredictor.calculateReservations(robot, path, server.getTimeInTicks(), 0);
            reservations.add(ReservationNavigation.createLastTileIndefiniteReservation(reservations));

            if(server.getReservationManager().hasConflictingReservations(reservations)){
                waitingSteps.add(new Step(start, WAITING_INTERVAL_TICKS)); // Try waiting one more step
                ticksSpentWaiting += WAITING_INTERVAL_TICKS;
            }else
                return path;
        }

        throw new NoPathFoundException(start, destination, "Wait timed out for OneStepWaitingPathFinder - waited "
                + MAX_WAIT_TIME_TICKS + " ticks but was still not able to move to destination");
    }

    @Override
    public boolean accountsForReservations() {
        return true;
    }
}
