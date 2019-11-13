package dk.aau.d507e19.warehousesim;

import dk.aau.d507e19.warehousesim.storagegrid.GridBounds;

import java.util.Arrays;

public class WarehouseSpecs {
    /** * * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                                    *
     *               THIS FILE IS READ ONLY               *
     *  TO CHANGE RUN CONFIG CHANGE JSON FILE IN ASSETS   *
     *                                                    *
     * * * * * * * * * * * * * * * * * * * * * * * * * * **/





    /** Warehouse **/
    public int wareHouseWidth = 30;
    public int wareHouseHeight = 30;
    public GridBounds WAREHOUSE_BOUNDS = new GridBounds(wareHouseWidth - 1, wareHouseHeight - 1);
    public float binSizeInMeters = 1;
    public int productsPerBin = 15;
    public int SKUsPerBin = 9;
    public int SKUs = 20;
    public int productsInStock = 1000;
    // For example 20% of the SKUs = 80% of turnover and 80% of SKUs = 20% turnover would be {{20,80},{80,20}.
    // Both turnover and SKUs should also sum up to 100. Do not enter more than 1 decimal.
    // Do not enter lower distribution, than 100 / SKUs.
    public double[][] skuDistribution = {{20.5, 50}, {9.5,30}, {70,20}};
    public boolean isRandomProductDistribution = true;

    /** Robot **/
    public int numberOfRobots = 20;
    public float robotTopSpeed = 3f; // Meters/second
    public float robotAcceleration = 0.8f; // m/s^2
    public float robotDeceleration = 2f; // m/s^2
    public float robotMinimumSpeed = 0.1f;
    public int robotPickUpSpeedInSeconds = 3;
    public int robotDeliverToPickInSeconds = 12;
    public boolean collisionDetectedEnabled = true; // TEMP - Should not be enabled until reservationManager done.
    public Pattern robotPlacementPattern = new Pattern(Pattern.PatternType.STACKED_LINES, 1, WAREHOUSE_BOUNDS);

    /** Charger **/
    public Pattern chargerPlacementPattern = new Pattern(Pattern.PatternType.SPIRAL, 1, WAREHOUSE_BOUNDS);
    public int numberOfChargers = 1;
    public int[][] chargerPoints = {{0,1}};

    /** Maintenance **/
    public Pattern maintenancePlacementPattern = new Pattern(Pattern.PatternType.SPIRAL, 1, WAREHOUSE_BOUNDS);
    public int numberOfMaintenance = 1;
    public int[][] maintenancePoints = {{0,3}};

    /** Picker **/
    public Pattern pickerPlacementPattern = new Pattern(Pattern.PatternType.SPIRAL, 1, WAREHOUSE_BOUNDS);
    public int numberOfPickers = 60;
    public int[][] pickerPoints = {{0,0}, {2,0}, {4,0}, {6,0}, {8,0}, {10,0}, {12,0}, {14,0}, {16,0}, {18,0}, {20,0}, {22,0}, {24,0},{26,0}, {28,0}};

    /** Orders **/
    public int secondsBetweenOrders = 1;
    public int orderGoal = 50;
    public int productsPerOrder = 4;

    @Override
    public String toString() {
        return "WarehouseSpecs{" +
                "wareHouseWidth=" + wareHouseWidth +
                ", wareHouseHeight=" + wareHouseHeight +
                ", WAREHOUSE_BOUNDS=" + WAREHOUSE_BOUNDS +
                ", binSizeInMeters=" + binSizeInMeters +
                ", productsPerBin=" + productsPerBin +
                ", SKUsPerBin=" + SKUsPerBin +
                ", SKUs=" + SKUs +
                ", productsInStock=" + productsInStock +
                ", skuDistribution=" + Arrays.toString(skuDistribution) +
                ", isRandomProductDistribution=" + isRandomProductDistribution +
                ", numberOfRobots=" + numberOfRobots +
                ", robotTopSpeed=" + robotTopSpeed +
                ", robotAcceleration=" + robotAcceleration +
                ", robotDeceleration=" + robotDeceleration +
                ", robotMinimumSpeed=" + robotMinimumSpeed +
                ", robotPickUpSpeedInSeconds=" + robotPickUpSpeedInSeconds +
                ", collisionDetectedEnabled=" + collisionDetectedEnabled +
                ", robotPlacementPattern=" + robotPlacementPattern +
                ", pickerPlacementPattern=" + pickerPlacementPattern +
                ", numberOfPickers=" + numberOfPickers +
                ", pickerPoints=" + Arrays.toString(pickerPoints) +
                ", secondsBetweenOrders=" + secondsBetweenOrders +
                ", orderGoal=" + orderGoal +
                ", productsPerOrder=" + productsPerOrder +
                '}';
    }
}
