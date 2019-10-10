package dk.aau.d507e19.warehousesim.controller.server;

public class TimeFrame {

    private long startTime;
    private long endTime;
    private TimeMode timeMode;

    private enum TimeMode{
        UNBOUNDED, BOUNDED;
    }

    public TimeFrame(long startTime, long endTime){
        this.startTime = startTime;
        this.endTime = endTime;
        timeMode = TimeMode.BOUNDED;
    }

    private TimeFrame(long startTime){
        this.startTime = startTime;
        this.endTime = -1;
        timeMode = TimeMode.UNBOUNDED;
    }

    public static TimeFrame indefiniteTimeFrameFrom(long startTime){
        return new TimeFrame(startTime);
    }

    public boolean isWithinTimeFrame(long currentTime){
        /*if(timeMode == TimeMode.UNBOUNDED)
            return currentTime >= startTime;

        return currentTime >= startTime && */
        return false;
    }


}
