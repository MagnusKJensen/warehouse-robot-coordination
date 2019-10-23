package dk.aau.d507e19.warehousesim.exception;

import dk.aau.d507e19.warehousesim.controller.server.Reservation;
import dk.aau.d507e19.warehousesim.controller.server.TimeFrame;

public class DoubleReservationException extends RuntimeException {

    public DoubleReservationException(Reservation res1, Reservation res2) {
        System.err.println("Robot " + res1.getRobot().getRobotID() + " trying to make a reservation of tile " + res1.getGridCoordinate() +
                " in timeframe " + res1.getTimeFrame() );
        System.err.println("but that tile is already reserved by robot " + +res2.getRobot().getRobotID() +
                " in TimeFrame" + res2.getTimeFrame());
    }
}
