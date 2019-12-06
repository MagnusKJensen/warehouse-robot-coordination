package dk.aau.d507e19.warehousesim;

import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.server.ReservationManager;
import dk.aau.d507e19.warehousesim.controller.server.Server;
import dk.aau.d507e19.warehousesim.storagegrid.GridBounds;
import org.junit.Before;

public class PathFinderPerformanceTests {

    private Server server;
    private ReservationManager reservationManager;

    private static long seed = 0L;
    private GridBounds gridBounds;
    private GridCoordinate destination;
    private GridCoordinate start;

    @Before
    public void setUp() throws Exception {
        gridBounds = generateBounds();
        destination = generateDestination(seed, gridBounds);
        start = generateStart(seed, gridBounds);

        // Mock server
        reservationManager = createFilledReservationTable(seed, server, gridBounds);

        seed++;
    }

    private ReservationManager createFilledReservationTable(long seed, Server server, GridBounds gridBounds) {
        return null; // new ReservationManager...
    }

    private GridBounds generateBounds() {
        return null;
    }

    private GridCoordinate generateStart(long seed, GridBounds bounds) {
        return null;
    }

    private GridCoordinate generateDestination(long seed, GridBounds bounds) {
        return null;
    }

    private void fillReservationTable(long seed, GridBounds bounds){
        
    }
}
