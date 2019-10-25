package dk.aau.d507e19.warehousesim.controller.server;

import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.robot.plan.task.BinDelivery;
import dk.aau.d507e19.warehousesim.controller.robot.plan.task.Task;
import dk.aau.d507e19.warehousesim.controller.server.order.Order;
import dk.aau.d507e19.warehousesim.controller.server.order.OrderLine;
import dk.aau.d507e19.warehousesim.controller.server.taskAllocator.DummyTaskAllocator;
import dk.aau.d507e19.warehousesim.controller.server.taskAllocator.NaiveShortestDistanceTaskAllocator;
import dk.aau.d507e19.warehousesim.controller.server.taskAllocator.ShortestDistanceTaskAllocator;
import dk.aau.d507e19.warehousesim.controller.server.taskAllocator.TaskAllocator;
import dk.aau.d507e19.warehousesim.storagegrid.BinTile;
import dk.aau.d507e19.warehousesim.storagegrid.PickerTile;
import dk.aau.d507e19.warehousesim.storagegrid.StorageGrid;
import dk.aau.d507e19.warehousesim.storagegrid.product.Bin;
import dk.aau.d507e19.warehousesim.storagegrid.product.Product;

import java.util.*;

public class OrderManager {
    private ArrayList<Order> orderQueueNew = new ArrayList<>();
    private Server server;
    private TaskAllocator taskAllocator;
    private ArrayList<Order> ordersProcessing = new ArrayList<>();
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
        if(isOrderServiceable(order)) {
            // Divide into RetrievalTasks ??
            removeProducts(order.getAllProductsInOrder());
            this.orderQueueNew.add(order);
            return true;
        } else {
            System.out.println("Rejected order " + order);
            return false;
        }
    }

    private void removeProducts(ArrayList<Product> productsToRemove){
        for(Product prod : productsToRemove){
            server.getProductsAvailable().remove(prod);
        }
    }

    private boolean isOrderServiceable(Order order){
        return server.getProductsAvailable().containsAll(order.getAllProductsInOrder());
    }

    public void updateNew(){
        ArrayList<PickerTile> availablePickers = server.getAvailablePickers();
        if(availablePickers.size() != 0 && orderQueueNew.size() > 0){
            // Assign order to picker
            availablePickers.get(0).assignOrder(orderQueueNew.get(0));
            // Give order a reference to picker
            orderQueueNew.get(0).setPicker(availablePickers.get(0));
            // Add to orders being processed
            ordersProcessing.add(orderQueueNew.get(0));
            // Divide order into tasks to robots and add to list of available tasks
            tasksAvailable.addAll(createTasksFromOrder(orderQueueNew.get(0)));
            System.out.println("Commenced order: " + orderQueueNew.get(0));
            // Remove order from queue
            orderQueueNew.remove(0);
        }
        if(server.hasAvailableRobot()) {
            for(Task task : tasksAvailable){
                Optional<Robot> optimalRobot = taskAllocator.findOptimalRobot(server.getAllRobots(), task);
                if(optimalRobot.isPresent()){
                    if(optimalRobot.get().getRobotController().assignTask(task))
                        tasksAvailable.remove(task);
                }
            }
        }
    }

    private ArrayList<Task> createTasksFromOrder(Order order) {
        ArrayList<Task> orderTasks = new ArrayList<>();

        for(OrderLine line : order.getLinesInOrder()){
            orderTasks.addAll(splitIntoTasks(line, order));
        }

        return orderTasks;
    }

    private Collection<Task> splitIntoTasks(OrderLine line, Order order) {
        Product product = line.getProduct();

        StorageGrid storageGrid = server.getSimulation().getStorageGrid();
        ArrayList<BinTile> binTiles = storageGrid.tilesWithProduct(line.getProduct());
        binTiles.sort(Comparator.comparingInt(tile -> tile.getBin().productCount(product)));

        ArrayList<Task> productTasks = new ArrayList<>();
        int remainingProducts = line.getAmount();
        for(BinTile tile : binTiles){
            remainingProducts -= tile.getBin().productCount(product);
            productTasks.add(new BinDelivery(order, tile.getGridCoordinate()));
            if(remainingProducts <= 0) break;
        }

        return productTasks;
    }

    public int ordersInQueue(){
        return orderQueueNew.size();
    }

}
