package dk.aau.d507e19.warehousesim.controller.server.taskAllocator;

import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Order;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.robot.Status;
import dk.aau.d507e19.warehousesim.storagegrid.StorageGrid;

import java.util.ArrayList;
import java.util.Optional;

public class NaiveShortestDistanceTaskAllocator implements TaskAllocator {
    private StorageGrid grid;

    public NaiveShortestDistanceTaskAllocator(StorageGrid grid) {
        this.grid = grid;
    }

    @Override
    public Optional<Robot> findOptimalRobot(ArrayList<Robot> robots, Order order) {
        Robot optimalRobot = null;

        // Find idling robots
        ArrayList<Robot> availableRobots = findAvailableRobots(robots);

        ArrayList<GridCoordinate> tilesWithProducts = grid.tilesWithProducts(order.getProduct(), order.getAmount());


        int shortestDistance = -1;
        int newDistance;
        for(Robot robot : availableRobots){
            for (GridCoordinate gc : tilesWithProducts){
                newDistance = calculateDistance(robot.getGridCoordinate(), gc);
                if(newDistance < shortestDistance || shortestDistance == -1){
                    shortestDistance = newDistance;
                    optimalRobot = robot;
                }
            }
        }

        if(optimalRobot != null) return Optional.of(optimalRobot);

        return Optional.empty();
    }

    private ArrayList<Robot> findAvailableRobots(ArrayList<Robot> robots){
        ArrayList<Robot> availableRobots = new ArrayList<>();
        for (Robot robot : robots){
            if(robot.getCurrentStatus() == Status.AVAILABLE) availableRobots.add(robot);
        }

        return availableRobots;
    }

    private int calculateDistance(GridCoordinate source, GridCoordinate dest) {
        // distance = abs(ydistance) + abs(xdistance)
        return Math.abs(source.getX() - dest.getX()) + Math.abs(source.getY() - dest.getY());
    }
}
