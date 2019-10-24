package dk.aau.d507e19.warehousesim.controller.server.order;

import com.sun.org.apache.xpath.internal.operations.Or;
import dk.aau.d507e19.warehousesim.SimulationApp;
import dk.aau.d507e19.warehousesim.WarehouseSpecs;
import dk.aau.d507e19.warehousesim.controller.robot.Order;
import dk.aau.d507e19.warehousesim.controller.server.OrderManager;
import dk.aau.d507e19.warehousesim.controller.server.Server;
import dk.aau.d507e19.warehousesim.storagegrid.product.Product;

import java.util.ArrayList;
import java.util.Random;

public class OrderGeneratorNew {
    private static final long RANDOM_SEED = 123456789L;
    private Random random = new Random(RANDOM_SEED);
    private final int TICKS_BETWEEN_ORDERS = WarehouseSpecs.secondsBetweenOrders * SimulationApp.TICKS_PER_SECOND;

    private OrderManager orderManager;
    private int tickSinceLastOrder = TICKS_BETWEEN_ORDERS;
    private Server server;

    private final int MAX_LINES = 3;
    private final int MAX_AMOUNT = 4;

    public OrderGeneratorNew(OrderManager orderManager, Server server) {
        this.orderManager = orderManager;
        this.server = server;
    }

    public void update(){
        if(tickSinceLastOrder == TICKS_BETWEEN_ORDERS){
            OrderNew randomOrder = generateRandomOrder();
            orderManager.takeOrder(randomOrder);
            tickSinceLastOrder = 0;
        }
        tickSinceLastOrder += 1;
    }

    private OrderNew generateRandomOrder() {
        int linesInOrder = random.nextInt(MAX_LINES - 1) + 1;

        ArrayList<OrderLine> orderLines = new ArrayList<>();
        for(int i = 0; i < linesInOrder; i++){
            orderLines.add(generateRandomLine());
        }

        return new OrderNew(orderLines);
    }

    private OrderLine generateRandomLine(){
        int bound = server.getProductsAvailable().size();
        Product prod = server.getProductsAvailable().get(random.nextInt(bound));
        int amount = random.nextInt(MAX_AMOUNT - 1) + 1;
        return new OrderLine(prod, amount);
    }

}
