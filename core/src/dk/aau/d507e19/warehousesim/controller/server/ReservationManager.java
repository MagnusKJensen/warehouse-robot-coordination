package dk.aau.d507e19.warehousesim.controller.server;

import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.exception.DoubleReservationException;
import dk.aau.d507e19.warehousesim.storagegrid.BinTile;
import dk.aau.d507e19.warehousesim.storagegrid.StorageGrid;
import dk.aau.d507e19.warehousesim.storagegrid.Tile;

import java.util.*;

public class ReservationManager {

    private final ReservationTile[][] reservationTiles;
    private final HashMap<Robot, ArrayList<Reservation>> robotReservationsMap;
    private Server server;
    private ArrayList<BinTile> reservedBinTiles = new ArrayList<>();

    public ReservationManager(int width, int height, Server server) {
        reservationTiles = new ReservationTile[width][height];
        robotReservationsMap = new HashMap<>();
        intiTiles(width, height);
        this.server = server;
    }

    private void intiTiles(int width, int height) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                reservationTiles[x][y] = new ReservationTile(new GridCoordinate(x, y));
            }
        }
    }

    public void reserve(Robot robot, GridCoordinate resCoordinate, TimeFrame timeFrame) throws DoubleReservationException {
        Reservation reservation = new Reservation(robot, resCoordinate, timeFrame);
        reservationTiles[resCoordinate.getX()][resCoordinate.getY()].addReservation(reservation);

        mapReservation(reservation);
    }

    public boolean canReserve(GridCoordinate gridCoordinate, TimeFrame timeFrame) {
        int x = gridCoordinate.getX(), y = gridCoordinate.getY();
        return !reservationTiles[x][y].isReserved(timeFrame);
    }

    public boolean isReserved(GridCoordinate gridCoordinate, TimeFrame timeFrame) {
        return reservationTiles[gridCoordinate.getX()][gridCoordinate.getY()].isReserved(timeFrame);
    }

    public HashSet<Robot> whoReserved(GridCoordinate gridCoordinate) {
        return whoReserved(gridCoordinate, TimeFrame.ALL_TIME);
    }

    public HashSet<Robot> whoReserved(GridCoordinate gridCoordinate, TimeFrame timeFrame) {
        int x = gridCoordinate.getX(), y = gridCoordinate.getY();
        ArrayList<Reservation> reservations = reservationTiles[x][y].getOverlappingReservations(timeFrame);
        HashSet<Robot> robots = new HashSet<>();

        for (Reservation reservation : reservations) {
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

    public ArrayList<Reservation> getReservationsBy(Robot robot) {
        if (!robotReservationsMap.containsKey(robot))
            robotReservationsMap.put(robot, new ArrayList<>());
        return robotReservationsMap.get(robot);
    }

    public void removeOutdatedReservationsBy(Robot robot) {
        ArrayList<Reservation> reservations = getReservationsBy(robot);
        ArrayList<Reservation> outdatedReservations = new ArrayList<>();
        for (Reservation reservation : reservations) {
            if (reservation.getTimeFrame().isOutdated(server.getTimeInTicks()))
                outdatedReservations.add(reservation);
        }

        reservations.removeAll(outdatedReservations);
    }

    private void addRobotKeyIfNotPresent(Robot robot) {
        if (robotReservationsMap.containsKey(robot))
            robotReservationsMap.put(robot, new ArrayList<>());
    }

    public void removeReservation(Reservation reservation) {
        int x = reservation.getGridCoordinate().getX(), y = reservation.getGridCoordinate().getY();
        reservationTiles[x][y].removeReservation(reservation);
        robotReservationsMap.get(reservation.getRobot()).remove(reservation);
    }

    public boolean isBinReserved(GridCoordinate gridCoordinate) {
        StorageGrid grid = server.getSimulation().getStorageGrid();
        BinTile tile = (BinTile)grid.getTile(gridCoordinate.getX(), gridCoordinate.getY());
        return reservedBinTiles.contains(tile);
    }

    public void reserve(ArrayList<Reservation> reservations) throws DoubleReservationException {
        for (Reservation reservation : reservations) {
            int x = reservation.getGridCoordinate().getX(), y = reservation.getGridCoordinate().getY();
            reservationTiles[x][y].addReservation(reservation);
            mapReservation(reservation);
        }
    }

    public void reserve(Reservation reservation) throws DoubleReservationException {
        int x = reservation.getGridCoordinate().getX(), y = reservation.getGridCoordinate().getY();
        reservationTiles[x][y].addReservation(reservation);
        mapReservation(reservation);
    }

    public void removeReservationsBy(Robot robot) {
        ArrayList<Reservation> reservations = new ArrayList<>(getReservationsBy(robot));
        for (Reservation res : reservations) {
            removeReservation(res);
        }
    }

    private void mapReservation(Reservation reservation) {
        if (!robotReservationsMap.containsKey(reservation.getRobot()))
            robotReservationsMap.put(reservation.getRobot(), new ArrayList<>());
        robotReservationsMap.get(reservation.getRobot()).add(reservation);
    }

    public boolean isReservedIndefinitely(GridCoordinate gridCoordinate) {
        return reservationTiles[gridCoordinate.getX()][gridCoordinate.getY()].isReservedIndefinitely();
    }

    public boolean hasConflictingReservations(ArrayList<Reservation> reservations) {
        for(Reservation reservation : reservations){
            if(hasConflictingReservations(reservation))
                return true;
        }
        return false;
    }

    public boolean hasConflictingReservations(Reservation reservation){
        int x = reservation.getGridCoordinate().getX();
        int y = reservation.getGridCoordinate().getY();
        ArrayList<Reservation> sameTimeReservations =
                reservationTiles[x][y].getOverlappingReservations(reservation.getTimeFrame());

        // Check if conflicting reservations are made by the same robot
        for(Reservation sameTimeReservation : sameTimeReservations){
            if(!sameTimeReservation.getRobot().equals(reservation.getRobot()))
                return true;
        }

        return false;
    }

    public void reserveBinTiles(ArrayList<BinTile> toReserve){
        reservedBinTiles.addAll(toReserve);
    }

    public void reserveBinTile(BinTile binTile){
        reservedBinTiles.add(binTile);
    }

    public void reserveBinTile(GridCoordinate coords){
        BinTile tile = (BinTile) server.getSimulation().getStorageGrid().getTile(coords.getX(), coords.getY());
        reservedBinTiles.add(tile);
    }

    public void removeBinReservation(BinTile binTile){
        reservedBinTiles.remove(binTile);
    }

    public void removeBinReservation(GridCoordinate coords){
        BinTile tile = (BinTile) server.getSimulation().getStorageGrid().getTile(coords.getX(), coords.getY());
        reservedBinTiles.remove(tile);
    }

    public ArrayList<BinTile> getReservedBinTiles() {
        return reservedBinTiles;
    }

    public ArrayList<Reservation> getConflictingReservations(Reservation newReservation) {
        int x = newReservation.getGridCoordinate().getX();
        int y = newReservation.getGridCoordinate().getY();

        ArrayList<Reservation> conflictingReservations;
        conflictingReservations = reservationTiles[x][y].getOverlappingReservations(newReservation.getTimeFrame());
        conflictingReservations.removeIf((r) -> r.getRobot().equals(newReservation.getRobot()));

        return conflictingReservations;
    }

    public ArrayList<Reservation> getAllReservations(){
        ArrayList<Reservation> reservations = new ArrayList<>();

        for(Robot robot : server.getAllRobots()){
            reservations.addAll(getReservationsBy(robot));
        }

        return reservations;
    }

    public ArrayList<Reservation> removeOutdatedReservations(){
        ArrayList<Reservation> reservations = getAllReservations();
        long currentTime = server.getTimeInTicks();

        for (Reservation res : reservations) {
            if (res.getTimeFrame().isOutdated(currentTime))
                removeReservation(res);
        }

        return reservations;
    }

    public Reservation getIndefiniteReservationsAt(GridCoordinate gridCoordinate){
        int x = gridCoordinate.getX();
        int y = gridCoordinate.getY();
        ArrayList<Reservation> reservations = reservationTiles[x][y]
                .getOverlappingReservations(TimeFrame.indefiniteTimeFrameFrom(server.getTimeInTicks()));

        for(Reservation res : reservations){
            if(res.getTimeFrame().getTimeMode() == TimeFrame.TimeMode.UNBOUNDED) return res;
        }

        throw new IllegalArgumentException("No indefinite reservations at grid coordinate: " + gridCoordinate);
    }
}
