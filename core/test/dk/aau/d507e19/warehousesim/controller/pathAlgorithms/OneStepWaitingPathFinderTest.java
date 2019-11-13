package dk.aau.d507e19.warehousesim.controller.pathAlgorithms;

import dk.aau.d507e19.warehousesim.RunConfigurator;
import dk.aau.d507e19.warehousesim.Simulation;
import dk.aau.d507e19.warehousesim.WarehouseSpecs;
import dk.aau.d507e19.warehousesim.controller.path.Path;
import dk.aau.d507e19.warehousesim.controller.path.Step;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.server.Reservation;
import dk.aau.d507e19.warehousesim.controller.server.ReservationManager;
import dk.aau.d507e19.warehousesim.controller.server.Server;
import dk.aau.d507e19.warehousesim.controller.server.TimeFrame;
import dk.aau.d507e19.warehousesim.exception.NoPathFoundException;
import javafx.beans.binding.When;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class OneStepWaitingPathFinderTest {

    private Simulation simulation;
    private OneStepWaitingPathFinder pathFinder;
    private long simTimeInTicks = 250;
    private Robot robot1, robot2;
    private Server server;

    @Before
    public void setUp() throws Exception {
        RunConfigurator.setDefaultRunConfiguration();
        server = Mockito.mock(Server.class);
        when(server.getTimeInTicks()).thenReturn(simTimeInTicks);

        ReservationManager reservationManager = new ReservationManager(Simulation.getWarehouseSpecs().wareHouseWidth,
                Simulation.getWarehouseSpecs().wareHouseHeight, server);
        when(server.getReservationManager()).thenReturn(reservationManager);

        robot1 = Mockito.mock(Robot.class);
        when(robot1.getAccelerationBinSecond()).thenReturn(Simulation.getWarehouseSpecs().robotAcceleration);
        when(robot1.getDecelerationBinSecond()).thenReturn(Simulation.getWarehouseSpecs().robotDeceleration);
        when(robot1.getMaxSpeedBinsPerSecond()).thenReturn(Simulation.getWarehouseSpecs().robotTopSpeed);
        robot2 = Mockito.mock(Robot.class);
        when(robot2.getAccelerationBinSecond()).thenReturn(Simulation.getWarehouseSpecs().robotAcceleration);
        when(robot2.getDecelerationBinSecond()).thenReturn(Simulation.getWarehouseSpecs().robotDeceleration);
        when(robot2.getMaxSpeedBinsPerSecond()).thenReturn(Simulation.getWarehouseSpecs().robotTopSpeed);
    }

    @Test
    public void calculatePath() throws NoPathFoundException {
        GridCoordinate start = new GridCoordinate(5,5);
        GridCoordinate destination = new GridCoordinate(5,6);
        long expectedWaitTime = 150;

        server.getReservationManager().reserve(
                new Reservation(robot2, destination, new TimeFrame(simTimeInTicks, simTimeInTicks + expectedWaitTime)));

        Path path = new OneStepWaitingPathFinder(robot1, server).calculatePath(start, destination);
        for(Step step : path.getFullPath()){
            System.out.println(step);
        }

    }
}