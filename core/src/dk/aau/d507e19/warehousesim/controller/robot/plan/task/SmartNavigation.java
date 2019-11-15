package dk.aau.d507e19.warehousesim.controller.robot.plan.task;

import dk.aau.d507e19.warehousesim.controller.path.Path;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.OneStepWaitingPathFinder;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.PartialPathFinder;
import dk.aau.d507e19.warehousesim.controller.robot.Direction;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.robot.RobotController;
import dk.aau.d507e19.warehousesim.controller.robot.plan.ChainMover;
import dk.aau.d507e19.warehousesim.controller.server.ReservationManager;
import dk.aau.d507e19.warehousesim.exception.NextStepBlockedException;
import dk.aau.d507e19.warehousesim.exception.NoPathFoundException;

import java.util.ArrayList;

public class SmartNavigation extends Navigation {

    private PartialPathFinder partialPathFinder;
    private OneStepWaitingPathFinder oneStepPathFinder;

    public SmartNavigation(RobotController robotController, GridCoordinate destination) {
        super(robotController, destination);
        if (!(robotController.getPathFinder() instanceof PartialPathFinder))
            throw new IllegalArgumentException("Robot must have a partial pathfinder to use the SmartNavigation task");
        this.partialPathFinder = (PartialPathFinder) robotController.getPathFinder();
        this.oneStepPathFinder = new OneStepWaitingPathFinder(robotController.getRobot(), robotController.getServer());
    }

    @Override
    boolean planPath() {
        GridCoordinate start = robot.getGridCoordinate();
        Path newPath;

        newPath = partialPathFinder.calculatePath(start, destination);
        // Check to see if any of the neighbours (in the direction of the destination) are blocking the robot
        if (newPath.isOneStepPath()){
            ReservationManager resManager = robotController.getServer().getReservationManager();
            ArrayList<Direction> directionsTowardDestination = GridCoordinate.getDirectionsOf(start, destination);

            // Check neighbours toward the destination to see if there are blocking robots there
            for (Direction direction : directionsTowardDestination) {
                GridCoordinate neighbourCoordinate = start.plus(direction);
                boolean isNeighbourReservedForever = resManager.isReservedIndefinitely(neighbourCoordinate);
                if (isNeighbourReservedForever)
                    planOneStepForwardPath(neighbourCoordinate);
            }

            // No neighbours are blocked and no path could be found
            return false;
        }

        setNewPath(newPath);
        updateReservations(newPath);
        return true;
    }

    private boolean planOneStepForwardPath(GridCoordinate blockedCoordinate) {
        ReservationManager reservationManager = robotController.getServer().getReservationManager();
        Robot blockingRobot = reservationManager.getIndefiniteReservationsAt(blockedCoordinate).getRobot();

        // If the blocking robot is not interruptable, cancel path planning
        if (!blockingRobot.getRobotController().canInterrupt(this.robot))
            return false;

        ChainMover chainMover = new ChainMover(this.robot, blockingRobot, this.robot.getRobotController().getServer());
        if (chainMover.attemptChainMove()) {
            try {
                Path newPath = oneStepPathFinder.calculatePath(robot.getGridCoordinate(), blockedCoordinate);
                setNewPath(newPath);
                updateReservations(newPath);
            } catch (NoPathFoundException e) {
                throw new RuntimeException("Fatal exception : Failed to calculate path one step forward");
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean canInterrupt() {
        return !isMoving();
    }
}
