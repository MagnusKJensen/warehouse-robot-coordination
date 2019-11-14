package dk.aau.d507e19.warehousesim.controller.pathAlgorithms.chp;

import dk.aau.d507e19.warehousesim.SimulationApp;
import dk.aau.d507e19.warehousesim.controller.path.Path;
import dk.aau.d507e19.warehousesim.controller.path.Step;
import dk.aau.d507e19.warehousesim.controller.robot.RobotController;

public class DistanceTurnGCost implements GCostCalculator {
    private final double waitingPenalty = 1.5;

    @Override
    public double getGCost(Path path, RobotController robotController) {
        double gCost = 0d;
        gCost += path.getFullPath().size();
        gCost += Math.max((path.getLines().size() - 1), 0) * DistanceTurnHeuristic.stoppingCost;
        gCost += calculateWaitPenalty(path);
        return gCost;
    }

    private double calculateWaitPenalty(Path path) {
        double penalty = 0;
        for(Step step : path.getFullPath()){
            if (step.isWaitingStep())
                penalty += (double) step.getWaitTimeInTicks() / (double) SimulationApp.TICKS_PER_SECOND;
        }
        return penalty;
    }

}
