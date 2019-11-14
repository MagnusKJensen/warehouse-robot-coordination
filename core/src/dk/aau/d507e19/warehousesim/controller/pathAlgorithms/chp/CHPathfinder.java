package dk.aau.d507e19.warehousesim.controller.pathAlgorithms.chp;

import dk.aau.d507e19.warehousesim.TimeUtils;
import dk.aau.d507e19.warehousesim.controller.path.Path;
import dk.aau.d507e19.warehousesim.controller.path.Step;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.DummyPathFinder;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.PartialPathFinder;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.PathFinder;
import dk.aau.d507e19.warehousesim.controller.robot.*;
import dk.aau.d507e19.warehousesim.controller.server.Reservation;
import dk.aau.d507e19.warehousesim.controller.server.ReservationManager;
import dk.aau.d507e19.warehousesim.controller.server.Server;
import dk.aau.d507e19.warehousesim.controller.server.TimeFrame;
import dk.aau.d507e19.warehousesim.exception.DestinationReservedIndefinitelyException;
import dk.aau.d507e19.warehousesim.exception.NextStepBlockedException;
import dk.aau.d507e19.warehousesim.exception.NoValidPathException;
import dk.aau.d507e19.warehousesim.exception.PathFindingTimedOutException;
import dk.aau.d507e19.warehousesim.storagegrid.GridBounds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;
import java.util.PriorityQueue;

public class CHPathfinder implements PartialPathFinder {

    private static final long MAXIMUM_WAIT_TIME = TimeUtils.secondsToTicks(10);
    private static final long MAXIMUM_ITERATIONS = 1000;
    private static final long MIN_TIME_BETWEEN_NODES = TimeUtils.secondsToTicks(0.9f);
    private final RobotController robotController;
    private final Server server;

    private Heuristic heuristic;
    private GCostCalculator gCostCalculator;
    private CHNodeFactory nodeFactory;
    private GridBounds gridBounds;


    private static final long STANDARD_WAIT_TIME_IN_TICKS = 30L;

    public static CHPathfinder defaultCHPathfinder(GridBounds gridBounds, RobotController robotController) {
        return new CHPathfinder(gridBounds, new DistanceTurnHeuristic(), new DistanceTurnGCost(), robotController);
    }

    public CHPathfinder(GridBounds gridBounds, Heuristic heuristic, GCostCalculator gCostCalculator, RobotController robotController) {
        this.gridBounds = gridBounds;
        this.heuristic = heuristic;
        this.gCostCalculator = gCostCalculator;
        this.robotController = robotController;
        this.server = robotController.getServer();
        this.nodeFactory = new CHNodeFactory(heuristic, gCostCalculator, robotController);
    }

    @Override
    public Path calculatePath(GridCoordinate start, GridCoordinate destination) throws PathFindingTimedOutException, NoValidPathException, DestinationReservedIndefinitelyException {
        if (start.equals(destination))
            return Path.oneStepPath(new Step(start));

        if (server.getReservationManager().isReservedIndefinitely(destination))
            throw new DestinationReservedIndefinitelyException(start, destination);

        PriorityQueue<CHNode> openList = new PriorityQueue<>();
        openList.add(nodeFactory.createInitialNode(start, destination));

        int iterationCount = 0;
        while (!openList.isEmpty()) {
            CHNode bestOpenListNode = openList.poll();

            ArrayList<CHNode> successors = getValidSuccessors(bestOpenListNode, destination);

            //Check if destination is reached
            for (CHNode successor : successors) {
                if (successor.getGridCoordinate().equals(destination)) {
                    /* // efficiency stats
                    System.out.print("Iterations to calculate path : " + iterationCount);
                    int manhattanDistance = Math.abs(start.getX() - destination.getX()) + Math.abs(start.getY() - destination.getY());
                    System.out.println(" || Manhattan distance : " + manhattanDistance + " || Path length : " + successor.getPath().getFullPath().size());*/
                    return successor.getPath();
                }
            }

            openList.addAll(successors);

            iterationCount++;
            if (iterationCount > MAXIMUM_ITERATIONS)
                throw new PathFindingTimedOutException(start, destination, iterationCount);
        }

        throw new NoValidPathException(start, destination, "Pathfinder max wait time per tile : " + MAXIMUM_WAIT_TIME);
    }

    @Override
    public boolean accountsForReservations() {
        return true;
    }

    private ArrayList<CHNode> getValidSuccessors(CHNode parent, GridCoordinate target) {
        ArrayList<GridCoordinate> neighbourCoords = getValidNeighbours(parent.getGridCoordinate());
        ArrayList<CHNode> successors = new ArrayList<>();

        for (GridCoordinate coord : neighbourCoords)
            successors.add(nodeFactory.createNode(coord, target, parent));

        successors.add(nodeFactory.createWaitingNode(parent, STANDARD_WAIT_TIME_IN_TICKS));

        successors.removeIf((node) -> isInvalidNode(node, target));

        return successors;
    }

    private boolean isInvalidNode(CHNode node, GridCoordinate target) {
        Step lastStep = node.getPath().getLastStep();
        if (lastStep.isWaitingStep() && lastStep.getWaitTimeInTicks() > MAXIMUM_WAIT_TIME)
            return true;

        Robot robot = robotController.getRobot();
        ArrayList<Reservation> reservations =
                MovementPredictor.calculateReservations(robot, node.getPath(), server.getTimeInTicks(), 0);

        Reservation nodeReservation = reservations.get(reservations.size() - 1);

        if (nodeReservation.getGridCoordinate().equals(target)) {
            TimeFrame indefiniteTimeFrame = TimeFrame.indefiniteTimeFrameFrom(nodeReservation.getTimeFrame().getStart());
            Reservation indefiniteReservation = new Reservation(robot, target, indefiniteTimeFrame);

            return server.getReservationManager().hasConflictingReservations(reservations) ||
                    server.getReservationManager().hasConflictingReservations(indefiniteReservation);
        } else {
            // todo (Bug: will not ignore it's own reservation)
            return server.getReservationManager().hasConflictingReservations(reservations);
        }
    }

    // Returns all neighbours within bounds
    private ArrayList<GridCoordinate> getValidNeighbours(GridCoordinate originalCoords) {
        ArrayList<GridCoordinate> neighbours = new ArrayList<>();

        for (Direction dir : Direction.values()) {
            int neighbourX = originalCoords.getX() + dir.xDir;
            int neighbourY = originalCoords.getY() + dir.yDir;
            GridCoordinate neighbour = new GridCoordinate(neighbourX, neighbourY);
            if (gridBounds.isWithinBounds(neighbour))
                neighbours.add(neighbour);
        }

        return neighbours;
    }

    @Override
    public Path findPartialPath(GridCoordinate start, GridCoordinate destination) throws NextStepBlockedException {
        PriorityQueue<CHNode> openList = new PriorityQueue<>();
        final CHNode initialNode = nodeFactory.createInitialNode(start, destination);
        openList.add(initialNode);

        // The default best partial path is the starting node
        CHNode bestNodeSoFar = initialNode;


        int iterationCount = 0;
        while (!openList.isEmpty()) {
            CHNode bestOpenListNode = openList.poll();

            // Check if this node
            if (bestOpenListNode.getFCost() <= bestNodeSoFar.getFCost()
                    && bestOpenListNode.getHCost() < bestNodeSoFar.getHCost()){
                // todo only exchange if can reserve indefinitely
                bestNodeSoFar = bestOpenListNode;
            }

            ArrayList<CHNode> successors = getValidSuccessors(bestOpenListNode, destination);

            //Check if destination is reached
            for (CHNode successor : successors)
                if (successor.getGridCoordinate().equals(destination)) return successor.getPath();

            openList.addAll(successors);

            iterationCount++;
            if (iterationCount > MAXIMUM_ITERATIONS)
                break;
        }

        // If the best partial path consists only of the initial node,
        // the robot must be blocked by at least one neighbouring robot
        if (initialNode.equals(bestNodeSoFar)) {
            ArrayList<Direction> directionsTowardDestination = getDirectionsOf(initialNode.getGridCoordinate(), destination);
            ReservationManager resManager = server.getReservationManager();

            // Check to see if any of the neighbours (in the direction of the destination) are blocking the robot
            for(Direction direction : directionsTowardDestination){
                GridCoordinate neighbourCoordinate = initialNode.getGridCoordinate().plus(direction);
                boolean isNeighbourReservedForever = resManager.isReservedIndefinitely(neighbourCoordinate);
                if(isNeighbourReservedForever)
                    throw new NextStepBlockedException(start, destination, neighbourCoordinate);
            }
        }

        return bestNodeSoFar.getPath();
    }

    private ArrayList<Direction> getDirectionsOf(GridCoordinate start, GridCoordinate destination) {
        ArrayList<Direction> directions = new ArrayList<>();

        if(start.getX() - destination.getX() < 0){
            directions.add(Direction.EAST);
        }else if(start.getY() - destination.getY() < 0){
            directions.add(Direction.NORTH);
        }else if(start.getX() - destination.getX() > 0){
            directions.add(Direction.WEST);
        }else if(start.getY() - destination.getY() > 0){
            directions.add(Direction.SOUTH);
        }

        return directions;
    }
}
