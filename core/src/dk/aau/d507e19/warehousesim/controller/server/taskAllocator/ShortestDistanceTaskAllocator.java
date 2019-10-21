package dk.aau.d507e19.warehousesim.controller.server.taskAllocator;

import dk.aau.d507e19.warehousesim.controller.robot.Order;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;

import java.util.ArrayList;
import java.util.Optional;

public class ShortestDistanceTaskAllocator implements TaskAllocator {
    @Override
    public Optional<Robot> findOptimalRobot(ArrayList<Robot> robots, Order order) {
        
        return Optional.empty();
    }
}
