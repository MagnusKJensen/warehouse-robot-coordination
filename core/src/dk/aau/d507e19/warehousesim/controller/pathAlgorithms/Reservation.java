package dk.aau.d507e19.warehousesim.controller.pathAlgorithms;

public class Reservation {
    int robotID;
    float timeTileIsReserved;
    int xCordinate;
    int yCordinate;

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

    public int getxCordinate() {
        return xCordinate;
    }

    public int getyCordinate() {
        return yCordinate;
    }

    public void setxCordinate(int xCordinate) {
        this.xCordinate = xCordinate;
    }

    public void setyCordinate(int yCordinate) {
        this.yCordinate = yCordinate;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "robotID=" + robotID +
                ", timeTileIsReserved=" + timeTileIsReserved +
                ", xCordinate=" + xCordinate +
                ", yCordinate=" + yCordinate +
                '}';
    }
}
