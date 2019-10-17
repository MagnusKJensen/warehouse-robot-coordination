package dk.aau.d507e19.warehousesim.controller.server;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TimeFrameTest {

    @Test
    public void indefiniteWithInTimeFrameTest() {
        TimeFrame timeFrame = TimeFrame.indefiniteTimeFrameFrom(560);
        assertTrue(timeFrame.isWithinTimeFrame(560));
        assertTrue(timeFrame.isWithinTimeFrame(561));
        assertTrue(timeFrame.isWithinTimeFrame(1500));
        assertTrue(timeFrame.isWithinTimeFrame(Long.MAX_VALUE));
    }

    @Test
    public void indefiniteNotWithinTimeFrame() {
        TimeFrame timeFrame = TimeFrame.indefiniteTimeFrameFrom(223);
        assertFalse(timeFrame.isWithinTimeFrame(222));
        assertFalse(timeFrame.isWithinTimeFrame(0));
        assertFalse(timeFrame.isWithinTimeFrame(-5000));
        assertFalse(timeFrame.isWithinTimeFrame(Long.MIN_VALUE));
    }

    @Test
    public void boundedNotWithinTimeFrameTest() {
        TimeFrame timeFrame = new TimeFrame(150, 353);
        assertFalse(timeFrame.isWithinTimeFrame(354));
        assertFalse(timeFrame.isWithinTimeFrame(149));
        assertFalse(timeFrame.isWithinTimeFrame(-5000));
        assertFalse(timeFrame.isWithinTimeFrame(Long.MIN_VALUE));
    }

    @Test
    public void boundedWithinTimeFrameTest() {
        TimeFrame timeFrame = new TimeFrame(150, 353);
        assertTrue(timeFrame.isWithinTimeFrame(353));
        assertTrue(timeFrame.isWithinTimeFrame(150));
        assertTrue(timeFrame.isWithinTimeFrame(236));
        assertTrue(timeFrame.isWithinTimeFrame(352));
    }

    @Test
    public void beforeTimeFrameTrueTest() {
        TimeFrame timeFrame = new TimeFrame(150, 353);
        assertTrue(timeFrame.isBeforeTimeFrame(149));
        assertTrue(timeFrame.isBeforeTimeFrame(65));
        assertTrue(timeFrame.isBeforeTimeFrame(0));
        assertTrue(timeFrame.isBeforeTimeFrame(Long.MIN_VALUE));
    }

    @Test
    public void beforeTimeFrameFalseTest() {
        TimeFrame timeFrame = new TimeFrame(150, 353);
        assertFalse(timeFrame.isBeforeTimeFrame(150));
        assertFalse(timeFrame.isBeforeTimeFrame(210));
        assertFalse(timeFrame.isBeforeTimeFrame(360));
        assertFalse(timeFrame.isBeforeTimeFrame(Long.MAX_VALUE));
    }

    @Test
    public void afterTimeFrameTrueTest() {
        TimeFrame timeFrame = new TimeFrame(150, 353);
        assertTrue(timeFrame.isOutdated(354));
        assertTrue(timeFrame.isOutdated(1000));
        assertTrue(timeFrame.isOutdated(Long.MAX_VALUE));
    }

    @Test
    public void afterTimeFrameFalseTest() {
        TimeFrame timeFrame = new TimeFrame(150, 353);
        assertFalse(timeFrame.isOutdated(353));
        assertFalse(timeFrame.isOutdated(216));
        assertFalse(timeFrame.isOutdated(0));
        assertFalse(timeFrame.isOutdated(Long.MIN_VALUE));
    }

    @Test
    public void afterTimeFrameUnboundedTest() {
        TimeFrame timeFrame = TimeFrame.indefiniteTimeFrameFrom(3);
        assertFalse(timeFrame.isOutdated(400));
        assertFalse(timeFrame.isOutdated(0));
        assertFalse(timeFrame.isOutdated(-1));
        assertFalse(timeFrame.isOutdated(Long.MAX_VALUE));
    }

}