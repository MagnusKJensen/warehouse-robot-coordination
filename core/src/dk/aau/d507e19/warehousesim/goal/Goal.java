package dk.aau.d507e19.warehousesim.goal;

public interface Goal {
    boolean isReached();
    long goalReachedAtTime();
    String toString();
    void update();
}
