package dk.aau.d507e19.warehousesim.controller.robot;

import dk.aau.d507e19.warehousesim.controller.path.Line;
import dk.aau.d507e19.warehousesim.controller.path.Path;
import dk.aau.d507e19.warehousesim.controller.robot.plan.SpeedCalculator;
import dk.aau.d507e19.warehousesim.controller.server.Reservation;

import java.util.ArrayList;

public class MovementPredictor {

    public static ArrayList<Reservation> calculateReservations(Robot robot, Path path, float padding){
        ArrayList<Line> lines = path.getLines();


        return null;
    }


}
