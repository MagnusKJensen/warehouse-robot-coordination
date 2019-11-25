package dk.aau.d507e19.warehousesim.controller.robot.plan.task;

import dk.aau.d507e19.warehousesim.Simulation;
import dk.aau.d507e19.warehousesim.TimeUtils;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.server.order.Order;
import dk.aau.d507e19.warehousesim.storagegrid.product.Bin;
import dk.aau.d507e19.warehousesim.storagegrid.product.Product;

import java.util.ArrayList;

public class RecollectBinDelivery extends BinDelivery {

    private GridCoordinate binOnTopOfGridCoords;
    private Bin bin;

    public RecollectBinDelivery(BinDelivery binDelivery, GridCoordinate binOnTopOfGridCoords, Bin bin) {
        super(binDelivery.order, binDelivery.binCoords, binDelivery.productsToPick);
        this.binOnTopOfGridCoords = binOnTopOfGridCoords;
        this.bin = bin;
    }

    @Override
    protected void planTasks() {
        // Pickup bin from atop the grid
        subTasks.add(new BinOnTopOfGridPickup(robotController, binOnTopOfGridCoords, bin));

        // Delivery
        subTasks.add(Navigation.getInstance(robotController, order.getPicker().getGridCoordinate()));
        subTasks.add(new TimedAction(() -> robot.deliverBinToPicker(order.getPicker().getGridCoordinate(), productsToPick), TimeUtils.secondsToTicks(Simulation.getWarehouseSpecs().robotDeliverToPickInSeconds)));

        // Bin return
        subTasks.add(Navigation.getInstance(robotController, binCoords));
        subTasks.add(new TimedAction(() -> robot.putDownBin(), TimeUtils.secondsToTicks(Simulation.getWarehouseSpecs().robotPickUpSpeedInSeconds)));

        isPlanned = true;
    }
}
