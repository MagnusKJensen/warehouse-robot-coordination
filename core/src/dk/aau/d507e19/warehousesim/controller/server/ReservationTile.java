package dk.aau.d507e19.warehousesim.controller.server;

import dk.aau.d507e19.warehousesim.SimulationApp;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.exception.DoubleReservationException;

import java.util.ArrayList;
import java.util.Collections;

public class ReservationTile {

    private ArrayList<Reservation> reservations = new ArrayList<>();
    private GridCoordinate coordinate;

    public ReservationTile(GridCoordinate coordinate){
        this.coordinate = coordinate;
    }

    public boolean isReserved(TimeFrame timeFrame){
        for(Reservation reservation : reservations){
            if(reservation.getTimeFrame().overlaps(timeFrame))
                return true;
        }
        return false;
    }

    public ArrayList<Reservation> getReservations(TimeFrame timeFrame){
        ArrayList<Reservation> overlappingReservations = new ArrayList<>();

        for(Reservation reservation : reservations){
            if(reservation.getTimeFrame().overlaps(timeFrame))
                overlappingReservations.add(reservation);
        }

        return overlappingReservations;
    }

    public void addReservation(Reservation reservation) {
        for(Reservation res : reservations){
            if(res.getTimeFrame().overlaps(reservation.getTimeFrame()))
                // TODO: 23/10/2019 Temporary to allow DummyPathFinder to work
                if(!(SimulationApp.pathFinderSelected.equals("DummyPathFinder") || SimulationApp.pathFinderSelected.equals("CustomH - Turns") || SimulationApp.pathFinderSelected.equals("RRT*"))){
                    throw new DoubleReservationException(res, reservation);
                }
        }

        reservations.add(reservation);
    }

    public void removeReservation(Reservation reservation) {
        // TODO: 16/10/2019 add achecks
        reservations.remove(reservation);
    }

    public boolean isReservedIndefinitely() {
        for(Reservation reservation : reservations){
            if(reservation.getTimeFrame().getTimeMode() == TimeFrame.TimeMode.UNBOUNDED)
                return true;
        }
        return false;
    }
}
