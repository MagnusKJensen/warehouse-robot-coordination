package dk.aau.d507e19.warehousesim.controller.server;


import dk.aau.d507e19.warehousesim.SimulationApp;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.when;

public class ReservationManagerTest {

    private ReservationManager reservationManager;


    @Test
    public void reserve() {

    }

    @Test
    public void canReserveIndefinitely() {

    }

    @Test
    public void isReserved() {
        Server server = Mockito.mock(Server.class);
        when(server.getTimeInSeconds()).thenReturn(30L);

        Robot robot = Mockito.mock(Robot.class);
        when(robot.getGridCoordinate()).thenReturn(new GridCoordinate(0, 0));

        ReservationManager reservationManager = new ReservationManager(30, 30, server);
        reservationManager.reserve(robot, robot.getGridCoordinate(), new TimeFrame(0, 50));

        assertTrue(reservationManager.isReserved(robot.getGridCoordinate(), new TimeFrame(0, 50)));
    }

    @Test
    public void whoReserved() {

    }

    @Test
    public void testWhoReserved() {

    }

    @Test
    public void getReservationsBy() {

    }

    @Test
    public void removeOutdatedReservationsBy() {

    }

    @Test
    public void removeReservation() {

    }

    @Test
    public void isBinReserved() {

    }

    @Test
    public void testReserve() {

    }
}