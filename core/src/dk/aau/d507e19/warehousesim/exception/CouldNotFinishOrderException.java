package dk.aau.d507e19.warehousesim.exception;

import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.storagegrid.product.Product;

import java.util.ArrayList;

public class CouldNotFinishOrderException extends RuntimeException {

    public CouldNotFinishOrderException(ArrayList<Product> orderProducts, ArrayList<Product> holdingProducts) {
        System.err.println("Picker does not have all products for order." +
                " Holding products: " + holdingProducts +
                ", order contains: " + orderProducts);
    }
}
