package dk.aau.d507e19.warehousesim.controller.pathAlgorithms.chp;

import dk.aau.d507e19.warehousesim.controller.path.Path;
import dk.aau.d507e19.warehousesim.controller.robot.MovementPredictor;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.robot.RobotController;
import dk.aau.d507e19.warehousesim.controller.server.Reservation;

import java.util.ArrayList;

public class TimeGCostCalculator implements GCostCalculator {

    @Override
    public double getGCost(Path path, RobotController robotController) {
        if(path.getFullPath().size() <= 1) return 0;
        //return - path.getFullPath().size();
        Robot robot = robotController.getRobot();
        ArrayList<Reservation> reservations =
                MovementPredictor.calculateReservations(robotController.getRobot(), path, 0L,0L);
        long startRes = reservations.get(0).getTimeFrame().getStart();
        long endRes = reservations.get(reservations.size() - 1).getTimeFrame().getEnd();
        return endRes - startRes;
    }
}
