package dk.aau.d507e19.warehousesim;

import dk.aau.d507e19.warehousesim.storagegrid.GridBounds;

public class WarehouseSpecs {


    /** Warehouse **/
    public static final int wareHouseWidth = 30;
    public static final int wareHouseHeight = 30;
    public static final GridBounds WAREHOUSE_BOUNDS = new GridBounds(wareHouseWidth - 1, wareHouseHeight - 1);
    public static final float binSizeInMeters = 1;
    public static final int productsPerBin = 15;
    public static final int SKUsPerBin = 9;
    public static final int SKUs = 20;
    public static final int productsInStock = 1000;
    // For example 20% of the SKUs = 80% of turnover and 80% of SKUs = 20% turnover would be {{20,80},{80,20}.
    // Both turnover and SKUs should also sum up to 100. Do not enter more than 1 decimal.
    // Do not enter lower distribution, than 100 / SKUs.
    public static final double[][] skuDistribution = {{20.5, 50}, {9.5,30}, {70,20}};
    public static final boolean isRandomProductDistribution = true;

    /** Robot **/
    public static final int numberOfRobots = 200;
    public static final float robotTopSpeed = 3f; // Meters/second
    public static final float robotAcceleration = 0.8f; // m/s^2
    public static final float robotDeceleration = 2f; // m/s^2
    public static final float robotMinimumSpeed = 0.1f;
    public static final int robotPickUpSpeedInSeconds = 3;
    public static final boolean collisionDetectedEnabled = true; // TEMP - Should not be enabled until reservationManager done.
    public static final Pattern robotPlacementPattern = new Pattern(Pattern.PatternType.STACKED_LINES, 1, WAREHOUSE_BOUNDS);

    /** Picker **/
    public static final Pattern pickerPlacementPattern = new Pattern(Pattern.PatternType.SPIRAL, 1, WAREHOUSE_BOUNDS);
    public static final int numberOfPickers = 60;
    public static final int[][] pickerPoints = {{0,0}, {2,0}, {4,0}, {6,0}, {8,0}, {10,0}, {12,0}, {14,0}, {16,0}, {18,0}, {20,0}, {22,0}, {24,0},{26,0}, {28,0}};

    /** Orders **/
    public static final int secondsBetweenOrders = 1;
    public static final int orderGoal = 50;
    public static final int productsPerOrder = 4;
}
