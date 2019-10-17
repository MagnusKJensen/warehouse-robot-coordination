package dk.aau.d507e19.warehousesim.controller.pathAlgorithms.Astar;

public class Reservation {
    int robotID;
    private float timeTileIsReservedFrom;
    private float timeTileIsReservedTo;
    int xCordinate;
    int yCordinate;

    public Reservation(int robotID, float timeTileIsReservedFrom, float timeTileIsReservedTo) {
        this.robotID = robotID;
        this.timeTileIsReservedFrom = timeTileIsReservedFrom;
        this.timeTileIsReservedTo = timeTileIsReservedTo;

    }

    public int getRobotID() {
        return robotID;
    }

    public float getTimeTileIsReservedFrom() {
        return timeTileIsReservedFrom;
    }

    public float getTimeTileIsReservedTo() {
        return timeTileIsReservedTo;
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
                ", timeTileIsReservedFrom=" + timeTileIsReservedFrom +
                ", timeTileIsReservedTo=" + timeTileIsReservedTo +
                ", xCordinate=" + xCordinate +
                ", yCordinate=" + yCordinate +
                '}';
    }
}
