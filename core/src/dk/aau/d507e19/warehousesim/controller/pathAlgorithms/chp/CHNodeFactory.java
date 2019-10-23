package dk.aau.d507e19.warehousesim.controller.pathAlgorithms.chp;

import dk.aau.d507e19.warehousesim.controller.path.Path;
import dk.aau.d507e19.warehousesim.controller.path.Step;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.RobotController;

import java.util.ArrayList;

public class CHNodeFactory {

    private Heuristic heuristic;
    private GCostCalculator gCostCalculator;
    private RobotController robotController;

    public CHNodeFactory(Heuristic heuristic, GCostCalculator gCostCalculator, RobotController robotController) {
        this.heuristic = heuristic;
        this.gCostCalculator = gCostCalculator;
        this.robotController = robotController;
    }

    public CHNode createNode(GridCoordinate nodeCoords, GridCoordinate target, CHNode parent){
        Path newPath = extendPath(parent.getPath(), new Step(nodeCoords));
        double gCost = gCostCalculator.getGCost(newPath, robotController);
        double hCost = heuristic.getHeuristic(parent.getPath(), target, robotController);
        return new CHNode(nodeCoords, parent, newPath, gCost, hCost);
    }

    public CHNode createWaitingNode(CHNode parent, long waitTimeTicks){
        Path newPath = extendPath(parent.getPath(), waitTimeTicks);
        double gCost = gCostCalculator.getGCost(newPath, robotController);
        double hCost = parent.getHCost();
        return new CHNode(newPath.getLastStep().getGridCoordinate(), parent, newPath, gCost, hCost);
    }

    public CHNode createInitialNode(GridCoordinate gridCoordinate){
        Path initialPath = createInitialPath(gridCoordinate);
        return new CHNode(gridCoordinate, initialPath, 0, 0);
    }

    private static Path createInitialPath(GridCoordinate gridCoordinate){
        ArrayList<Step> singleStep = new ArrayList<>();
        singleStep.add(new Step(gridCoordinate));
        return new Path(singleStep);
    }

    private static Path extendPath(Path path, Step step){
        ArrayList<Step> extendedSteps = new ArrayList<>(path.getFullPath());
        extendedSteps.add(step);
        return new Path(extendedSteps);
    }

    private static Path extendPath(Path path, long waitTimeTicks){
        ArrayList<Step> extendedSteps = new ArrayList<>(path.getFullPath());
        Step originalLastStep = extendedSteps.get(extendedSteps.size() - 1);

        if(originalLastStep.isWaitingStep()){ // Extend waiting period if last step is already a waiting step
            extendedSteps.remove(extendedSteps.size() - 1);
            extendedSteps.add(new Step(originalLastStep.getGridCoordinate(),
                    originalLastStep.getWaitTimeInTicks() + waitTimeTicks));
        }else{
            extendedSteps.add(new Step(originalLastStep.getGridCoordinate(), waitTimeTicks));
        }

        return new Path(extendedSteps);
    }




}
