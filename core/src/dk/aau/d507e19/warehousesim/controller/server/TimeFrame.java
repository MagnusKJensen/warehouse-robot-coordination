package dk.aau.d507e19.warehousesim.controller.server;

public class TimeFrame {

    public static final TimeFrame ALL_TIME = TimeFrame.indefiniteTimeFrameFrom(Long.MIN_VALUE);
    private long startTime;
    private long endTime;
    private TimeMode timeMode;

    public boolean overlaps(TimeFrame otherFrame) {
        if(this.timeMode == TimeMode.UNBOUNDED){
            // If both frames are unlimited they always overlap
            if(otherFrame.getTimeMode() == TimeMode.UNBOUNDED)
                return true;

            return this.isBeforeTimeFrame(otherFrame.endTime);
        }

        // If the other frame is unbounded:
        // they overlap only if this frame has not ended when the other one starts
        if(otherFrame.getTimeMode() == TimeMode.UNBOUNDED)
            return !this.isOutdated(otherFrame.getStart());

        return isWithinTimeFrame(otherFrame.startTime) || isWithinTimeFrame(otherFrame.endTime);
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
