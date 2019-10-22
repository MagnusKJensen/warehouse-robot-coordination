package dk.aau.d507e19.warehousesim.controller.pathAlgorithms.chp;

import dk.aau.d507e19.warehousesim.controller.path.Path;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.PathFinder;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.RobotController;

import java.util.ArrayList;

public class CHPathfinder implements PathFinder {

    private final RobotController robotController;

    private Heuristic heuristic;
    private GCostCalculator gCostCalculator;
    private CHNodeFactory nodeFactory;

    public CHPathfinder(Heuristic heuristic, GCostCalculator gCostCalculator, RobotController robotController) {
        this.heuristic = heuristic;
        this.gCostCalculator = gCostCalculator;
        this.robotController = robotController;
        this.nodeFactory = new CHNodeFactory(heuristic, gCostCalculator, robotController);
    }

    @Override
    public Path calculatePath(GridCoordinate start, GridCoordinate destination) {
        ArrayList<CHNode> openList = new ArrayList<>();
        ArrayList<CHNode> closedList = new ArrayList<>();

        openList.add(nodeFactory.createInitialNode(start));



        return null;
    }
}
