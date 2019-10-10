package dk.aau.d507e19.warehousesim.controller.server;

import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;

import java.util.ArrayList;

public class ReservationManager {

    private final ReservationTile[][] tiles;

    public ReservationManager(int width, int height){
        tiles = new ReservationTile[width][height];
    }

    public void reserve(GridCoordinate gridCoordinate, TimeFrame timeFrame){
        // todo exception
    }

    public boolean canReserveIndefinitely(GridCoordinate gridCoordinate){
        return false;
    }

    public boolean isReserved(GridCoordinate gridCoordinate, TimeFrame timeFrame){
        return false;
    }

    public ArrayList<Robot> whoReserved(GridCoordinate gridCoordinate){
        return new ArrayList<>();
    }

    public ArrayList<Reservation> getReservationsBy(Robot robot){
        return null;
    }

    public void removeReservation(Reservation reservation){

    }


}
