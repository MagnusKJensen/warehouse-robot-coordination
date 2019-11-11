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


    }

    @Test
    public void addNeighborTileToOpenList() {
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
