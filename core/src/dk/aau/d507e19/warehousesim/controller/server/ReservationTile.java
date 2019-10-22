package dk.aau.d507e19.warehousesim.controller.server;

import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

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

    public Optional<Reservation> getCurrentReservation(Long timeInTicks){
        for(Reservation n: reservations){
            if (n.getTimeFrame().isWithinTimeFrame(timeInTicks)) {
                return Optional.of(n);
            }
        }
        return Optional.empty();
    }

    public void addReservation(Reservation reservation) {
        for(Reservation res : reservations){
            // todo Check conflicts
        }

        reservations.add(reservation);
    }

    public void removeReservation(Reservation reservation) {
        // TODO: 16/10/2019 add achecks
        reservations.remove(reservation);
    }
}
