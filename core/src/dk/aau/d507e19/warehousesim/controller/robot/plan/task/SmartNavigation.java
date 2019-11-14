package dk.aau.d507e19.warehousesim.controller.robot.plan.task;

import dk.aau.d507e19.warehousesim.controller.path.Path;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.OneStepWaitingPathFinder;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.PartialPathFinder;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.robot.RobotController;
import dk.aau.d507e19.warehousesim.controller.server.ReservationManager;
import dk.aau.d507e19.warehousesim.exception.DestinationReservedIndefinitelyException;
import dk.aau.d507e19.warehousesim.exception.NextStepBlockedException;
import dk.aau.d507e19.warehousesim.exception.NoPathFoundException;

public class SmartNavigation extends Navigation {

    private PartialPathFinder partialPathFinder;
    private OneStepWaitingPathFinder oneStepPathFinder;

    public SmartNavigation(RobotController robotController, GridCoordinate destination) {
        super(robotController, destination);
        if(!(robotController.getPathFinder() instanceof PartialPathFinder))
            throw new IllegalArgumentException("Robot must have a partial pathfinder to use the SmartNavigation task");
        this.partialPathFinder = (PartialPathFinder) robotController.getPathFinder();
        this.oneStepPathFinder = new OneStepWaitingPathFinder(robotController.getRobot(), robotController.getServer());
    }

    @Override
    boolean planPath() {
        GridCoordinate start = robot.getGridCoordinate();
        Path newPath;

        if(robot.getRobotID() != 0)return false;

        try {
             newPath = robotController.getPathFinder().calculatePath(start, destination);
        } catch (NoPathFoundException e) {
            return planPartialPath();
        }

        // Remove previously held reservations
        setNewPath(newPath);
        updateReservations(newPath);
        return true;
    }

    private boolean planPartialPath() {
        Path partialPath;

        try{
            partialPath = partialPathFinder.findPartialPath(robot.getGridCoordinate(), destination);
        } catch (NextStepBlockedException e) {
            return planOneStepForwardPath(e.blockedCoordinate);
        }

        setNewPath(partialPath);
        updateReservations(partialPath);
        return false;
    }

    private boolean planOneStepForwardPath(GridCoordinate blockedCoordinate) {
        ReservationManager reservationManager = robotController.getServer().getReservationManager();
        Robot blockingRobot = reservationManager.getIndefiniteReservationsAt(blockedCoordinate).getRobot();

        if(blockingRobot.getRobotController().requestChainedMove(robot, blockedCoordinate)){
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
    boolean canInterrupt() {
        return !isMoving();
    }
}
