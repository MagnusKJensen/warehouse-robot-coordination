package dk.aau.d507e19.warehousesim.controller.pathAlgorithms;

public class Reservation {
    int robotID;
    float timeTileIsReserved;

    public Reservation(int robotID, float timeTileIsReserved) {
        this.robotID = robotID;
        this.timeTileIsReserved = timeTileIsReserved;
    }

    public int getRobotID() {
        return robotID;
    }

    public float getTimeTileIsReserved() {
        return timeTileIsReserved;
    }
}
