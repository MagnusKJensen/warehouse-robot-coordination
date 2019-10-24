package dk.aau.d507e19.warehousesim.controller.pathAlgorithms.chp;

import dk.aau.d507e19.warehousesim.controller.path.Path;
import dk.aau.d507e19.warehousesim.controller.robot.MovementPredictor;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.robot.RobotController;

public class TimeGCostCalculator implements GCostCalculator {
    @Override
    public double getGCost(Path path, RobotController robotController) {
        return - path.getFullPath().size();
        /*Robot robot = robotController.getRobot();

        if(path.getFullPath().size() <= 1){
            if(path.getLastStep().isWaitingStep()) return path.getLastStep().getWaitTimeInTicks();
            return 0;
        }
        return MovementPredictor.timeToTraverse(robot, path);*/
    }
}
