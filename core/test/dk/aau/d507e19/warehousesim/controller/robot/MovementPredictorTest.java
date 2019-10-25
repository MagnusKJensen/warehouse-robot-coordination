package dk.aau.d507e19.warehousesim.controller.robot;

import dk.aau.d507e19.warehousesim.controller.path.Path;
import dk.aau.d507e19.warehousesim.controller.path.Step;
import dk.aau.d507e19.warehousesim.controller.server.Reservation;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class MovementPredictorTest {

    private static Robot robot;

    private static final float acceleration = 0.8f;
    private static final float deceleration = 2f;
    private static final float topSpeed = 3.0f;

    @BeforeClass
    public static void setUp() throws Exception {
        robot = Mockito.mock(Robot.class);
        when(robot.getAccelerationBinSecond()).thenReturn(acceleration);
        when(robot.getDecelerationBinSecond()).thenReturn(deceleration);
        when(robot.getMaxSpeedBinsPerSecond()).thenReturn(topSpeed);
        when(robot.getSize()).thenReturn(1);
    }


    @Test
    public void printTest() {
        ArrayList<Step> pausedSteps = new ArrayList<>();

        pausedSteps.add(new Step(0,0, 60));
        pausedSteps.add(new Step(1,0));
        pausedSteps.add(new Step(2,0));
        pausedSteps.add(new Step(2,0, 50));
        pausedSteps.add(new Step(3,0));
        pausedSteps.add(new Step(4,0));
        pausedSteps.add(new Step(5,0));
        pausedSteps.add(new Step(6,0));

        ArrayList<Reservation> reservations = MovementPredictor.calculateReservations(robot, new Path(pausedSteps), 0, 0);

        //Path pausedPath = new
    }
}