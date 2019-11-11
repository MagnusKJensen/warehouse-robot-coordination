package dk.aau.d507e19.warehousesim.controller.pathAlgorithms.aStar;

import dk.aau.d507e19.warehousesim.WarehouseSpecs;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.robot.RobotController;
import dk.aau.d507e19.warehousesim.controller.server.Server;
import org.junit.Before;
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
        when(server.getGridWidth()).thenReturn(WarehouseSpecs.wareHouseWidth);
        when(server.getGridHeight()).thenReturn(WarehouseSpecs.wareHouseHeight);
        astar = new Astar(server, robot);
    }

    @Test
    public void fillGrid() {

        AStarTile[][] testGrid = new AStarTile[WarehouseSpecs.wareHouseWidth][WarehouseSpecs.wareHouseHeight];
        AStarTile[][] actualGrid = astar.fillGrid(WarehouseSpecs.wareHouseWidth, WarehouseSpecs.wareHouseHeight);

        for (int i = 0; i < WarehouseSpecs.wareHouseWidth; i++) {
            for (int j = 0; j < WarehouseSpecs.wareHouseHeight; j++) {
                testGrid[i][j] = new AStarTile(i, j);
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

        // Makes F bigger in the list so that it deletes the old object when the new object has a lower F
        asNeighbor.setG(500);
        asNeighbor.calculateF();

        int fBig = astar.openList.get(0).getF();

        astar.addNeighborTileToOpenList(neighbor);

        assertEquals(1, astar.openList.size());

        int fSmall = astar.openList.get(0).getF();

        // Asserts that the same tile with the lowest F is now in the list.
        assertEquals(1, astar.openList.size());
        assertTrue(fSmall < fBig);

    }

    @Test
    public void addTilesToClosedList() {
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
