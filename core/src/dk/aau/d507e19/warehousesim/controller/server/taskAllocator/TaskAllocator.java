package dk.aau.d507e19.warehousesim.controller.server.taskAllocator;

import dk.aau.d507e19.warehousesim.controller.robot.Order;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.robot.plan.task.Task;

import java.util.ArrayList;
import java.util.Optional;

public interface TaskAllocator {
    Optional<Robot> findOptimalRobot(ArrayList<Robot> robots, Order order);
    Optional<Robot> findOptimalRobot(ArrayList<Robot> robots, Task task);
}
