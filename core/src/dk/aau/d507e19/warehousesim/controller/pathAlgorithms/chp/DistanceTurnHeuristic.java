package dk.aau.d507e19.warehousesim.controller.pathAlgorithms.chp;

import dk.aau.d507e19.warehousesim.controller.path.Line;
import dk.aau.d507e19.warehousesim.controller.path.Path;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.DummyPathFinder;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.RobotController;

public class DistanceTurnHeuristic implements Heuristic{

    private static final DummyPathFinder dummyPathFinder = new DummyPathFinder();

    public static final double cornerCost = 0.5d;

    @Override
    public double getHeuristic(Path path, GridCoordinate goal, RobotController robotController) {
        double heuristic = 0d;

        Path simplifiedRemainingPath = simplePath(path.getLastStep().getGridCoordinate(), goal);
        simplifiedRemainingPath.getFullPath().remove(0);

        heuristic += simplifiedRemainingPath.getFullPath().size();

        if(path.getLines().size() != 0 && simplifiedRemainingPath.getFullPath().size() != 0){
            Line newLine = new Line(path.getLastStep(), simplifiedRemainingPath.getFullPath().get(0));
            if(path.getLines().get(path.getLines().size() - 1).getDirection() != newLine.getDirection())
                heuristic += cornerCost;
        }

        heuristic += ((double) simplifiedRemainingPath.getLines().size() - 1) * cornerCost;


        return heuristic;
    }

    private static Path simplePath(GridCoordinate start, GridCoordinate end){
        // todo temporary
        return dummyPathFinder.calculatePath(start, end);
    }

}
