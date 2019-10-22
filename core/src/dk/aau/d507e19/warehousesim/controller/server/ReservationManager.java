package dk.aau.d507e19.warehousesim.controller.server;

import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;

public class ReservationManager {

    private final ReservationTile[][] reservationTiles;
    private final HashMap<Robot, ArrayList<Reservation>> robotReservationsMap;
    private Server server;

    public ReservationManager(int width, int height, Server server){
        reservationTiles = new ReservationTile[width][height];
        robotReservationsMap = new HashMap<>();
        intiTiles(width, height);
        this.server = server;
    }

    private void intiTiles(int width, int height) {
        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                reservationTiles[x][y] = new ReservationTile(new GridCoordinate(x, y));
            }
        }
    }

    public void reserve(Robot robot, GridCoordinate resCoordinate, TimeFrame timeFrame){
        Reservation reservation = new Reservation(robot, resCoordinate, timeFrame);
        reservationTiles[resCoordinate.getX()][resCoordinate.getY()].addReservation(reservation);

        mapReservation(reservation);
    }

    public boolean canReserveIndefinitely(GridCoordinate gridCoordinate, TimeFrame timeFrame){

        return false;
    }

    public boolean isReserved(GridCoordinate gridCoordinate, TimeFrame timeFrame){
        return reservationTiles[gridCoordinate.getX()][gridCoordinate.getY()].isReserved(timeFrame);
    }

    public HashSet<Robot> whoReserved(GridCoordinate gridCoordinate){
        return whoReserved(gridCoordinate, TimeFrame.ALL_TIME);
    }

    public HashSet<Robot> whoReserved(GridCoordinate gridCoordinate, TimeFrame timeFrame){
        int x = gridCoordinate.getX(), y = gridCoordinate.getY();
        ArrayList<Reservation> reservations = reservationTiles[x][y].getReservations(timeFrame);
        HashSet<Robot> robots = new HashSet<>();

        for(Reservation reservation : reservations){
            robots.add(reservation.getRobot());
        }

        return robots;
    }
    public ArrayList<Reservation> getAllCurrentReservations(Long currentTimeInTicks){
        ArrayList<Reservation> reservations = new ArrayList<>();
        for (int x = 0; x<reservationTiles.length; x++){
            for (int y = 0; y<reservationTiles[x].length; y++){
                if(reservationTiles[x][y].getCurrentReservation(currentTimeInTicks).isPresent()){
                    Optional<Reservation> reservation = reservationTiles[x][y].getCurrentReservation(currentTimeInTicks);
                    reservation.ifPresent(reservations::add);
                }
            }
        }
        return reservations;
    }

    public ArrayList<Reservation> getReservationsBy(Robot robot){
        if(!robotReservationsMap.containsKey(robot))
            robotReservationsMap.put(robot, new ArrayList<>());
        return robotReservationsMap.get(robot);
    }

    public void removeOutdatedReservationsBy(Robot robot){
        ArrayList<Reservation> reservations = getReservationsBy(robot);
        ArrayList<Reservation> outdatedReservations = new ArrayList<>();
        for(Reservation reservation : reservations){
            if(reservation.getTimeFrame().isOutdated(server.getTimeInTicks()))
                outdatedReservations.add(reservation);
        }

        reservations.removeAll(outdatedReservations);
    }

    private void addRobotKeyIfNotPresent(Robot robot){
        if(robotReservationsMap.containsKey(robot))
            robotReservationsMap.put(robot, new ArrayList<>());
    }

    public void removeReservation(Reservation reservation){
        int x = reservation.getGridCoordinate().getX(), y = reservation.getGridCoordinate().getY();
        reservationTiles[x][y].removeReservation(reservation);
    }

    public boolean isBinReserved(GridCoordinate gridCoordinate){
        // Should maybe be independent of time?
        return false;
    }

    public void reserve(ArrayList<Reservation> reservations) {
        for(Reservation reservation : reservations){
            int x = reservation.getGridCoordinate().getX(), y = reservation.getGridCoordinate().getY();
            reservationTiles[x][y].addReservation(reservation);
            mapReservation(reservation);

        }
    }

    private void mapReservation(Reservation reservation) {
        if(!robotReservationsMap.containsKey(reservation.getRobot()))
            robotReservationsMap.put(reservation.getRobot(), new ArrayList<>());
        robotReservationsMap.get(reservation.getRobot()).add(reservation);
    }

}
