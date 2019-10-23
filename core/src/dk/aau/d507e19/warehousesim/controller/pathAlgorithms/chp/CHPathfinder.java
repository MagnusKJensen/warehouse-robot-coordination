package dk.aau.d507e19.warehousesim.controller.pathAlgorithms.chp;

import dk.aau.d507e19.warehousesim.controller.path.Path;
import dk.aau.d507e19.warehousesim.controller.path.Step;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.PathFinder;
import dk.aau.d507e19.warehousesim.controller.robot.*;
import dk.aau.d507e19.warehousesim.controller.server.Reservation;
import dk.aau.d507e19.warehousesim.controller.server.Server;
import dk.aau.d507e19.warehousesim.controller.server.TimeFrame;
import dk.aau.d507e19.warehousesim.storagegrid.GridBounds;

import java.util.ArrayList;
import java.util.Optional;
import java.util.PriorityQueue;

public class CHPathfinder implements PathFinder {

    private static final long MAXIMUM_WAIT_TIME = 90;
    private final RobotController robotController;
    private final Server server;

    private Heuristic heuristic;
    private GCostCalculator gCostCalculator;
    private CHNodeFactory nodeFactory;
    private GridBounds gridBounds;

    private static final long STANDARD_WAIT_TIME_IN_TICKS = 30L;

    public static CHPathfinder defaultCHPathfinder(GridBounds gridBounds, RobotController robotController){
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
    public Optional<Path> calculatePath(GridCoordinate start, GridCoordinate destination) {
        if(start.equals(destination))
            return Optional.of(Path.oneStepPath(new Step(start)));

        if(server.getReservationManager().isReserved(destination, TimeFrame.indefiniteTimeFrameFrom(server.getTimeInTicks() + 400)))
            return Optional.empty();

        PriorityQueue<CHNode> openList = new PriorityQueue<>();
        PriorityQueue<CHNode> closedList = new PriorityQueue<>(); // todo integrate for performance

        openList.add(nodeFactory.createInitialNode(start));

        while (!openList.isEmpty()){
            CHNode bestCandidate = openList.poll();
            closedList.add(bestCandidate);

            ArrayList<CHNode> successors = getValidSuccessors(bestCandidate, destination);

            //Check if destination is reached
            for(CHNode successor : successors)
                if(successor.getGridCoordinate().equals(destination))
                    return Optional.of(successor.getPath());

            openList.addAll(successors);
        }


        return Optional.empty();
    }

    private ArrayList<CHNode> getValidSuccessors(CHNode parent, GridCoordinate target) {
        ArrayList<GridCoordinate> neighbourCoords = getValidNeighbours(parent.getGridCoordinate());
        ArrayList<CHNode> successors = new ArrayList<>();

        for(GridCoordinate coord : neighbourCoords)
            successors.add(nodeFactory.createNode(coord, target, parent));

        successors.add(nodeFactory.createWaitingNode(parent, STANDARD_WAIT_TIME_IN_TICKS));

        successors.removeIf(this::isInvalidNode);

        return successors;
    }

    private boolean isInvalidNode(CHNode node){
        Step lastStep = node.getPath().getLastStep();
        if(lastStep.isWaitingStep() && lastStep.getWaitTimeInTicks() > MAXIMUM_WAIT_TIME)
            return true;

        Robot robot = robotController.getRobot();
        ArrayList<Reservation> reservations =
                MovementPredictor.calculateReservations(robot, node.getPath(), server.getTimeInTicks(), 0);

        Reservation nodeReservation = reservations.get(reservations.size() - 1);

        // todo (Bug: will not ignore it's own reservation)
        return server.getReservationManager().isReserved(node.getGridCoordinate(), nodeReservation.getTimeFrame());
    }

    // Returns all neighbours within bounds
    private ArrayList<GridCoordinate> getValidNeighbours(GridCoordinate originalCoords){
        ArrayList<GridCoordinate> neighbours = new ArrayList<>();

        for(Direction dir : Direction.values()){
            int neighbourX = originalCoords.getX() + dir.xDir;
            int neighbourY = originalCoords.getY() + dir.yDir;
            GridCoordinate neighbour = new GridCoordinate(neighbourX, neighbourY);
            if(gridBounds.isWithinBounds(neighbour))
                neighbours.add(neighbour);
        }

        return neighbours;
    }

}
