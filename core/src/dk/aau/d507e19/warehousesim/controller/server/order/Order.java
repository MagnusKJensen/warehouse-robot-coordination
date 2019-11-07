package dk.aau.d507e19.warehousesim.controller.server.order;

import dk.aau.d507e19.warehousesim.storagegrid.PickerTile;
import dk.aau.d507e19.warehousesim.storagegrid.product.Product;

import java.util.ArrayList;

public class Order {
    private String orderID;
    private ArrayList<OrderLine> linesInOrder = new ArrayList<>();
    private PickerTile picker;
    private long startTimeInMS;
    private long finishTimeInMS;

    public Order(ArrayList<OrderLine> linesInOrder) {
        this.linesInOrder = linesInOrder;
    }

    public Order(long nextOrderID) {
        orderID = String.valueOf(nextOrderID);
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
                "orderID='" + orderID + '\'' +
                ", linesInOrder=" + linesInOrder +
                ", picker=" + picker +
                ", startTimeInMS=" + startTimeInMS +
                ", finishTimeInMS=" + finishTimeInMS +
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

    public long getStartTimeInMS() {
        return startTimeInMS;
    }

    public void setStartTimeInMS(long startTimeInMS) {
        this.startTimeInMS = startTimeInMS;
    }

    public long getFinishTimeInMS() {
        return finishTimeInMS;
    }

    public void setFinishTimeInMS(long finishTimeInMS) {
        this.finishTimeInMS = finishTimeInMS;
    }

    public String getStatsAsCSV(){
        StringBuilder builder = new StringBuilder();

        long timeSpentOnOrder = finishTimeInMS - startTimeInMS;

        builder.append(orderID).append(',');
        builder.append(startTimeInMS).append(',');
        builder.append(finishTimeInMS).append(',');
        builder.append(timeSpentOnOrder);

        return builder.toString();
    }
}
