package dk.aau.d507e19.warehousesim.controller.server;

import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.robot.plan.task.BinDelivery;
import dk.aau.d507e19.warehousesim.controller.robot.plan.task.Task;
import dk.aau.d507e19.warehousesim.controller.server.order.Order;
import dk.aau.d507e19.warehousesim.controller.server.order.OrderLine;
import dk.aau.d507e19.warehousesim.controller.server.taskAllocator.TaskAllocator;
import dk.aau.d507e19.warehousesim.storagegrid.BinTile;
import dk.aau.d507e19.warehousesim.storagegrid.PickerTile;
import dk.aau.d507e19.warehousesim.storagegrid.StorageGrid;
import dk.aau.d507e19.warehousesim.storagegrid.Tile;
import dk.aau.d507e19.warehousesim.storagegrid.product.Product;

import java.util.*;

public class OrderManager {
    private ArrayList<Order> orderQueue = new ArrayList<>();
    private ArrayList<Order> ordersFinished = new ArrayList<>();
    private Server server;
    private TaskAllocator taskAllocator;
    private ArrayList<Task> tasksQueue = new ArrayList<>();
    private HashMap<Order, ArrayList<BinDelivery>> processingOrdersToTaskMap = new HashMap<>();

    OrderManager(Server server) {
        this.server = server;
        this.taskAllocator = server.getSimulation().getSimulationApp().getTaskAllocatorSelected().getTaskAllocator(server.getSimulation().getStorageGrid());
    }

    public void takeOrder(Order order){
        this.orderQueue.add(order);
    }

    private void checkIfOrdersAreCompleted() {
        boolean isCompleted;
        ArrayList<Order> ordersToRemove = new ArrayList<>();
        for(Order order : processingOrdersToTaskMap.keySet()){
            isCompleted = true;
            if(!processingOrdersToTaskMap.get(order).isEmpty()){
                for(Task task : processingOrdersToTaskMap.get(order)){
                    if(!task.isCompleted()) isCompleted = false;
                }

                if(isCompleted){
                    order.getPicker().setAvailable();
                    ordersFinished.add(order);
                    ordersToRemove.add(order);
                }
            }
        }

        for(Order order : ordersToRemove){
            processingOrdersToTaskMap.remove(order);
        }
    }


    public void update(){
        checkIfOrdersAreCompleted();

        ArrayList<PickerTile> availablePickers = server.getAvailablePickers();

        int maxOrderAssignPerTick = 50;
        Iterator<Order> orderIterator = orderQueue.iterator();

        while(orderIterator.hasNext() && !availablePickers.isEmpty() && maxOrderAssignPerTick != 0){
            Order order = orderIterator.next();
            // Assign order to picker
            availablePickers.get(0).assignOrder(order);
            // Give order a reference to picker
            order.setPicker(availablePickers.get(0));
            // Divide order into tasks to robots and add to list of available tasks
            ArrayList<BinDelivery> tasksFromOrder = createTasksFromOrder(order);
            if(tasksFromOrder != null){
                processingOrdersToTaskMap.put(order, tasksFromOrder);
                tasksQueue.addAll(tasksFromOrder);
                // Remove order from queue
                orderIterator.remove();
                // Picker is no longer available
                availablePickers.remove(0);
            }
            // If order could not be divided into tasks, it is not assigned to a picker
            // And not put into the processing ArrayList
            else {
                availablePickers.get(0).setAvailable();
                order.removePicker();
            }
            maxOrderAssignPerTick--;
        }

        if(server.hasAvailableRobot()) {
            Iterator<Task> taskIterator = tasksQueue.iterator();
            while(taskIterator.hasNext()){
                Task task = taskIterator.next();
                Optional<Robot> optimalRobot = taskAllocator.findOptimalRobot(server.getAllRobots(), task);
                if(optimalRobot.isPresent()){
                    if(optimalRobot.get().getRobotController().assignTask(task)){
                        task.setRobot(optimalRobot.get());
                        taskIterator.remove();
                    }
                }
            }
        }
    }

    private ArrayList<BinDelivery> divideOrderIntoDeliveries(Order order) {
        ArrayList<BinDelivery> deliveries = new ArrayList<>();
        ArrayList<Product> productsToPick = order.getAllProductsInOrder();

        StorageGrid storageGrid = server.getSimulation().getStorageGrid();

        for(int x = 0; x < storageGrid.width; x++){
            for(int y = 0; y < storageGrid.height; y++){
                Tile tile = storageGrid.getTile(x,y);
                if(!(tile instanceof BinTile)) continue;
                if(server.getReservationManager().isBinReserved(tile.getGridCoordinate())) continue;
                BinDelivery delivery = generateDelivery((BinTile) tile, productsToPick, order);
                if(delivery != null) deliveries.add(delivery);
            }
        }

        if(productsToPick.isEmpty()) return deliveries;

        return null;
    }

    private BinDelivery generateDelivery(BinTile tile, ArrayList<Product> productsToPick, Order order) {
        ArrayList<Product> pickProductFromBin = new ArrayList<>();
        for(Product product : tile.getBin().getProducts()){
            if(productsToPick.contains(product)){
                pickProductFromBin.add(product);
                productsToPick.remove(product);
            }
        }

        if(pickProductFromBin.isEmpty()) return null;

        return new BinDelivery(order, tile.getGridCoordinate(), pickProductFromBin);
    }

    private ArrayList<BinDelivery> createTasksFromOrder(Order order) {
        ArrayList<BinDelivery> deliveries = divideOrderIntoDeliveries(order);
        if(deliveries != null){
            for(BinDelivery delivery : deliveries){
                server.getReservationManager().reserveBinTile(delivery.getBinCoords());
            }

            return deliveries;
        }
        return null;
    }


    /*
    private ArrayList<Task> splitLineIntoTasks(OrderLine line, Order order, ArrayList<BinTile> tempReservations) {
        ArrayList<BinTile> newReservations = new ArrayList<>();
        Product product = line.getProduct();

        StorageGrid storageGrid = server.getSimulation().getStorageGrid();
        ArrayList<BinTile> binTiles = storageGrid.tilesWithProduct(line.getProduct());
        binTiles.sort(Comparator.comparingInt(tile -> tile.getBin().productCount(product)));

        ArrayList<Task> productTasks = new ArrayList<>();

        int remainingProducts = line.getAmount();
        int productInBin;
        int productsToPick;
        for(BinTile tile : binTiles){
            if(server.getReservationManager().isBinReserved(tile.getGridCoordinate())){
                continue;
            }
            if(tempReservations.contains(tile)){
                continue;
            }

            productInBin = tile.getBin().productCount(product);
            // Only pick up all products from the bin, if they are needed
            if(remainingProducts > productInBin){
                remainingProducts -= productInBin;
                productsToPick = productInBin;
            } else {
                productsToPick = remainingProducts;
                remainingProducts = 0;
            }

            productTasks.add(new BinDelivery(order, tile.getGridCoordinate(), product, productsToPick));
            newReservations.add(tile);
            if(remainingProducts <= 0){
                break;
            }
        }

        if(remainingProducts > 0){
            return null;
        }

        tempReservations.addAll(newReservations);

        return productTasks;
    }
    */
    public int ordersInQueue(){
        return orderQueue.size();
    }

    public int ordersFinished(){
        return ordersFinished.size();
    }

    public int tasksInQueue(){
        int tasksNotComplete = 0;
        for(ArrayList<BinDelivery> taskArray : processingOrdersToTaskMap.values()){
            for(Task task : taskArray){
                if(!task.isCompleted()) tasksNotComplete++;
            }
        }
        return tasksNotComplete;
    }
}
