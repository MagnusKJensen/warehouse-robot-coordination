package dk.aau.d507e19.warehousesim;

import com.google.gson.Gson;

public class WarehouseSpecs {
    public static final int wareHouseWidth = 30;
    public static final int wareHouseHeight = 30;
    public static final int wareHousePickUpPoints = 0;
    public static final float binSizeInMeters = 1;
    public static final int SKUs = 0;
    public static final int productsInStock = 0;
    public static final int numberOfRobots = 5;
    public static final int robotTopSpeed = 3; // Meters/second
    public static final float robotAcceleration = 0.8f; // m/s^2
    public static final float robotDeceleration = 2f; // m/s^2
    public static final float robotMinimumSpeed = 0.1f;
    public static final int robotPickUpSpeedInSeconds = 1;
    public static final int robotDeliverToPickerInSeconds = 0;
    // todo turnover distribution - Philip
    // todo layers? - Philip

}
