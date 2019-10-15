package dk.aau.d507e19.warehousesim.controller.server;

import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;

import java.util.ArrayList;

public class ReservationTile {

    private ArrayList<Reservation> reservations = new ArrayList<>();
    private GridCoordinate coordinate;

    public ReservationTile(GridCoordinate coordinate){

    }

    public boolean isReserved(TimeFrame timeFrame){
        return false;
    }

    public ArrayList getReservations(TimeFrame timeFrame){
        return null;
    }



}
