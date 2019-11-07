package dk.aau.d507e19.warehousesim;

import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.server.Server;

public class HeatMap {

    public static GridCoordinate getLeastCrowdedCoordinate(Server server){

        return null;
    }


    public static float[][] getHeatMap(Server server){
        float[][] heatMap = createZeroInitGrid(server.getGridWidth(), server.getGridHeight());
        for(int x = 0; x < server.getGridWidth(); x++){
            for(int y = 0; y < server.getGridHeight(); y++) {
                heatMap[x][y] = 0f;
            }
        }
        return heatMap;
    }


    private static float[][] createZeroInitGrid(int gridWidth, int gridHeight) {
        float[][] grid =  new float[gridWidth][gridHeight];
        for(int x = 0; x < gridWidth; x++){
            for(int y = 0; y < gridHeight; y++) {
                grid[x][y] = 0f;
            }
        }
        return grid;
    }


}
