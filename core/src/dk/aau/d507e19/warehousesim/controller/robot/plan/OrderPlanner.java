package dk.aau.d507e19.warehousesim.controller.robot.plan;

import dk.aau.d507e19.warehousesim.SimulationApp;
import dk.aau.d507e19.warehousesim.controller.path.Path;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.PathFinder;
import dk.aau.d507e19.warehousesim.controller.robot.*;
import dk.aau.d507e19.warehousesim.controller.server.Reservation;
import dk.aau.d507e19.warehousesim.controller.server.Server;

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

        plan.add(new PathTraversal(robot, pathToPickUpPoint));
        plan.add(new PickUp(robot));

        ArrayList<Reservation> reservations =
                MovementPredictor.calculateReservations(robot, pathToPickUpPoint, server.getTimeInTicks(), 0);

        server.getReservationManager().reserve(reservations);

        return plan;
    }

    public ArrayList<Action> planDelivery(){
        ArrayList<Action> plan = new ArrayList<>();
        GridCoordinate deliveryPoint = getNearestAvailablePicker();
        Path pathToDeliveryPoint = pathFinder.calculatePath(robot.getGridCoordinate(), deliveryPoint);

        plan.add(new PathTraversal(robot, pathToDeliveryPoint));
        plan.add(new Delivery(robot));

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
        // TODO: 15/10/2019 Find free delivery tile
        Random rand = new Random();
        return new GridCoordinate(rand.nextInt(15), 0);
    }

    private GridCoordinate getNearestAvailableProduct(Order order){
        // TODO: 15/10/2019 Find on grid and check if reserved
        Random rand = new Random();
        return new GridCoordinate(SimulationApp.random.nextInt(15), SimulationApp.random.nextInt(14) + 1);
    }


}
