package dk.aau.d507e19.warehousesim.controller.robot.plan;

import dk.aau.d507e19.warehousesim.SimulationApp;
import dk.aau.d507e19.warehousesim.WarehouseSpecs;
import dk.aau.d507e19.warehousesim.controller.path.Path;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.PathFinder;
import dk.aau.d507e19.warehousesim.controller.robot.*;
import dk.aau.d507e19.warehousesim.controller.server.Reservation;
import dk.aau.d507e19.warehousesim.controller.server.Server;
import dk.aau.d507e19.warehousesim.storagegrid.BinTile;

import java.util.ArrayList;
import java.util.Random;

public class OrderPlanner {

    private Robot robot;
    private PathFinder pathFinder;
    private Server server;
    private RobotController robotController;

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
        Path pathToPickUpPoint = pathFinder.calculatePath(robot.getGridCoordinate(), pickUpPoint);

        System.out.println(pathToPickUpPoint);

        plan.add(new PathTraversal(robot, pathToPickUpPoint));
        plan.add(new PickUp(robot));

        ArrayList<Reservation> reservations =
                MovementPredictor.calculateReservations(robot, pathToPickUpPoint, server.getTimeInTicks(), 0);

        server.getReservationManager().reserve(reservations);

        return plan;
    }

    public ArrayList<Action> planDelivery(Order order){
        ArrayList<Action> plan = new ArrayList<>();
        GridCoordinate deliveryPoint = getNearestAvailablePicker();
        System.out.println("DeliveryPoint: " + deliveryPoint);
        Path pathToDeliveryPoint = pathFinder.calculatePath(robot.getGridCoordinate(), deliveryPoint);

        plan.add(new PathTraversal(robot, pathToDeliveryPoint));
        plan.add(new Delivery(robot, order));

        ArrayList<Reservation> reservations =
                MovementPredictor.calculateReservations(robot, pathToDeliveryPoint, server.getTimeInTicks(), 0);
        server.getReservationManager().reserve(reservations);

        return plan;
    }

    public ArrayList<Action> planBinReturn(){
        ArrayList<Action> plan = new ArrayList<>();
        // TODO: 15/10/2019 Find empty tile
        Path pathToEmptyTile = pathFinder.calculatePath(robot.getGridCoordinate(), robot.getLastPickUp());

        plan.add(new PathTraversal(robot, pathToEmptyTile));
        plan.add(new PlaceBin(robot));

        ArrayList<Reservation> reservations =
                MovementPredictor.calculateReservations(robot, pathToEmptyTile, server.getTimeInTicks(), 0);
        server.getReservationManager().reserve(reservations);

        return plan;
    }

    public GridCoordinate getNearestAvailablePicker(){
        /*
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

        return shortestDistanceGC;*/

        // TODO: 15/10/2019 Find free delivery tile
        Random rand = new Random();
        GridCoordinate gc = new GridCoordinate(rand.nextInt(WarehouseSpecs.wareHouseWidth - 1), 0);
        System.out.println("Nearest picker : " + gc);
        return new GridCoordinate(rand.nextInt(WarehouseSpecs.wareHouseWidth - 1), 0);
    }

    private int calculateDistance(GridCoordinate source, GridCoordinate dest) {
        // distance = abs(ydistance) + abs(xdistance)
        return Math.abs(source.getX() - dest.getX()) + Math.abs(source.getY() - dest.getY());
    }

    private GridCoordinate getNearestAvailableProduct(Order order){
        /*// TODO: 15/10/2019 Finds the nearest, but does not check if it is reserved - Philip
        ArrayList<BinTile> tilesWithProd = server.getTilesContaining(order.getProduct().getSKU());
        ArrayList<BinTile> tilesWithEnoughProds = new ArrayList<>();

        // If the tile is in grid, get tile with the correct amount
        for (BinTile tile : tilesWithProd) {
            if(tile.getBin() != null){
                if(tile.getBin().hasProducts(order.getProduct(), order.getAmount()))
                    tilesWithEnoughProds.add(tile);
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

        return shortestGC;*/

        // TODO: 15/10/2019 Find on grid and check if reserved
        Random rand = new Random();
        return new GridCoordinate(SimulationApp.random.nextInt(WarehouseSpecs.wareHouseWidth - 1),
                SimulationApp.random.nextInt(WarehouseSpecs.wareHouseHeight - 1) + 1);
    }


}
