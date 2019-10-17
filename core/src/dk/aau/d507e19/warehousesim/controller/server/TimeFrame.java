package dk.aau.d507e19.warehousesim.controller.server;

public class TimeFrame {

    public static final TimeFrame ALL_TIME = TimeFrame.indefiniteTimeFrameFrom(Long.MIN_VALUE);
    private long startTime;
    private long endTime;
    private TimeMode timeMode;

    public boolean overlaps(TimeFrame otherFrame) {
        return false;
        // TODO: 16/10/2019 Functionality
    }

    private enum TimeMode{
        UNBOUNDED, BOUNDED;
    }

    public TimeFrame(long startTime, long endTime){
        this.startTime = startTime;
        this.endTime = endTime;
        timeMode = TimeMode.BOUNDED;
    }

    public static TimeFrame indefiniteTimeFrameFrom(long startTime){
        return new TimeFrame(startTime);
    }

    private TimeFrame(long startTime){
        this.startTime = startTime;
        this.endTime = -1;
        timeMode = TimeMode.UNBOUNDED;
    }

    public boolean isWithinTimeFrame(long currentTime){
        if(timeMode == TimeMode.UNBOUNDED)
            return currentTime >= startTime;

        return currentTime >= startTime && currentTime <= endTime;
    }

    public boolean isOutdated(long currentTime){
        if(timeMode == TimeMode.UNBOUNDED) return false;
        return currentTime > endTime;
    }

    public boolean isBeforeTimeFrame(long currentTime){
        return currentTime < startTime;
    }

    public long getStart() {
        return startTime;
    }

    public long getEnd() {
        return endTime;
    }

    public TimeMode getTimeMode() {
        return timeMode;
    }

    @Override
    public String toString() {
        return "{" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
                ", timeMode=" + timeMode +
                '}';
    }
}
