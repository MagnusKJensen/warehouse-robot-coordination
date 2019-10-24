package dk.aau.d507e19.warehousesim.controller.robot.plan.task;

import dk.aau.d507e19.warehousesim.controller.robot.Order;
import dk.aau.d507e19.warehousesim.controller.robot.RobotController;

import java.util.ArrayList;

public class ProductDelivery implements Task {

    private Order order;
    private RobotController robotController;

    private ArrayList<Task> subTasks = new ArrayList<>();

    public ProductDelivery(RobotController robotController, Order order) {
        this.robotController = robotController;
        this.order = order;
        planActions();
    }

    private void planActions() {

    }

    @Override
    public void perform() {

    }

    @Override
    public boolean isCompleted() {
        return false;
    }

    @Override
    public boolean hasFailed() {
        return false;
    }
}
