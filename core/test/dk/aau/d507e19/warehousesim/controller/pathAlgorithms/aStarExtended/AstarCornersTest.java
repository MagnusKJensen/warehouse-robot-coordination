package dk.aau.d507e19.warehousesim.controller.pathAlgorithms.aStarExtended;


import dk.aau.d507e19.warehousesim.RunConfigurator;
import dk.aau.d507e19.warehousesim.Simulation;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.aStar.AStarTile;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.aStar.Astar;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.server.Server;
import dk.aau.d507e19.warehousesim.exception.NoPathFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;


public class AstarCornersTest {

    AstarCorners astarCorners;
    Server server = Mockito.mock(Server.class);
    Robot robot = Mockito.mock(Robot.class);

    @Before
    public void initiate(){
        RunConfigurator.setDefaultRunConfiguration();
        when(server.getGridWidth()).thenReturn(Simulation.getWarehouseSpecs().wareHouseWidth);
        when(server.getGridHeight()).thenReturn(Simulation.getWarehouseSpecs().wareHouseHeight);
        astarCorners = new AstarCorners(server, robot);
    }

    @Test
    public void addNeighborTileToOpenList() {
    }

    @Test
    public void addCornersToG() {
        // Sets coordinates
        astarCorners.xStart = 0;
        astarCorners.yStart = 1;
        astarCorners.xEndPosition = 4;
        astarCorners.yEndPosition = 6;

        // Adds start tile
        astarCorners.addStartTileToClosedList();

        // Try catch when calculating path, should not throw
        try {
            astarCorners.calculatePath2();
        } catch (NoPathFoundException e) {
            e.printStackTrace();
        }

        AStarTile neighbor = astarCorners.grid[5][6];

        int oldG = neighbor.getG();

        astarCorners.addCornersToG(neighbor);

        int newG = neighbor.getG();

        assertTrue(oldG < newG);

    }

}
