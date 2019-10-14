package dk.aau.d507e19.warehousesim.controller.robot;

public class Pause implements MovementAction {

    private final long pauseLengthInTicks;
    private long remainingPause;

    public Pause(long pauseLengthInTicks) {
        this.pauseLengthInTicks = pauseLengthInTicks;
        this.remainingPause = pauseLengthInTicks;
    }

    @Override
    public void perform() {
        if(isFinished()) throw new IllegalStateException("Attempted to perform pause action that is already completed");
        remainingPause -= 1;
    }

    @Override
    public boolean isFinished() {
        return remainingPause <= 0;
    }

}
