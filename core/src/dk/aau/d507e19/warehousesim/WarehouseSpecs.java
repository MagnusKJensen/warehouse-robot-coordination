package dk.aau.d507e19.warehousesim;


public class WarehouseSpecs {
    public static final int wareHouseWidth = 30;
    public static final int wareHouseHeight = 30;
    public static final int wareHousePickUpPoints = 0;
    public static final float binSizeInMeters = 1;
    public static final int productsPerBin = 15;
    public static final int SKUsPerBin = 9;
    public static final int SKUs = 99;
    public static final int productsInStock = 1214;
    public static final int numberOfRobots = 5;
    public static final float robotTopSpeed = 3f; // Meters/second
    public static final float robotAcceleration = 0.8f; // m/s^2
    public static final float robotDeceleration = 2f; // m/s^2
    public static final float robotMinimumSpeed = 0.1f;
    public static final int robotPickUpSpeedInSeconds = 1;
    public static final int robotDeliverToPickerInSeconds = 1;
    // For example 20% of the SKUs = 80% of turnover and 80% of SKUs = 20% turnover
    public static final float[][] skuDistribution = {{20, 80}, {80,20}};
    // todo turnover distribution - Philip
    // todo layers? - Philip
}
