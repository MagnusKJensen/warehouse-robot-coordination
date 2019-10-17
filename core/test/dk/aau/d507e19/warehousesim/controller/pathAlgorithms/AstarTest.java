package dk.aau.d507e19.warehousesim.controller.pathAlgorithms;


import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import org.junit.jupiter.api.Test;


public class AstarTest {

    int gridLength;
    Astar astar;


    @Test
    public void main() {
    }

    // Checks if grid is filled correctly
    @Test
    public void fillGridTest() {
/*
        gridLength = 10;
        astar = new Astar(gridLength);

        for (int i = 0; i < gridLength; i++) {
            for (int j = 0; j < gridLength; j++) {
                assertEquals(i, astar.getGrid()[i][j].getCurrentXPosition());
                assertEquals(j, astar.getGrid()[i][j].getCurrentYPosition());
            }

        }*/
    }

    @Test
    public void addStartTileToClosedListTest() {
        /*astar = new Astar(10);

        astar.calculatePath(new GridCoordinate(0,0), new GridCoordinate(0,4));

        // Sets startTile into dummy tile
        AStarTile startTile = astar.getGrid()[astar.xStart][astar.yStart];

        // Adds startTile into closedList
        astar.addStartTileToClosedList(astar.xStart,astar.yStart);

        // Asserts til startTile is the same as the one in closedList
        assertEquals(startTile, astar.closedList.get(0));

        // Asserts that the tile in closedList is blocked
        assertTrue(astar.closedList.get(0).isBlocked());

        // Asserts that the startTile is the same as the one becoming a currentTile.
        assertEquals(startTile, astar.closedList.get(astar.closedList.size() - 1));*/

    }

    //TODO: find a way to add your own start and end positions.
    @Test
    public void checkNeighborValidity() {
        /*astar = new Astar(10);

        // Dummy neighbors to test
        AStarTile neighbor01 = astar.getGrid()[astar.xStart][astar.yStart + 1];
        AStarTile neighbor10 = astar.getGrid()[astar.xStart + 1][astar.yStart];
        AStarTile neighborWrong = astar.getGrid()[astar.xStart][astar.yStart];

        // Adds the first tile to closedList
        astar.addStartTileToClosedList(astar.xStart, astar.yStart);

        // Checks neighbors and adds them to openList
        astar.checkNeighborValidity();

        // Asserts that openList contains the right neighbors
        assertTrue(astar.openList.contains(neighbor01));
        assertTrue(astar.openList.contains(neighbor10));
        assertFalse(astar.openList.contains(neighborWrong));

        // Asserts that openList contains the right amount of neighbors
        assertEquals(2, astar.openList.size());*/

    }

    @Test
    public void addNeighborTileToOpenList() {
        /*gridLength = 10;
        astar = new Astar(10);

        // Dummy neighbors to test
        //Tile neighbor01 = grid[xStart][yStart + 1];
        //Tile neighbor10 = grid[xStart + 1][yStart];
        //Tile neighborWrong = grid[xStart][yStart];

        // Adds the first tile to closedList
        //astar.addStartTileToClosedList(xStart, yStart);

        // Checks neighbors and adds them to openList
        // astar.checkNeighborValidity();*/
    }

    @Test
    public void addTilesToClosedList() {
    }

    @Test
    public void calculatePath() {
       /*gridLength = 10;
        astar = new Astar(10);
        //GridCoordinate start = new GridCoordinate(xStart, yStart);
        GridCoordinate end = new GridCoordinate(0,0);

        //astar.calculatePath(start, end);


*/
    }
}
