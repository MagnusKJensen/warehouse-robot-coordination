package dk.aau.d507e19.warehousesim.controller.robot;

import dk.aau.d507e19.warehousesim.storagegrid.product.Product;

public class Order {
    private GridCoordinate destination; // TODO: 15/10/2019 Remove. This should be found by taskmanager and pathfinder
    private Product product;
    private int amount;

    public Order(GridCoordinate destination, Product product, int amount) {
        this.destination = destination;
        this.product = product;
        this.amount = amount;
    }

    public GridCoordinate getDestination() {
        return destination;
    }

}
