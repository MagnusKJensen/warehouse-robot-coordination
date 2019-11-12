package dk.aau.d507e19.warehousesim.controller.pathAlgorithms.aStarExtended;


import dk.aau.d507e19.warehousesim.RunConfigurator;
import dk.aau.d507e19.warehousesim.Simulation;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.aStar.Astar;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.server.Server;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;


public class AstarCornersTest {

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
    public void addNeighborTileToOpenList() {
    }

    @Test
    public void addCornersToG() {
    }

    @Test
    public void calculatePath() {
    }
}
