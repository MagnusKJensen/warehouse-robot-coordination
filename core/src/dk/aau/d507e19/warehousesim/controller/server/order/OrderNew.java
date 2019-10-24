package dk.aau.d507e19.warehousesim.controller.server.order;

import dk.aau.d507e19.warehousesim.storagegrid.PickerTile;

import java.util.ArrayList;

public class OrderNew {
    private ArrayList<OrderLine> linesInOrder;
    private PickerTile picker;

    public OrderNew(ArrayList<OrderLine> linesInOrder) {
        this.linesInOrder = linesInOrder;
    }

    public ArrayList<OrderLine> getLinesInOrder() {
        return linesInOrder;
    }

    public PickerTile getPicker() {
        return picker;
    }
}
