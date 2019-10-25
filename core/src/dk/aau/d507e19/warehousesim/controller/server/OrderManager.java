package dk.aau.d507e19.warehousesim.controller.server;

import dk.aau.d507e19.warehousesim.controller.robot.Order;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.robot.plan.task.Task;
import dk.aau.d507e19.warehousesim.controller.server.order.OrderNew;
import dk.aau.d507e19.warehousesim.controller.server.taskAllocator.DummyTaskAllocator;
import dk.aau.d507e19.warehousesim.controller.server.taskAllocator.NaiveShortestDistanceTaskAllocator;
import dk.aau.d507e19.warehousesim.controller.server.taskAllocator.ShortestDistanceTaskAllocator;
import dk.aau.d507e19.warehousesim.controller.server.taskAllocator.TaskAllocator;
import dk.aau.d507e19.warehousesim.storagegrid.BinTile;
import dk.aau.d507e19.warehousesim.storagegrid.PickerTile;

import java.util.ArrayList;
import java.util.Optional;

public class OrderManager {
    private ArrayList<Order> ordersFinished = new ArrayList<>();
    private ArrayList<Order> orderQueue = new ArrayList<>();
    private ArrayList<OrderNew> orderQueueNew = new ArrayList<>();
    private Server server;
    private TaskAllocator taskAllocator;
    private ArrayList<OrderNew> ordersProcessing = new ArrayList<>();
    private ArrayList<Task> tasksAvailable = new ArrayList<>();

    public OrderManager(Server server) {
        this.server = server;
        this.taskAllocator = generateTaskAllocator();
    }

    private TaskAllocator generateTaskAllocator() {
        switch (server.getSimulation().getSimulationApp().getTaskAllocatorSelected()){
            // If a task allocator is added, also add it to the side menu at ui.TaskAllocationDropDown.createDropDown()
            case "DummyTaskAllocator" : return new DummyTaskAllocator();
            case "ShortestDistanceTaskAllocator" : return new ShortestDistanceTaskAllocator(server.getSimulation().getStorageGrid());
            case "NaiveShortestDistanceTaskAllocator" : return new NaiveShortestDistanceTaskAllocator(server.getSimulation().getStorageGrid());
            default : throw new IllegalArgumentException("Could not identify task allocator " + server.getSimulation().getSimulationApp().getTaskAllocatorSelected());
        }
    }

    public boolean takeOrder(Order order){
        if(!orderIsServiceable(order)) return false;
        else {
            this.orderQueue.add(order);
            for(int i = 0; i < order.getAmount(); i++){
                server.getProductsAvailable().remove(order.getProduct());
            }
            return true;
        }
    }

    public boolean takeOrder(OrderNew order){
        if(isOrderServiceable(order)) return false;
        else {
            // Divide into RetrievalTasks ??
            server.getProductsAvailable().removeAll(order.getAllProductsInOrder());
            this.orderQueueNew.add(order);
            return true;
        }
    }

    private boolean isOrderServiceable(OrderNew order){
        return server.getProductsAvailable().containsAll(order.getAllProductsInOrder());
    }

    private boolean orderIsServiceable(Order order) {
        // TODO: 18/10/2019 Also maybe assign two or more robots if two more bins needs to be picked up?
        //  Divide order into more, maybe? - Philip
        ArrayList<BinTile> tilesWithProd = server.getTilesContaining(order.getProduct().getSKU());

        // If the product is not in grid, reject
        if(tilesWithProd.isEmpty()) return false;

        // If the tile is in grid, get tile with the correct amount
        for (BinTile tile : tilesWithProd) {
            if(tile.getBin() != null){
                if(tile.getBin().hasProducts(order.getProduct(), order.getAmount()))
                    return true;
            }
        }

        System.out.println("Rejected order: " + order);
        return false;
    }

    // Should be replaced by updateNew
    public void update(){
        if(server.hasAvailableRobot() && orderQueue.size() > 0){
            for(int i = 0; i < orderQueue.size(); ++i){
                Order order = orderQueue.get(i);
                Optional<Robot> optimalRobot = taskAllocator.findOptimalRobot(server.getAllRobots(), order);
                if(optimalRobot.isPresent()){
                    optimalRobot.get().assignOrder(order);
                    ordersFinished.add(orderQueue.get(i));
                    System.out.println("Commenced order: " + orderQueue.get(i));
                    orderQueue.remove(i);
                    break;
                }
            }
        }
    }

    public void updateNew(){
        ArrayList<PickerTile> availablePickers = server.getAvailablePickers();
        if(availablePickers.size() != 0 && orderQueueNew.size() > 0){
            availablePickers.get(0).assignOrder(orderQueueNew.get(0));
            ordersProcessing.add(orderQueueNew.get(0));
            tasksAvailable.addAll(createTasksFromOrder(orderQueueNew.get(0)));
            System.out.println("Commenced order: " + orderQueueNew.get(0));
            orderQueueNew.remove(0);
        }
        if(server.hasAvailableRobot()) {
            for(Task task : tasksAvailable){
                Optional<Robot> optimalRobot = taskAllocator.findOptimalRobot(server.getAllRobots(), task);
                if(optimalRobot.isPresent()){
                    optimalRobot.get().assignTask(task);
                }
                tasksAvailable.remove(task);
            }
        }
    }

    private ArrayList<Task> createTasksFromOrder(OrderNew orderNew) {
        return new ArrayList<>();
    }

    public int ordersInQueue(){
        return orderQueue.size();
    }

}
