package dk.aau.d507e19.warehousesim.controller.server.taskAllocator;

import dk.aau.d507e19.warehousesim.controller.path.Path;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.robot.Status;
import dk.aau.d507e19.warehousesim.controller.robot.plan.task.BinDelivery;
import dk.aau.d507e19.warehousesim.controller.robot.plan.task.Task;
import dk.aau.d507e19.warehousesim.controller.server.Server;
import dk.aau.d507e19.warehousesim.exception.NoPathFoundException;
import dk.aau.d507e19.warehousesim.storagegrid.StorageGrid;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

public class NaiveShortestDistanceTaskAllocator extends TaskAllocator {
    private StorageGrid grid;
    private Server server;

    public NaiveShortestDistanceTaskAllocator(StorageGrid grid, Server server) {
        this.server = server;
        this.grid = grid;
    }

    @Override
    public void update() {
        Iterator<BinDelivery> taskIterator = getTaskIterator();
        while (taskIterator.hasNext()) {
            Task task = taskIterator.next();
            Optional<Robot> optimalRobot = findOptimalRobot(server.getAllRobots(), task);
            if (optimalRobot.isPresent()) {
                assignTask(task, optimalRobot.get());
                taskIterator.remove();
            }
        }
    }

    public Optional<Robot> findOptimalRobot(ArrayList<Robot> robots, Task task) {
        Robot optimalRobot = null;

        ArrayList<Robot> availableRobots = findAvailableRobots(robots);

        int shortestDistance = -1;
        int newDistance;
        for (Robot robot : availableRobots) {
            newDistance = calculateDistance(robot.getApproximateGridCoordinate(), ((BinDelivery) task).getBinCoords());
            if (shortestDistance == -1 || newDistance < shortestDistance) {
                shortestDistance = newDistance;
                optimalRobot = robot;
            }
        }

        if (optimalRobot != null) return Optional.of(optimalRobot);
        return Optional.empty();
    }

    private ArrayList<Robot> findAvailableRobots(ArrayList<Robot> robots) {
        ArrayList<Robot> availableRobots = new ArrayList<>();
        for (Robot robot : robots) {
            if (robot.getCurrentStatus() == Status.AVAILABLE) availableRobots.add(robot);
        }

        return availableRobots;
    }

    private int calculateDistance(GridCoordinate source, GridCoordinate dest) {
        // distance = abs(ydistance) + abs(xdistance)
        return Math.abs(source.getX() - dest.getX()) + Math.abs(source.getY() - dest.getY());
    }


}
