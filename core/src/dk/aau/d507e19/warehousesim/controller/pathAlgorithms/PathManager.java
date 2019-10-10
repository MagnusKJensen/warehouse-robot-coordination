package dk.aau.d507e19.warehousesim.controller.pathAlgorithms;

import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.server.Reservation;

import java.util.ArrayList;

public class PathManager {
    int gridLength;
    int gridHeight;
    private ArrayList<Reservation> listOfResevations;
    private ArrayList<Reservation>[][] gridOfResevations;

    public PathManager(int gridLength, int gridHeight) {
        this.gridLength = gridLength;
        this.gridHeight = gridHeight;
        gridOfResevations = new ArrayList[gridLength][gridHeight];
        addReservationListsToGrid();
    }

    public void addReservationToList(ArrayList<GridCoordinate> arrayList, float simulatedtime, int robotID, float robotSpeedPerBin) {
        Reservation reservation;
        int i = 1;
        for (GridCoordinate gridcordinate : arrayList) {
            reservation = new Reservation(robotID, simulatedtime + robotSpeedPerBin * i);
            reservation.setxCordinate(gridcordinate.getX());
            reservation.setyCordinate(gridcordinate.getY());

            gridOfResevations[gridcordinate.getX()][gridcordinate.getY()].add(reservation);
            i += 1;
        }

    }

    public void removeReservation(Reservation reservation, int xCordinate, int yCordinate) {
        for (Reservation reservationInList : gridOfResevations[xCordinate][yCordinate]) {
            if (reservationInList.equals(reservation))
                listOfResevations.remove(reservationInList);

        }

    }

    public void addReservationListsToGrid() {
        for (int i = 0; i < gridLength; i++) {
            for (int j = 0; j < gridHeight; j++) {
                listOfResevations = new ArrayList<>();
                gridOfResevations[i][j] = listOfResevations;
            }
        }
    }

    public void printReservations() {
        int g =0;
        for (int i = 0; i < gridLength; i++) {
            for (int j = 0; j < gridHeight; j++) {
                for (Reservation res : gridOfResevations[i][j]) {
                    if (res.robotID == 1) {
                        System.out.println(g);
                        System.out.println(res.toString());
                    g++;
                    }
                }
            }
        }
    }

    public ArrayList<Reservation>[][] getGridOfResevations() {
        return gridOfResevations;
    }
}
