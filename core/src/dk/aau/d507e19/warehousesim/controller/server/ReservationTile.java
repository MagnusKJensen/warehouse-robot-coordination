package dk.aau.d507e19.warehousesim.controller.server;

import java.util.ArrayList;

public class ReservationTile {

    ArrayList<Reservation> reservations = new ArrayList<>();

    public ReservationTile(){

    }

    public boolean isReserved(TimeFrame timeFrame){
        return false;
    }

    public ArrayList getReservations(TimeFrame timeFrame){
        return null;
    }



}
