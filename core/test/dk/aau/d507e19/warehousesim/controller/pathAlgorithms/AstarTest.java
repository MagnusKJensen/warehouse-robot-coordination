package dk.aau.d507e19.warehousesim.controller.pathAlgorithms;


import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import org.junit.Test;

import static org.junit.Assert.*;


public class AstarTest {
/*
    int gridLength, xStart, yStart;
    Tile[][] grid;
    Astar astar;


    @Test
    public void main() {
    }

    // Checks if grid is filled correctly
    @Test
    public void fillGridTest() {
        gridLength = 10;
        grid = new Tile[gridLength][gridLength];
        astar = new Astar(10);

        for (int i = 0; i < gridLength; i++) {
            for (int j = 0; j < gridLength; j++) {
                assertEquals(i, grid[i][j].getCurrentXPosition());
                assertEquals(j, grid[i][j].getCurrentYPosition());
            }

        }
    }

    @Test
    public void addStartTileToClosedListTest() {
        xStart = 0;
        yStart = 0;
        gridLength = 10;
        grid = new Tile[gridLength][gridLength];
        astar = new Astar(10);

        // Sets startTile into dummy tile
        Tile startTile = grid[xStart][yStart];

        // Adds startTile into closedList
        astar.addStartTileToClosedList(xStart,yStart);

        // Asserts til startTile is the same as the one in closedList
        assertEquals(startTile, astar.closedList.get(0));

        // Asserts that the tile in closedList is blocked
        assertTrue(astar.closedList.get(0).isBlocked());

        // Asserts that the startTile is the same as the one becoming a currentTile.
        assertEquals(startTile, astar.closedList.get(astar.closedList.size() - 1));

    }

    @Test
    public void checkNeighborValidity() {
        xStart = 0;
        yStart = 0;
        gridLength = 10;
        grid = new Tile[gridLength][gridLength];
        astar = new Astar(10);

        // Dummy neighbors to test
        Tile neighbor01 = grid[xStart][yStart + 1];
        Tile neighbor10 = grid[xStart + 1][yStart];
        Tile neighborWrong = grid[xStart][yStart];

        // Adds the first tile to closedList
        astar.addStartTileToClosedList(xStart, yStart);

        // Checks neighbors and adds them to openList
        astar.checkNeighborValidity();

        // Asserts that openList contains the right neighbors
        assertTrue(astar.openList.contains(neighbor01));
        assertTrue(astar.openList.contains(neighbor10));
        assertFalse(astar.openList.contains(neighborWrong));

        // Asserts that openList contains the right amount of neighbors
        assertEquals(2, astar.openList.size());

    }

    @Test
    public void addNeighborTileToOpenList() {
        xStart = 3;
        yStart = 3;
        gridLength = 10;
        astar = new Astar(10);

        // Dummy neighbors to test
        Tile neighbor01 = grid[xStart][yStart + 1];
        Tile neighbor10 = grid[xStart + 1][yStart];
        Tile neighborWrong = grid[xStart][yStart];

        // Adds the first tile to closedList
        astar.addStartTileToClosedList(xStart, yStart);

        // Checks neighbors and adds them to openList
        astar.checkNeighborValidity();





    }

    @Test
    public void addTilesToClosedList() {
    }

    @Test
    public void calculatePath() {
        xStart = 5;
        yStart = 5;
        gridLength = 10;
        astar = new Astar(10);
        GridCoordinate start = new GridCoordinate(xStart, yStart);
        GridCoordinate end = new GridCoordinate(0,0);

        astar.calculatePath(start, end);



    }*/
}
