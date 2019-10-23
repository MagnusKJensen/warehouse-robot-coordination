package dk.aau.d507e19.warehousesim.controller.server;

import dk.aau.d507e19.warehousesim.SimulationApp;
import dk.aau.d507e19.warehousesim.controller.robot.Order;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.server.taskAllocator.DummyTaskAllocator;
import dk.aau.d507e19.warehousesim.controller.server.taskAllocator.NaiveShortestDistanceTaskAllocator;
import dk.aau.d507e19.warehousesim.controller.server.taskAllocator.ShortestDistanceTaskAllocator;
import dk.aau.d507e19.warehousesim.controller.server.taskAllocator.TaskAllocator;
import dk.aau.d507e19.warehousesim.storagegrid.BinTile;

import java.util.ArrayList;
import java.util.Optional;

public class OrderManager {
    private ArrayList<Order> ordersFinished = new ArrayList<>();
    private ArrayList<Order> orderQueue = new ArrayList<>();
    private Server server;
    private TaskAllocator taskAllocator;

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

    public void update(){
        if(server.hasRobotsAvailable()){
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

    public int ordersInQueue(){
        return orderQueue.size();
    }
}
