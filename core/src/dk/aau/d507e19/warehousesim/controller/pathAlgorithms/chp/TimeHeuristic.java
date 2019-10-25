package dk.aau.d507e19.warehousesim.controller.pathAlgorithms.chp;

import dk.aau.d507e19.warehousesim.controller.path.Path;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.DummyPathFinder;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.MovementPredictor;
import dk.aau.d507e19.warehousesim.controller.robot.RobotController;

import java.util.ArrayList;

public class TimeHeuristic implements Heuristic {

    private static DummyPathFinder dummyPathFinder = new DummyPathFinder();

    @Override
    public double getHeuristic(Path path, GridCoordinate goal, RobotController robotController) {
        Path simplifiedRemainingPath = simplePath(path.getLastStep().getGridCoordinate(), goal);

        simplifiedRemainingPath.getFullPath().remove(0);
        Path fullSimplePath = Path.join(path, simplifiedRemainingPath);

        /*long firstHalfTime;
        if(path.getFullPath().size() <= 1){
            if(path.getLastStep().isWaitingStep()) firstHalfTime = path.getLastStep().getWaitTimeInTicks();
            else firstHalfTime = 0;
        }
        else firstHalfTime = MovementPredictor.timeToTraverse(robotController.getRobot(), path);*/

        long totalTime = MovementPredictor.timeToTraverse(robotController.getRobot(), fullSimplePath);
        return totalTime; /*- firstHalfTime;*/
    }

    private static Path simplePath(GridCoordinate start, GridCoordinate end){
        // todo temporary
        return dummyPathFinder.calculatePath(start, end);
    }
}
