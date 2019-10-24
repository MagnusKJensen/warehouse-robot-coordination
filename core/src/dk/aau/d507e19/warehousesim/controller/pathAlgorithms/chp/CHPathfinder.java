package dk.aau.d507e19.warehousesim.controller.pathAlgorithms.chp;

import dk.aau.d507e19.warehousesim.controller.path.Path;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.PathFinder;
import dk.aau.d507e19.warehousesim.controller.robot.Direction;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.RobotController;
import dk.aau.d507e19.warehousesim.storagegrid.GridBounds;

import java.util.ArrayList;
import java.util.Optional;
import java.util.PriorityQueue;

public class CHPathfinder implements PathFinder {

    private final RobotController robotController;

    private Heuristic heuristic;
    private GCostCalculator gCostCalculator;
    private CHNodeFactory nodeFactory;
    private GridBounds gridBounds;

    private static final long STANDARD_WAIT_TIME_IN_TICKS = 30L;

    public CHPathfinder(GridBounds gridBounds, Heuristic heuristic, GCostCalculator gCostCalculator, RobotController robotController) {
        this.gridBounds = gridBounds;
        this.heuristic = heuristic;
        this.gCostCalculator = gCostCalculator;
        this.robotController = robotController;
        this.nodeFactory = new CHNodeFactory(heuristic, gCostCalculator, robotController);
    }

    @Override
    public Optional<Path> calculatePath(GridCoordinate start, GridCoordinate destination) {
        PriorityQueue<CHNode> openList = new PriorityQueue<>();
        PriorityQueue<CHNode> closedList = new PriorityQueue<>();

        openList.add(nodeFactory.createInitialNode(start));

        while (!openList.isEmpty()){
            CHNode bestCandidate = openList.poll();
            openList.addAll(getSuccessors(bestCandidate, destination));
        }


        return null;
    }

    private ArrayList<CHNode> getSuccessors(CHNode parent, GridCoordinate target) {
        ArrayList<GridCoordinate> neighbourCoords = getValidNeighbours(parent.getGridCoordinate());
        ArrayList<CHNode> successors = new ArrayList<>();

        for(GridCoordinate coord : neighbourCoords){
            successors.add(nodeFactory.createNode(coord, target, parent));
        }

        successors.add(nodeFactory.createWaitingNode(parent, STANDARD_WAIT_TIME_IN_TICKS));

        return successors;
    }

    private CHNode getBestCandidate(ArrayList<CHNode> openList) {
        return null;
    }

    // Returns all neighbours within bounds
    private ArrayList<GridCoordinate> getValidNeighbours(GridCoordinate originalCoords){
        ArrayList<GridCoordinate> neighbours = new ArrayList<>();

        for(Direction dir : Direction.values()){
            int neighbourX = originalCoords.getX() + dir.xDir;
            int neighbourY = originalCoords.getY() + dir.yDir;
            GridCoordinate neighbour = new GridCoordinate(neighbourX, neighbourY);
            if(gridBounds.isWithinBounds(neighbour)) neighbours.add(neighbour);
        }

        return neighbours;
    }

}
