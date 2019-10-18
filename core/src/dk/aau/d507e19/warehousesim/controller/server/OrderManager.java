package dk.aau.d507e19.warehousesim.controller.server;

import dk.aau.d507e19.warehousesim.controller.robot.Order;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.robot.Status;
import dk.aau.d507e19.warehousesim.controller.server.taskAllocator.DummyTaskAllocator;
import dk.aau.d507e19.warehousesim.controller.server.taskAllocator.TaskAllocator;
import dk.aau.d507e19.warehousesim.storagegrid.BinTile;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;

public class OrderManager {
    private ArrayList<Order> ordersFinished = new ArrayList<>();
    private ArrayList<Order> orders = new ArrayList<>();
    private Server server;
    private TaskAllocator taskAllocator = new DummyTaskAllocator();

    public OrderManager(Server server) {
        this.server = server;
    }

    public boolean takeOrder(Order order){
        if(!orderIsServiceable(order)) return false;
        else {
            this.orders.add(order);
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
        // If some order is still not being processed.
        if(orders.size() > 0){
            Order order = orders.get(0);
            Optional<Robot> optimalRobot = taskAllocator.findOptimalRobot(server.getAllRobots(), order);
            if(optimalRobot.isPresent()){
                optimalRobot.get().assignOrder(order);
                ordersFinished.add(orders.get(0));
                System.out.println("Commenced order: " + orders.get(0));
                orders.remove(0);
            }
        }
    }
}
