package dk.aau.d507e19.warehousesim.controller.server.order;

import dk.aau.d507e19.warehousesim.SimulationApp;
import dk.aau.d507e19.warehousesim.WarehouseSpecs;
import dk.aau.d507e19.warehousesim.controller.server.OrderManager;
import dk.aau.d507e19.warehousesim.controller.server.Server;
import dk.aau.d507e19.warehousesim.storagegrid.product.Product;
import dk.aau.d507e19.warehousesim.storagegrid.product.SKU;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;

public class OrderGenerator {
    private static final long RANDOM_SEED = SimulationApp.RANDOM_SEED;
    private Random random = new Random(RANDOM_SEED);
    private final int TICKS_BETWEEN_ORDERS = WarehouseSpecs.secondsBetweenOrders * SimulationApp.TICKS_PER_SECOND;

    private OrderManager orderManager;
    private int tickSinceLastOrder = TICKS_BETWEEN_ORDERS;
    private Server server;

    private final int MAX_PRODUCTS = WarehouseSpecs.productsPerOrder;

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
        else tickSinceLastOrder += 1;

    }

    private Order generateRandomOrder(){
        Order order = new Order();

        ArrayList<Product> allProducts = server.getProductsAvailable();

        // Can't be 0
        int amountInOrder = random.nextInt(MAX_PRODUCTS) + 1;

        // for the products in the order
        ArrayList<Product> productsInOrder = new ArrayList<>();

        // Take out all products from the list of available products from server
        while(productsInOrder.size() < amountInOrder && !allProducts.isEmpty()){
            int nextProduct = random.nextInt(allProducts.size());
            productsInOrder.add(allProducts.get(nextProduct));
            allProducts.remove(nextProduct);
        }

        // Add all the products to the order
        for(Product prod : productsInOrder){
            order.addProducts(prod);
        }

        return order;
    }

}
