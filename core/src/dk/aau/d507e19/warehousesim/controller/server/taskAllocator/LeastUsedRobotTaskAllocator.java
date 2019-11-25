package dk.aau.d507e19.warehousesim.controller.server.taskAllocator;

import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.robot.Status;
import dk.aau.d507e19.warehousesim.controller.robot.plan.task.BinDelivery;
import dk.aau.d507e19.warehousesim.controller.robot.plan.task.Task;
import dk.aau.d507e19.warehousesim.controller.server.Server;
import dk.aau.d507e19.warehousesim.storagegrid.StorageGrid;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

public class LeastUsedRobotTaskAllocator extends TaskAllocator {
    private final Server server;
    private StorageGrid grid;

    LeastUsedRobotTaskAllocator(StorageGrid grid, Server server) {
        this.grid = grid;
        this.server = server;
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
        Robot leastUsedRobot = null;

        ArrayList<Robot> availableRobots = findAvailableRobots(robots);
        if (!availableRobots.isEmpty()) {
            double leastDistanceTraveled = availableRobots.get(0).getDistanceTraveledInMeters();
            for (Robot n : availableRobots) {
                if (leastDistanceTraveled >= n.getDistanceTraveledInMeters()) {
                    leastDistanceTraveled = n.getDistanceTraveledInMeters();
                    leastUsedRobot = n;
                }
            }
        }

        if (leastUsedRobot != null) {
            return Optional.of(leastUsedRobot);
        }
        return Optional.empty();
    }

    private ArrayList<Robot> findAvailableRobots(ArrayList<Robot> robots) {
        ArrayList<Robot> availableRobots = new ArrayList<>();
        for (Robot robot : robots) {
            if (robot.getCurrentStatus() == Status.AVAILABLE) availableRobots.add(robot);
        }
        return availableRobots;
    }


}