package dk.aau.d507e19.warehousesim;

public class TimeUtils {

    public static int secondsToTicks(float seconds){
        int ticks = (int) Math.ceil(seconds * (float)SimulationApp.TICKS_PER_SECOND);
        return ticks;
    }


    public static float tickToSeconds(float pauseDuration) {
        return (float)(SimulationApp.MILLIS_PER_TICK * pauseDuration) /  1000f;
    }
}
