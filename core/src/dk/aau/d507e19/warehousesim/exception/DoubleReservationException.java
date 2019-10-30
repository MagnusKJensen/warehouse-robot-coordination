package dk.aau.d507e19.warehousesim.exception;

import dk.aau.d507e19.warehousesim.controller.server.Reservation;
import dk.aau.d507e19.warehousesim.controller.server.TimeFrame;

public class DoubleReservationException extends RuntimeException {

    public DoubleReservationException(Reservation existingRes, Reservation newRes) {
        System.err.println("Robot " + newRes.getRobot().getRobotID() + " trying to make a reservation of tile " + newRes.getGridCoordinate() +
                " in timeframe " + newRes.getTimeFrame());
        System.err.println("but that tile is already reserved by robot " + existingRes.getRobot().getRobotID() +
                " in TimeFrame" + existingRes.getTimeFrame());
    }
}
