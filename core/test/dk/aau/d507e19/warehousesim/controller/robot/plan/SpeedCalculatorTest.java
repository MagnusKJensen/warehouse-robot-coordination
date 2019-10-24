package dk.aau.d507e19.warehousesim.controller.robot.plan;

import dk.aau.d507e19.warehousesim.controller.path.Path;
import dk.aau.d507e19.warehousesim.controller.path.Step;
import dk.aau.d507e19.warehousesim.controller.robot.MovementPredictor;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.server.Reservation;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class SpeedCalculatorTest {

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

        SpeedCalculator speedCalculator = new SpeedCalculator(robot, new Path(pausedSteps).getLines().get(0));
        SpeedCalculator speedCalculator2 = new SpeedCalculator(robot, new Path(pausedSteps).getLines().get(1));

        for(int i = 0; i < speedCalculator.getTotalTimeInTicks(); i++){
            if(i == 115)
                System.out.println("hey");
            System.out.println("Time : " + i + " || Position : " + speedCalculator.getPositionAfter(i));
        }

        //Path pausedPath = new
    }



}