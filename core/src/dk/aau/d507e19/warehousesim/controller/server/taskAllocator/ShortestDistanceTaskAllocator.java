package dk.aau.d507e19.warehousesim.controller.server.taskAllocator;

import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Order;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.robot.Status;
import dk.aau.d507e19.warehousesim.storagegrid.StorageGrid;

import java.util.ArrayList;
import java.util.Optional;

public class ShortestDistanceTaskAllocator implements TaskAllocator {
    private StorageGrid grid;

    public ShortestDistanceTaskAllocator(StorageGrid grid) {
        this.grid = grid;
    }

    @Override
    public Optional<Robot> findOptimalRobot(ArrayList<Robot> robots, Order order) {
        Robot optimalRobot = null;

        ArrayList<Robot> availableRobots = findAvailableRobots(robots);

        ArrayList<GridCoordinate> tilesWithProducts = grid.tilesWithProducts(order.getProduct(), order.getAmount());

        int shortestDistance = -1;
        int newDistance;
        for(Robot robot : availableRobots){
            for(GridCoordinate gc : tilesWithProducts){
                newDistance = calculateDistance(robot.getGridCoordinate(), gc);
                if(shortestDistance == -1 || newDistance < shortestDistance) {
                    shortestDistance = newDistance;
                    optimalRobot = robot;
                }
            }
        }
        if(optimalRobot == null) return Optional.empty();
        
        return Optional.of(optimalRobot);
    }

    private int calculateDistance(GridCoordinate source, GridCoordinate dest) {
        // distance = abs(ydistance) + abs(xdistance)
        return Math.abs(source.getX() - dest.getX()) + Math.abs(source.getY() - dest.getY());
    }

    private ArrayList<Robot> findAvailableRobots(ArrayList<Robot> robots){
        ArrayList<Robot> availableRobots = new ArrayList<>();
        for (Robot robot : robots){
            if(robot.getCurrentStatus() == Status.AVAILABLE) availableRobots.add(robot);
        }

        return availableRobots;
    }
}
