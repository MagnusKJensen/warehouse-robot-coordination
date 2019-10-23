package dk.aau.d507e19.warehousesim.controller.robot.plan;

import dk.aau.d507e19.warehousesim.SimulationApp;
import dk.aau.d507e19.warehousesim.WarehouseSpecs;
import dk.aau.d507e19.warehousesim.controller.path.Path;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.PathFinder;
import dk.aau.d507e19.warehousesim.controller.robot.*;
import dk.aau.d507e19.warehousesim.controller.server.Reservation;
import dk.aau.d507e19.warehousesim.controller.server.ReservationManager;
import dk.aau.d507e19.warehousesim.controller.server.Server;
import dk.aau.d507e19.warehousesim.controller.server.TimeFrame;
import dk.aau.d507e19.warehousesim.storagegrid.BinTile;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;

public class OrderPlanner {

    private Robot robot;
    private PathFinder pathFinder;
    private Server server;
    private RobotController robotController;
    private int padding = 0;

    public OrderPlanner(RobotController robotController) {
        this.pathFinder = robotController.getPathFinder();
        this.server = robotController.getServer();
        this.pathFinder = robotController.getPathFinder();
        this.robot = robotController.getRobot();
        this.server = robotController.getServer();
        this.robotController = robotController;
    }

    public ArrayList<Action> planPickUp(Order order){
        ArrayList<Action> plan = new ArrayList<>();
        GridCoordinate pickUpPoint = getNearestAvailableProduct(order);

        Optional<Path> pathToPickUpPoint = pathFinder.calculatePath(robot.getGridCoordinate(), pickUpPoint);

        if(pathToPickUpPoint.isPresent()){
            plan.add(new PathTraversal(robot, pathToPickUpPoint.get()));
            plan.add(new PickUp(robot));

            // TODO: 18/10/2019 Only reserve, if the robot is not already on the correct tile. Should however still reserve it's own position then.
            server.getReservationManager().removeReservationsBy(robot);
            ArrayList<Reservation> reservations =
                    MovementPredictor.calculateReservations(robot, pathToPickUpPoint.get(), server.getTimeInTicks(), padding);

            server.getReservationManager().reserve(reservations);

            // todo Temporary solution
            reserveLastTileIndefinitely(reservations);
        } else throw new RuntimeException("Could not plan pickUp");

        return plan;
    }

    private void reserveLastTileIndefinitely(ArrayList<Reservation> reservations){
        Reservation lastReservation = reservations.get(reservations.size() - 1);
        TimeFrame unboundedTimeFrame = TimeFrame
                .indefiniteTimeFrameFrom(lastReservation.getTimeFrame().getEnd() + 1);
        Reservation unboundedReservation = new Reservation(robot, lastReservation.getGridCoordinate(),
                unboundedTimeFrame);
        server.getReservationManager().reserve(unboundedReservation);
    }

    public ArrayList<Action> planDelivery(Order order){
        ArrayList<Action> plan = new ArrayList<>();
        GridCoordinate deliveryPoint = getNearestAvailablePicker();
        Optional<Path> pathToDeliveryPoint = pathFinder.calculatePath(robot.getGridCoordinate(), deliveryPoint);

        if(pathToDeliveryPoint.isPresent()){
            plan.add(new PathTraversal(robot, pathToDeliveryPoint.get()));
            plan.add(new Delivery(robot, order));

            server.getReservationManager().removeReservationsBy(robot);

            ArrayList<Reservation> reservations =
                    MovementPredictor.calculateReservations(robot, pathToDeliveryPoint.get(), server.getTimeInTicks(), padding);
            server.getReservationManager().reserve(reservations);
            // todo Temporary solution
            reserveLastTileIndefinitely(reservations);
        } else throw new RuntimeException("Could not plan planDelivery");


        return plan;
    }

    public ArrayList<Action> planBinReturn(){
        ArrayList<Action> plan = new ArrayList<>();
        // TODO: 15/10/2019 Find empty tile
        Optional<Path> pathToEmptyTile = pathFinder.calculatePath(robot.getGridCoordinate(), robot.getLastPickUp());

        if(pathToEmptyTile.isPresent()){
            plan.add(new PathTraversal(robot, pathToEmptyTile.get()));
            plan.add(new PlaceBin(robot));

            server.getReservationManager().removeReservationsBy(robot);
            ArrayList<Reservation> reservations =
                    MovementPredictor.calculateReservations(robot, pathToEmptyTile.get(), server.getTimeInTicks(), padding);
            server.getReservationManager().reserve(reservations);
            // todo Temporary solution
            reserveLastTileIndefinitely(reservations);
        } else throw new RuntimeException("Could not plan BinReturn");

        return plan;
    }

    public GridCoordinate getNearestAvailablePicker(){
        // TODO: 15/10/2019 Currently finds nearest, but not nearest AVAILABLE.
        ArrayList<GridCoordinate> pickerPoints = server.getPickerPoints();

        int shortestDistance = -1;
        int newDistance;
        GridCoordinate shortestDistanceGC = null;
        for(GridCoordinate pickerGC : pickerPoints){
            newDistance = calculateDistance(robot.getGridCoordinate(), pickerGC);
            // If no other distance found
            if(shortestDistance == -1) {
                shortestDistance = newDistance;
                shortestDistanceGC = pickerGC;
            }
            // Compare to currently shortest distance
            else if(newDistance < shortestDistance) {
                shortestDistance = newDistance;
                shortestDistanceGC = pickerGC;
            }
        }

        return shortestDistanceGC;
    }

    private int calculateDistance(GridCoordinate source, GridCoordinate dest) {
        // distance = abs(ydistance) + abs(xdistance)
        return Math.abs(source.getX() - dest.getX()) + Math.abs(source.getY() - dest.getY());
    }

    private GridCoordinate getNearestAvailableProduct(Order order){
        // TODO: 15/10/2019 Finds the nearest, but does not check if it is reserved - Philip
        ArrayList<BinTile> tilesWithProd = server.getTilesContaining(order.getProduct().getSKU());
        ArrayList<BinTile> tilesWithEnoughProds = new ArrayList<>();

        // If the tile is in grid, get tile with the correct amount
        boolean hasIdleRobotOnTop;
        for (BinTile tile : tilesWithProd) {
            hasIdleRobotOnTop = false;
            if(tile.getBin() != null){
                if(tile.getBin().hasProducts(order.getProduct(), order.getAmount()))

                    // TODO: 21/10/2019 VERY TEMP! An idle robot on top of the product, should not stop the robot from getting it! - Philip
                    for (Robot robot : server.getAllRobots()) {
                        if(robot.getApproximateGridCoordinate().equals(new GridCoordinate(tile.getPosX(), tile.getPosY())) &&
                                robot.getCurrentStatus() == Status.AVAILABLE &&
                                this.robot.getRobotID() != robot.getRobotID()
                        ) hasIdleRobotOnTop = true;
                    }

                    if(!hasIdleRobotOnTop) tilesWithEnoughProds.add(tile);
            }
        }

        // Find coordinate of closest tile with the products
        int shortestDistance = -1;
        int newDistance = 0;
        GridCoordinate shortestGC = null;
        GridCoordinate newGC = null;
        for (BinTile tile : tilesWithEnoughProds) {
            if(shortestDistance == -1) {
                shortestGC = new GridCoordinate(tile.getPosX(), tile.getPosY());
                shortestDistance = calculateDistance(robot.getGridCoordinate(),shortestGC);
            }
            else {
                newGC = new GridCoordinate(tile.getPosX(), tile.getPosY());
                newDistance = calculateDistance(robot.getGridCoordinate(), newGC);
                if(newDistance < shortestDistance) {
                    shortestDistance = newDistance;
                    shortestGC = newGC;
                }
            }
        }
        if(shortestGC == null) throw new RuntimeException("No nearest product available found");

        return shortestGC;
    }


}
