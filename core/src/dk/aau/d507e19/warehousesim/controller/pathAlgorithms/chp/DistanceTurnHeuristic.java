package dk.aau.d507e19.warehousesim.controller.pathAlgorithms.chp;

import dk.aau.d507e19.warehousesim.controller.path.Path;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.DummyPathFinder;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.RobotController;

public class DistanceTurnHeuristic implements Heuristic{

    private static final DummyPathFinder dummyPathFinder = new DummyPathFinder();

    public static final double stoppingCost = 0.6d;

    @Override
    public double getHeuristic(Path path, GridCoordinate goal, RobotController robotController) {
        double heuristic = 0d;

        // Straight path to the target
        Path simplifiedRemainingPath = simplePath(path.getLastStep().getGridCoordinate(), goal);
        heuristic += simplifiedRemainingPath.getFullPath().size();

        // Add extra cost for each stop in path
        if(simplifiedRemainingPath.getLines().size() != 0 && simplifiedRemainingPath.getFullPath().size() != 0){
             heuristic += stoppingCost * (double) (simplifiedRemainingPath.getLines().size() - 1);
        }

        return heuristic;
    }

    private static Path simplePath(GridCoordinate start, GridCoordinate end){
        // todo temporary
        return dummyPathFinder.calculatePath(start, end);
    }

}
