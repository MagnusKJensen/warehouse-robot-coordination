package dk.aau.d507e19.warehousesim.controller.pathAlgorithms.aStar;

import dk.aau.d507e19.warehousesim.WarehouseSpecs;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.robot.RobotController;
import dk.aau.d507e19.warehousesim.controller.server.Server;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static org.junit.Assert.*;


public class AstarTest {
    Astar astar;
    Server server = Mockito.mock(Server.class);
    Robot robot = Mockito.mock(Robot.class);

    @Test
    public void getGrid() {

    }

    @Test
    public void fillGrid() {
        astar = new Astar(server, robot);

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
