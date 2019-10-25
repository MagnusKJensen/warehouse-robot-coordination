package dk.aau.d507e19.warehousesim;

public class WarehouseSpecs {
    public static final int wareHouseWidth = 30;
    public static final int wareHouseHeight = 30;
    public static final float binSizeInMeters = 1;
    public static final int productsPerBin = 15;
    public static final int SKUsPerBin = 9;
    public static final int SKUs = 80;
    public static final int productsInStock = 5000;
    public static final int numberOfRobots = 12;
    public static final float robotTopSpeed = 3f; // Meters/second
    public static final float robotAcceleration = 0.8f; // m/s^2
    public static final float robotDeceleration = 2f; // m/s^2
    public static final float robotMinimumSpeed = 0.1f;
    public static final int robotPickUpSpeedInSeconds = 3;
    // For example 20% of the SKUs = 80% of turnover and 80% of SKUs = 20% turnover would be {{20,80},{80,20}.
    // Both turnover and SKUs should also sum up to 100. Do not enter more than 1 decimal.
    // Do not enter lower distribution, than 100 / SKUs.
    public static final double[][] skuDistribution = {{20.5, 50}, {9.5,30}, {70,20}};
    public static final boolean isRandomProductDistribution = true;
    public static final int[][] pickerPoints = {{0,0}, {2,0}, {4,0}, {6,0}, {8,0}, {10,0}, {12,0}, {14,0}, {16,0}, {18,0}, {20,0}, {22,0}, {24,0}};
    public static final int secondsBetweenOrders = 1;
    public static final int orderGoal = 50;
    public static final boolean collisionDetectedEnabled = false; // TEMP - Should not be enabled until reservationManager done.
    // todo layers? - Philip
}
