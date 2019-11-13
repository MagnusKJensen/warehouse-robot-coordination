package dk.aau.d507e19.warehousesim.controller.pathAlgorithms.aStar;

import dk.aau.d507e19.warehousesim.RunConfigurator;
import dk.aau.d507e19.warehousesim.Simulation;
import dk.aau.d507e19.warehousesim.WarehouseSpecs;
import dk.aau.d507e19.warehousesim.controller.path.Path;
import dk.aau.d507e19.warehousesim.controller.path.Step;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.MovementPredictor;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.robot.RobotController;
import dk.aau.d507e19.warehousesim.controller.server.Reservation;
import dk.aau.d507e19.warehousesim.controller.server.ReservationManager;
import dk.aau.d507e19.warehousesim.controller.server.Server;
import dk.aau.d507e19.warehousesim.controller.server.TimeFrame;
import dk.aau.d507e19.warehousesim.exception.NoPathFoundException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;


public class AstarTest {
    Astar astar;
    Server server = Mockito.mock(Server.class);
    Robot robot = Mockito.mock(Robot.class);
    ReservationManager reservationManager = Mockito.mock(ReservationManager.class);

    @Before
    public void initiate(){
        RunConfigurator.setDefaultRunConfiguration();
        when(server.getGridWidth()).thenReturn(Simulation.getWarehouseSpecs().wareHouseWidth);
        when(server.getGridHeight()).thenReturn(Simulation.getWarehouseSpecs().wareHouseHeight);
        when(server.getReservationManager()).thenReturn(reservationManager);
        astar = new Astar(server, robot);
    }

    @Test
    public void fillGrid() {

        AStarTile[][] testGrid = new AStarTile[Simulation.getWarehouseSpecs().wareHouseWidth][Simulation.getWarehouseSpecs().wareHouseHeight];
        AStarTile[][] actualGrid = astar.fillGrid(Simulation.getWarehouseSpecs().wareHouseWidth, Simulation.getWarehouseSpecs().wareHouseHeight);

        for (int i = 0; i < Simulation.getWarehouseSpecs().wareHouseWidth; i++) {
            for (int j = 0; j < Simulation.getWarehouseSpecs().wareHouseHeight; j++) {
                testGrid[i][j] = new AStarTile(i, j);

                // Asserts that the actual grid and the test grid are the same for each coordinate.
                assertEquals(testGrid[i][j], actualGrid[i][j]);

            }
        }
    }

    @Test
    public void addStartTileToClosedList() {

        astar.xStart = 0;
        astar.yStart = 5;

        astar.addStartTileToClosedList();

        // Asserts that start tile is in closed list
        assertEquals(astar.xStart, astar.closedList.get(0).getCurrentXPosition());
        assertEquals(astar.yStart, astar.closedList.get(0).getCurrentYPosition());

        // Asserts that the tile is blocked in the grid
        assertTrue(astar.grid[astar.xStart][astar.yStart].isBlocked());

        // Asserts that the top tile is now the current tile.
        assertEquals(astar.currentTile, astar.closedList.get(astar.closedList.size()-1));


    }

    @Test
    public void checkNeighborValidity() {

        astar.currentTile = new AStarTile(4,8);

        astar.checkNeighborValidity();

        // Asserts that the size of the list matches the amount of neighbors
        assertEquals(4, astar.openList.size());

        AStarTile upNeighbor = new AStarTile(4,9);
        AStarTile downNeighbor = new AStarTile(4,7);
        AStarTile leftNeighbor = new AStarTile(3,8);
        AStarTile rightNeighbor = new AStarTile(5,8);

        // Asserts that all the right neighbors are in the list
        assertTrue(astar.openList.contains(upNeighbor));
        assertTrue(astar.openList.contains(downNeighbor));
        assertTrue(astar.openList.contains(leftNeighbor));
        assertTrue(astar.openList.contains(rightNeighbor));

    }

    @Test
    public void checkNeighborValidityBlocked(){
        astar.currentTile = new AStarTile(0,8);

        AStarTile upNeighbor = new AStarTile(0,9);
        AStarTile downNeighbor = new AStarTile(0,7);
        AStarTile leftNeighbor = new AStarTile(-1,8);
        AStarTile rightNeighbor = new AStarTile(1,8);

        astar.grid[upNeighbor.getCurrentXPosition()][upNeighbor.getCurrentYPosition()].setBlocked(true);

        astar.checkNeighborValidity();

        // Asserts that the size of the list matches the amount of valid neighbors
        assertEquals(2, astar.openList.size());

        // Asserts that all the right neighbors are in the list
        assertFalse(astar.openList.contains(upNeighbor));
        assertTrue(astar.openList.contains(downNeighbor));
        assertFalse(astar.openList.contains(leftNeighbor));
        assertTrue(astar.openList.contains(rightNeighbor));
    }

    @Test
    public void addNeighborTileToOpenList() {
        GridCoordinate neighbor = new GridCoordinate(4,5);
        AStarTile asNeighbor;

        // Needs these to work
        astar.xEndPosition = 8;
        astar.yEndPosition = 8;

        // To avoid null pointer exception when it sets previous coordinates
        astar.currentTile = new AStarTile(4,4);

        astar.addNeighborTileToOpenList(neighbor);

        // Asserts that there is now a neighbor in the list
        assertEquals(1, astar.openList.size());

        // Makes AStarTile objects
        asNeighbor = astar.openList.get(0);

        // Asserts that this neighbor is the right one
        assertTrue(astar.openList.contains(asNeighbor));

        // Asserts that if the neighbor now has a higher F it is not replacing the old --------------------------------

        int oldF = astar.openList.get(0).getF();

        // Makes F bigger so that it does not delete the old object when the new object has a higher F
        astar.currentTile.setG(500);

        astar.addNeighborTileToOpenList(neighbor);

        // Asserts that the list still only has one
        assertEquals(1, astar.openList.size());

        int newF = astar.openList.get(0).getF();

        // Asserts that the same tile with the lowest F is now in the list.
        assertEquals(oldF, newF);

        // Asserts that if the neighbor now has a lower F it is replacing the old -------------------------------------

        oldF = newF;

        // The same as before, but now the new tile is better.
        astar.currentTile.setG(-510);

        astar.addNeighborTileToOpenList(neighbor);

        assertEquals(1, astar.openList.size());

        newF = astar.openList.get(0).getF();

        // Asserts that the same tile with the lowest F is now in the list.
        assertEquals(1, astar.openList.size());
        assertTrue(newF < oldF);

    }

    @Test
    public void addTilesToClosedList() {
        AStarTile tile = astar.grid[0][8];

        astar.openList.add(tile);

        // Asserts that tile is in openlist and not blocked.
        assertEquals(1, astar.openList.size());
        assertTrue(astar.closedList.isEmpty());
        assertFalse(astar.grid[tile.getCurrentXPosition()][tile.getCurrentYPosition()].isBlocked());

        astar.addTilesToClosedList();

        // Asserts that tile is now moved and blocked.
        assertTrue(astar.openList.isEmpty());
        assertEquals(1, astar.closedList.size());
        assertTrue(astar.closedList.get(0).isBlocked());

    }

    @Test
    public void createPathListFromClosedList() {

        // Sets coordinates
        astar.xStart = 0;
        astar.yStart = 1;
        astar.xEndPosition = 4;
        astar.yEndPosition = 6;

        // Adds start tile
        astar.addStartTileToClosedList();

        // Try catch when calculating path, should not throw
        try {
            astar.calculatePath2();
        } catch (NoPathFoundException e) {
            e.printStackTrace();
        }

        // Creates path from the closed list.
        astar.createPathListFromClosedList();

        // Asserts that the finalpath is either smaller or the same size as closed list, it cannot be bigger
        assertTrue(astar.finalPath.size() <= astar.closedList.size());

        // Asserts that it has the same start coordinates
        assertEquals(astar.xStart, astar.finalPath.get(0).getX());
        assertEquals(astar.yStart, astar.finalPath.get(0).getY());

        // Asserts that it has the same end coordinates
        assertEquals(astar.xEndPosition, astar.finalPath.get(astar.finalPath.size()-1).getX());
        assertEquals(astar.yEndPosition, astar.finalPath.get(astar.finalPath.size()-1).getY());
    }

    @Test //TODO: make mockito work for reservationmanager
    public void isReserved() {
        // Sets coordinates
        astar.xStart = 0;
        astar.yStart = 1;
        astar.xEndPosition = 4;
        astar.yEndPosition = 6;

        // Adds start tile
        astar.addStartTileToClosedList();

        // Try catch when calculating path, should not throw
        try {
            astar.calculatePath2();
        } catch (NoPathFoundException e) {
            e.printStackTrace();
        }

        // Creates path from the closed list.
        astar.createPathListFromClosedList();

        // Path from finalpath
        Path path = new Path(Step.fromGridCoordinates(astar.finalPath));

        // Makes a the list of reservations so that it can reserve a tile in the reservation, so that isReserved returns True
        ArrayList<Reservation> listOfReservations = MovementPredictor.calculateReservations(robot, path, server.getTimeInTicks(), 0);


        for (Reservation res: listOfReservations) {
            if (res.equals(listOfReservations.get(listOfReservations.size()-1))){
                when(reservationManager.isReserved(res.getGridCoordinate(), res.getTimeFrame())).thenReturn(true);
            } else {
                when(reservationManager.isReserved(res.getGridCoordinate(), res.getTimeFrame())).thenReturn(false);
            }
        }

        Reservation lastReservation = listOfReservations.get(listOfReservations.size()-1);
        when(reservationManager.hasConflictingReservations(lastReservation)).thenReturn(false);
        when(!reservationManager.canReserve(lastReservation.getGridCoordinate(), TimeFrame.indefiniteTimeFrameFrom(lastReservation.getTimeFrame().getStart()))).thenReturn(false);



            // Should be reserved
        try {
            //assertTrue(astar.isReserved());
            System.out.println(astar.isReserved());
        } catch (NoPathFoundException e) {
            e.printStackTrace();
        }


    }

    @Ignore
    public void calculatePath() {
        // Sets coordinates
        astar.xStart = 0;
        astar.yStart = 1;
        astar.xEndPosition = 4;
        astar.yEndPosition = 6;

        //TODO: der er noget galt med reservations?? se isreserved test
        try{
            astar.calculatePath();
        }catch (NoPathFoundException e){
            e.printStackTrace();
        }

        for (GridCoordinate gc: astar.finalPath) {
            System.out.println(gc.toString());
        }


    }

    @Test
    public void calculatePath2Exception() {

        astar.xStart = 0;
        astar.yStart = 1;
        astar.xEndPosition = 4;
        astar.yEndPosition = 6;

        // Blocks all surrounding tiles so that the try catch fails.
        astar.grid[0][2].setBlocked(true);
        astar.grid[1][1].setBlocked(true);
        astar.grid[0][0].setBlocked(true);

        astar.addStartTileToClosedList();

        try {
            astar.calculatePath2();
            // Makes it catch the exception correctly
            fail();
        } catch (NoPathFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void clear() {

        // Makes all lists and currentTile non empty and not null
        astar.closedList.add(astar.grid[3][3]);
        astar.closedList.get(0).setBlocked(true);
        astar.openList.add(astar.grid[4][5]);
        astar.finalPath.add(new GridCoordinate(2,2));
        astar.currentTile = astar.grid[6][2];

        astar.clear();

        // Asserts that none of the tiles in the grid are blocked
        for (int i = 0; i < astar.server.getGridWidth(); i++) {
            for (int j = 0; j < astar.server.getGridHeight(); j++) {
                assertFalse(astar.grid[i][j].isBlocked());
            }

        }

        // Asserts that all lists are empty and that the currentTile is null
        assertTrue(astar.openList.isEmpty());
        assertTrue(astar.closedList.isEmpty());
        assertTrue(astar.finalPath.isEmpty());
        assertNull(astar.currentTile);
    }
}
