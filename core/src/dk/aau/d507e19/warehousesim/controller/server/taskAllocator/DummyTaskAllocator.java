package dk.aau.d507e19.warehousesim.controller.server.taskAllocator;

import dk.aau.d507e19.warehousesim.controller.robot.MovementPredictor;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.robot.Status;
import dk.aau.d507e19.warehousesim.controller.robot.plan.task.BinDelivery;
import dk.aau.d507e19.warehousesim.controller.robot.plan.task.Task;
import dk.aau.d507e19.warehousesim.controller.server.ReservationManager;
import dk.aau.d507e19.warehousesim.controller.server.Server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class DummyTaskAllocator extends TaskAllocator {

    private Server server;

    public DummyTaskAllocator(Server server) {
        this.server = server;
    }

    @Override
    public void update() {
        if (server.hasAvailableRobot()) {
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
    }

    public Optional<Robot> findOptimalRobot(ArrayList<Robot> robots, Task task) {
        for (Robot robot : robots) {
            if (robot.getCurrentStatus() == Status.AVAILABLE) {
                return Optional.of(robot);
            }
        }

        return Optional.empty();
    }
}
