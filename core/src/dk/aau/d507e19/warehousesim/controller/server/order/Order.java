package dk.aau.d507e19.warehousesim.controller.server.order;

import dk.aau.d507e19.warehousesim.storagegrid.PickerTile;
import dk.aau.d507e19.warehousesim.storagegrid.product.Product;

import java.util.ArrayList;

public class Order {
    private ArrayList<OrderLine> linesInOrder = new ArrayList<>();
    private PickerTile picker;
    private long startTimeInSeconds;
    private long finishTimeInSeconds;

    public Order(ArrayList<OrderLine> linesInOrder) {
        this.linesInOrder = linesInOrder;
    }

    public Order() {
    }

    public ArrayList<OrderLine> getLinesInOrder() {
        return linesInOrder;
    }

    public PickerTile getPicker() {
        return picker;
    }

    public ArrayList<Product> getAllProductsInOrder(){
        ArrayList<Product> productsInOrder = new ArrayList<>();
        for(OrderLine line : getLinesInOrder()){
            for(int i = 0; i < line.getAmount(); i++){
                productsInOrder.add(line.getProduct());
            }
        }

        return productsInOrder;
    }

    public void setPicker(PickerTile picker) {
        this.picker = picker;
    }

    @Override
    public String toString() {
        return "Order{" +
                "linesInOrder=" + linesInOrder +
                ", picker=" + picker +
                ", startTimeInSeconds=" + startTimeInSeconds +
                ", finishTimeInSeconds=" + finishTimeInSeconds +
                '}';
    }

    public void removePicker(){
        this.picker = null;
    }

    public void addProducts(Product prod){
        boolean wasContained = false;
        for(OrderLine line : linesInOrder){
            if(line.getProduct().equals(prod)){
                line.setAmount(line.getAmount() + 1);
                wasContained = true;
            }
        }
        if(!wasContained) linesInOrder.add(new OrderLine(prod, 1));
    }

    public long getStartTimeInSeconds() {
        return startTimeInSeconds;
    }

    public void setStartTimeInMS(long startTimeInSeconds) {
        this.startTimeInSeconds = startTimeInSeconds;
    }

    public long getFinishTimeInSeconds() {
        return finishTimeInSeconds;
    }

    public void setFinishTimeInSeconds(long finishTimeInSeconds) {
        this.finishTimeInSeconds = finishTimeInSeconds;
    }
}
