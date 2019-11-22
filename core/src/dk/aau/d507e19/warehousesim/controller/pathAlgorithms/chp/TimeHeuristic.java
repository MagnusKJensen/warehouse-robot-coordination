package dk.aau.d507e19.warehousesim.controller.pathAlgorithms.chp;

import dk.aau.d507e19.warehousesim.controller.path.Path;
import dk.aau.d507e19.warehousesim.controller.path.Step;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.DummyPathFinder;
import dk.aau.d507e19.warehousesim.controller.robot.Direction;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.MovementPredictor;
import dk.aau.d507e19.warehousesim.controller.robot.RobotController;

import java.util.ArrayList;
import java.util.Iterator;

public class TimeHeuristic implements Heuristic {

    private static DummyPathFinder dummyPathFinder = new DummyPathFinder();

    @Override
    public double getHeuristic(Path pathSoFar, GridCoordinate goal, RobotController robotController) {
        Direction currentDirection = Direction.NORTH;

        if(pathSoFar.getStrippedPath().size()>1)
            currentDirection = pathSoFar.getLastDirection();

        Path simplifiedRemainingPath = simplePath(pathSoFar.getLastStep().getGridCoordinate(), goal, currentDirection);

        simplifiedRemainingPath.getFullPath().remove(0);
        if(simplifiedRemainingPath.getFullPath().size() <= 1) return 0;
        Path fullSimplePath = Path.join(pathSoFar, simplifiedRemainingPath);

        long totalTime = MovementPredictor.timeToTraverse(robotController.getRobot(), fullSimplePath);
        if(pathSoFar.getFullPath().size() <= 1) return totalTime;
        long firstPartTime = MovementPredictor.timeToTraverse(robotController.getRobot(), pathSoFar);

        return totalTime - firstPartTime;
    }

    private long timeToReach(Step firstStepOfRemainingPath, Path fullSimplePath) {

        for(Step s : fullSimplePath.getFullPath()){

        }

        throw new IllegalArgumentException("Given step is not contained in the given path");
    }

    private static Path simplePath(GridCoordinate start, GridCoordinate end, Direction preferredStartDirection){
        return dummyPathFinder.calculatePath(start, end, preferredStartDirection);
    }
}
