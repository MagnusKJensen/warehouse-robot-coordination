package dk.aau.d507e19.warehousesim;

public class TickTimer {

    private long startingTicks;
    private long remainingTicks;

    public TickTimer(long startingTicks) {
        this.startingTicks = startingTicks;
        this.remainingTicks = startingTicks;
    }

    public void decrement(){
        remainingTicks -= 1;
    }

    public boolean isDone(){
        return remainingTicks <= 0;
    }

    public long getStartingTicks() {
        return startingTicks;
    }

    public long getRemainingTicks() {
        return remainingTicks;
    }

    public void reset() {
        remainingTicks = startingTicks;
    }
}
