package dk.aau.d507e19.warehousesim.controller.server.order;

import dk.aau.d507e19.warehousesim.Simulation;
import dk.aau.d507e19.warehousesim.SimulationApp;
import dk.aau.d507e19.warehousesim.WarehouseSpecs;
import dk.aau.d507e19.warehousesim.controller.server.OrderManager;
import dk.aau.d507e19.warehousesim.controller.server.Server;
import dk.aau.d507e19.warehousesim.storagegrid.product.Product;
import org.graalvm.compiler.core.common.type.ArithmeticOpTable;

import java.util.ArrayList;
import java.util.Random;

public class OrderGenerator {
    private Random random = new Random(SimulationApp.RANDOM_SEED);

    private final int TICKS_BETWEEN_ORDERS = WarehouseSpecs.secondsBetweenOrders * SimulationApp.TICKS_PER_SECOND;

    private OrderManager orderManager;
    private int tickSinceLastOrder = TICKS_BETWEEN_ORDERS;
    private Server server;

    private final int MAX_LINES = 3;
    private final int MAX_AMOUNT = 4;

    public OrderGenerator(OrderManager orderManager, Server server) {
        this.orderManager = orderManager;
        this.server = server;
    }

    public void update(){
        if(tickSinceLastOrder == TICKS_BETWEEN_ORDERS && !server.getProductsAvailable().isEmpty()){
            Order randomOrder = generateRandomOrder();
            orderManager.takeOrder(randomOrder);
            tickSinceLastOrder = 0;
        }
        tickSinceLastOrder += 1;

    }

    private Order generateRandomOrder() {
        int linesInOrder;

        // edge case for last orders
        if(server.getProductsAvailable().size() < MAX_LINES * MAX_AMOUNT) {
            int SKUSleft = server.getProductSKUsRemaining();
            if(SKUSleft > MAX_LINES) linesInOrder = MAX_LINES;
            else linesInOrder = SKUSleft;
        }
        // Normal case
        else linesInOrder = random.nextInt(MAX_LINES - 1) + 1;

        ArrayList<OrderLine> orderLines = new ArrayList<>();
        for(int i = 0; i < linesInOrder; i++){
            orderLines.add(generateRandomLine());
        }

        Order order = new Order(orderLines);

        return order;
    }

    private OrderLine generateRandomLine(){
        int bound = server.getProductsAvailable().size();
        if(bound == 0)
            throw new RuntimeException("Cannot generate order line when no more products are available");

        Product prod;
        int amount;
        OrderLine line;
        // Edge case for last orders
        if(server.getProductsAvailable().size() < MAX_LINES * MAX_AMOUNT){
            if(bound == 1) prod = server.getProductsAvailable().get(0);
            else prod = server.getProductsAvailable().get(random.nextInt(bound - 1));

            amount = server.getAmountLeftOfProduct(prod);
            line = new OrderLine(prod,amount);
        } else { // Normal case
            prod = server.getProductsAvailable().get(random.nextInt(bound - 1));
            amount = random.nextInt(MAX_AMOUNT - 1) + 1;
            amount = Math.min(amount, server.getAmountLeftOfProduct(prod));
            line = new OrderLine(prod,amount);
        }

        return line;
    }

}
