package dk.aau.d507e19.warehousesim.controller.robot.plan.task;

import dk.aau.d507e19.warehousesim.controller.path.Path;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.PartialPathFinder;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.RobotController;
import dk.aau.d507e19.warehousesim.exception.DestinationReservedIndefinitelyException;
import dk.aau.d507e19.warehousesim.exception.NextStepBlockedException;
import dk.aau.d507e19.warehousesim.exception.NoPathFoundException;

public class SmartNavigation extends Navigation {

    private PartialPathFinder partialPathFinder;

    public SmartNavigation(RobotController robotController, GridCoordinate destination) {
        super(robotController, destination);
        if(!(robotController.getPathFinder() instanceof PartialPathFinder))
            throw new IllegalArgumentException("Robot must have a partial pathfinder to use the SmartNavigation task");
        this.partialPathFinder = (PartialPathFinder) robotController.getPathFinder();
    }

    @Override
    boolean planPath() {
        GridCoordinate start = robot.getGridCoordinate();
        Path newPath;

        try {
            newPath = robotController.getPathFinder().calculatePath(start, destination);
        } catch (DestinationReservedIndefinitelyException e) {
            askOccupyingRobotToMove(e.getDest());
            return false;
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
            return false; // todo
            //return planOneStepForwardPath(e.blockedCoordinate);
        }

        setNewPath(partialPath);
        updateReservations(partialPath);
        return false;
    }

    private boolean planOneStepForwardPath(GridCoordinate blockedCoordinate) {


        return false;
    }

    @Override
    boolean canInterrupt() {
        return false;
    }
}
