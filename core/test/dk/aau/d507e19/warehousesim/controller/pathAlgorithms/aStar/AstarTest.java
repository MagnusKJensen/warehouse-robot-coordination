package dk.aau.d507e19.warehousesim.controller.pathAlgorithms.aStar;

import dk.aau.d507e19.warehousesim.RunConfigurator;
import dk.aau.d507e19.warehousesim.Simulation;
import dk.aau.d507e19.warehousesim.WarehouseSpecs;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.robot.RobotController;
import dk.aau.d507e19.warehousesim.controller.server.Server;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;


public class AstarTest {
    Astar astar;
    Server server = Mockito.mock(Server.class);
    Robot robot = Mockito.mock(Robot.class);

    @Before
    public void initiate(){
        RunConfigurator.setDefaultRunConfiguration();
        when(server.getGridWidth()).thenReturn(Simulation.getWarehouseSpecs().wareHouseWidth);
        when(server.getGridHeight()).thenReturn(Simulation.getWarehouseSpecs().wareHouseHeight);
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

        int xStart = 0;
        int yStart = 5;

        astar.addStartTileToClosedList(xStart,yStart);

        // Asserts that start tile is in closed list
        assertEquals(xStart, astar.closedList.get(0).getCurrentXPosition());
        assertEquals(yStart, astar.closedList.get(0).getCurrentYPosition());

        // Asserts that the tile is blocked in the grid
        assertTrue(astar.grid[xStart][yStart].isBlocked());

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
        AStarTile tile = new AStarTile(0,8);

        astar.openList.add(tile);


    }

    @Test
    public void createTemporaryPath() {
    }

    @Test
    public void createPathListFromClosedList() {
    }

    @Test
    public void calculatePath() {
    }

    @Test
    public void clear() {
    }

    @Test
    public void isReserved() {
    }

    @Test
    public void testCalculatePath() {
    }
}
