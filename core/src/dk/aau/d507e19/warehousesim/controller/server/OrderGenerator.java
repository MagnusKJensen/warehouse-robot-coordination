package dk.aau.d507e19.warehousesim.controller.server;

import dk.aau.d507e19.warehousesim.SimulationApp;
import dk.aau.d507e19.warehousesim.WarehouseSpecs;
import dk.aau.d507e19.warehousesim.controller.robot.Order;
import dk.aau.d507e19.warehousesim.storagegrid.product.Product;

import java.util.Random;

public class OrderGenerator {
    private static final long RANDOM_SEED = 123456789L;
    private Random random = new Random(RANDOM_SEED);
    private final int TICKS_BETWEEN_ORDERS = WarehouseSpecs.secondsBetweenOrders * SimulationApp.TICKS_PER_SECOND;

    private OrderManager orderManager;
    private int tickSinceLastOrder = TICKS_BETWEEN_ORDERS;
    private Server server;

    public OrderGenerator(OrderManager orderManager, Server server) {
        this.orderManager = orderManager;
        this.server = server;
    }

    public void update(){
        if(tickSinceLastOrder == TICKS_BETWEEN_ORDERS){
            Order randomOrder = generateRandomOrder();
            orderManager.takeOrder(randomOrder);
            tickSinceLastOrder = 0;
        }
        tickSinceLastOrder += 1;
    }

    private Order generateRandomOrder() {
        int bound = server.getProductsAvailable().size();
        Product prod = server.getProductsAvailable().get(random.nextInt(bound));
        int amount = random.nextInt(3 - 1) + 1;
        Order order = new Order(prod, amount);
        return order;
    }
}
