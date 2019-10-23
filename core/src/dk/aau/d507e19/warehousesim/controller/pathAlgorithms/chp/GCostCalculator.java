package dk.aau.d507e19.warehousesim.controller.pathAlgorithms.chp;

import dk.aau.d507e19.warehousesim.controller.path.Path;
import dk.aau.d507e19.warehousesim.controller.robot.RobotController;

public interface GCostCalculator {

    double getGCost(Path path, RobotController robotController);

}
