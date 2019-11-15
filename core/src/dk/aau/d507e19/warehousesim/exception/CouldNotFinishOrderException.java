package dk.aau.d507e19.warehousesim.exception;

import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.server.order.Order;
import dk.aau.d507e19.warehousesim.storagegrid.product.Product;

import java.util.ArrayList;

public class CouldNotFinishOrderException extends RuntimeException {

    public CouldNotFinishOrderException(Order order, ArrayList<Product> orderProducts, ArrayList<Product> holdingProducts) {
        System.err.println("Picker does not have all products for order: " + order.toString() +
                "\nHolding products: " + holdingProducts +
                "\nOrder contains: " + orderProducts);
    }
}
