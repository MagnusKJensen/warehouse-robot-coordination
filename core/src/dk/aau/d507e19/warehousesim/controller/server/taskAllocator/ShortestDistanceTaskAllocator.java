package dk.aau.d507e19.warehousesim.controller.server.taskAllocator;

import dk.aau.d507e19.warehousesim.controller.path.Path;
import dk.aau.d507e19.warehousesim.controller.path.Step;
import dk.aau.d507e19.warehousesim.controller.robot.*;
import dk.aau.d507e19.warehousesim.controller.robot.plan.task.Task;
import dk.aau.d507e19.warehousesim.storagegrid.StorageGrid;

import java.util.ArrayList;
import java.util.Optional;

public class ShortestDistanceTaskAllocator implements TaskAllocator {
    private StorageGrid grid;

    public ShortestDistanceTaskAllocator(StorageGrid grid) {
        this.grid = grid;
    }

    @Override
    public Optional<Robot> findOptimalRobot(ArrayList<Robot> robots, Task task) {
        return Optional.empty();
    }

    @Override
    public Optional<Robot> findOptimalRobot(ArrayList<Robot> robots, Order order) {
        Robot optimalRobot = null;

        // Find idling robots
        ArrayList<Robot> availableRobots = findAvailableRobots(robots);

        // Find tiles containing the correct products
        // TODO: 21/10/2019 Should check, if the bin is also available, when the robots gets there
        ArrayList<GridCoordinate> tilesWithProducts = grid.tilesWithProducts(order.getProduct(), order.getAmount());

        // Find the robot with the shortest distance to a tile with the products.
        Optional<Path> newPath;
        int shortestDistance = -1;
        int newDistance;
        for(Robot robot : availableRobots){
            for(GridCoordinate gc : tilesWithProducts){
                newPath = robot.getRobotController().getPath(robot.getGridCoordinate(), gc);
                if(newPath.isPresent()){
                    newDistance = calculatePathDistance(newPath.get());
                    if(shortestDistance == -1 || newDistance < shortestDistance) {
                        shortestDistance = newDistance;
                        optimalRobot = robot;
                    }
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

    private int calculatePathDistance(Path path){
        ArrayList<Step> steps = path.getStrippedPath();

        int totalDistance = 0;
        for(int i = 0; i < steps.size() - 1; ++i){
            if(i == 0 && !steps.get(0).isWaitingStep()){
                totalDistance += calculateDistance(steps.get(i).getGridCoordinate(), steps.get(i + 1).getGridCoordinate());
            }
            else if(!steps.get(i).isWaitingStep()) {
                totalDistance += calculateDistance(steps.get(i).getGridCoordinate(), steps.get(i + 1).getGridCoordinate());
            }

        }

        return totalDistance;
    }

    private ArrayList<Robot> findAvailableRobots(ArrayList<Robot> robots){
        ArrayList<Robot> availableRobots = new ArrayList<>();
        for (Robot robot : robots){
            if(robot.getCurrentStatus() == Status.AVAILABLE) availableRobots.add(robot);
        }

        return availableRobots;
    }
}
