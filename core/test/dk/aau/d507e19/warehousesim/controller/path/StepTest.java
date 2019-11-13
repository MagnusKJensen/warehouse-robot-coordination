package dk.aau.d507e19.warehousesim.controller.path;

import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import org.junit.Test;

import static org.junit.Assert.*;

public class StepTest {

    @Test (expected = IllegalArgumentException.class)
    public void zeroWaitTimeTest() {
        Step step = new Step(0, 0, 0);
    }


    @Test
    public void invalidContinuationSamePosition() {
        Step firstStep = new Step(7, 9);
        Step secondStep = new Step(7, 9);
        assertFalse(secondStep.isStepValidContinuationOf(firstStep));
    }

    @Test
    public void invalidContinuationNotConnectedPositionTest() {
        Step firstStep = new Step(8, 10);
        Step secondStep = new Step(7, 9);
        assertFalse(secondStep.isStepValidContinuationOf(firstStep));
    }

    @Test
    public void invalidContinuationOfSelfTest() {
        // Movement step
        Step step = new Step(0, 0);
        assertFalse(step.isStepValidContinuationOf(step));
    }

    @Test
    public void validContinuationFirstStepTest() {
        Step firstStep = new Step(0, 0, 50);
        assertTrue(firstStep.isStepValidContinuationOf(null));
    }

    @Test
    public void validContinuationWaitToMovementTest() {
        Step firstStep = new Step(0, 0, 50);
        Step secondStep = new Step(0, 1);
        assertTrue(secondStep.isStepValidContinuationOf(firstStep));
    }

    @Test
    public void invalidContinuationWaitToMovementTest() {
        Step firstStep = new Step(0, 0, 50);
        Step secondStep = new Step(0, 0);
        assertFalse(secondStep.isStepValidContinuationOf(firstStep));
    }

    @Test
    public void validContinuationMoveToWaitTest() {
        Step firstStep = new Step(9, 9);
        Step secondStep = new Step(9, 9, 50);
        assertTrue(secondStep.isStepValidContinuationOf(firstStep));
    }

    @Test
    public void invalidContinuationMoveToWaitTest() {
        Step firstStep = new Step(8, 9);
        Step secondStep = new Step(9, 9, 50);
        assertFalse(secondStep.isStepValidContinuationOf(firstStep));
    }

    @Test (expected = IllegalStateException.class)
    public void getTimeOnMovementStepTest() {
        Step step = new Step(8, 9);
        step.getWaitTimeInTicks();
    }

    @Test
    public void getTimeTest() {
        Step step = new Step(8, 9, 50);
        assertEquals(50, step.getWaitTimeInTicks());
    }




}