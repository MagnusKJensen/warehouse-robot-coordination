package dk.aau.d507e19.warehousesim.controller.pathAlgorithms.chp;

import dk.aau.d507e19.warehousesim.TimeUtils;
import dk.aau.d507e19.warehousesim.controller.path.Path;
import dk.aau.d507e19.warehousesim.controller.path.Step;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.DummyPathFinder;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.PartialPathFinder;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.PathFinder;
import dk.aau.d507e19.warehousesim.controller.robot.*;
import dk.aau.d507e19.warehousesim.controller.robot.plan.task.ReservationNavigation;
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
    private static final long PARTIAL_PATH_MAX_ITERATIONS = 300;
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


    public static PathFinder timedCHPathfinder(GridBounds gridBounds, RobotController robotController) {
        return new CHPathfinder(gridBounds, new TimeHeuristic(), new TimeGCostCalculator(), robotController);
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
    public Path calculatePath(GridCoordinate start, GridCoordinate destination) {
        PriorityQueue<CHNode> openList = new PriorityQueue<>();
        final CHNode initialNode = nodeFactory.createInitialNode(start, destination);
        openList.add(initialNode);

        // The default best partial path is the starting node
        CHNode bestNodeSoFar = initialNode;

        int iterationCount = 0;
        while (!openList.isEmpty()) {
            CHNode bestOpenListNode = openList.poll();

            // Check if this node is better than the currently best known node
            /*if(bestOpenListNode.getHCost() == bestNodeSoFar.getHCost()){
                if(bestOpenListNode.getFCost() < bestNodeSoFar.getFCost() && canReservePath(bestOpenListNode.getPath())){
                    bestNodeSoFar = bestOpenListNode;
                }
            }else if(bestOpenListNode.getHCost() < bestNodeSoFar.getHCost()){
                if(canReservePath(bestOpenListNode.getPath())){
                    bestNodeSoFar = bestOpenListNode;
                }
            }*/

            if (bestOpenListNode.getFCost() <= bestNodeSoFar.getFCost()
                    && bestOpenListNode.getHCost() < bestNodeSoFar.getHCost()) {
                if (canReservePath(bestOpenListNode.getPath()))
                    bestNodeSoFar = bestOpenListNode;
            }

            ArrayList<CHNode> successors = getValidSuccessors(bestOpenListNode, destination);

            //Check if destination is reached
            for (CHNode successor : successors)
                if (successor.getGridCoordinate().equals(destination)) return successor.getPath();

            openList.addAll(successors);

            iterationCount++;
            if (iterationCount > PARTIAL_PATH_MAX_ITERATIONS)
                break;
        }

        // If the best partial path consists only of the initial node,
        // the robot must be blocked by at least one neighbouring robot
        if (initialNode.equals(bestNodeSoFar))
            return Path.oneStepPath(new Step(initialNode.getGridCoordinate()));

        return bestNodeSoFar.getPath();
    }

    private boolean canReservePath(Path path) {
        ArrayList<Reservation> reservations = MovementPredictor.calculateReservations(robotController.getRobot(), path, server.getTimeInTicks(), 0);
        reservations.add(ReservationNavigation.createLastTileIndefiniteReservation(reservations));
        return !server.getReservationManager().hasConflictingReservations(reservations);
    }
}
