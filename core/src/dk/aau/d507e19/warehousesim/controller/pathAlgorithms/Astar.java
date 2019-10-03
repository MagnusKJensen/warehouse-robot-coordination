package dk.aau.d507e19.warehousesim.controller.pathAlgorithms;

import java.util.ArrayList;

public class Astar {

    private Tile[][] grid;
    private int xEndposition;
    private int yEndposition;
    private int xStartposition;
    private int yStartposition;

    ArrayList<Tile> openList = new ArrayList<>();
    ArrayList<Tile> closedList = new ArrayList<>();
    private Tile currentTile;
    private Tile previousTile;

    public Astar(Tile[][] grid, int xStart, int yStart, int xEnd, int yEnd) {
        this.grid = fillGrid(grid);
        this.xStartposition = xStart;
        this.yStartposition = yStart;
        this.xEndposition = xEnd;
        this.yEndposition = yEnd;
    }

    public static void main(String[] args) {
        // Sets grid size
        int gridLength = 10;

        // Makes new grid
        Tile[][] grid = new Tile[gridLength][gridLength];

        // Makes new Astar object and fills grid
        Astar astar = new Astar(grid, 0, 0, 6, 6);

        // Adds the starting tile to closed list.
        astar.addStartTileToClosedList();

        // Calculates the optimal A* path
        astar.calculatePath();

    }

    public Tile[][] fillGrid(Tile[][] grid) {

        // Fills grid with tiles matching the coordinates
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid.length; j++) {
                grid[i][j] = new Tile(i, j);

            }
        }
        return grid;
    }

    public void addStartTileToClosedList() {

        // Adds startTile to closedList
        closedList.add(grid[xStartposition][yStartposition]);

        // Blocks startTile so that it cannot be used anymore
        grid[xStartposition][yStartposition].setBlocked(true);

        // Sets currentTile to the top tile in closedList (startTile)
        currentTile = closedList.get(closedList.size() - 1);

    }

    public void checkNeighborValidity() {
        //Checks every potential neighbor to currentTile the same way.

        // Checks if neighbor is valid with a valid coordinate
        if (currentTile.getCurrentYPosition() - 1 >= 0) {
            // Adds Neighbor to openList if valid
            addNeighborTileToOpenList(grid[currentTile.getCurrentXPosition()][currentTile.getCurrentYPosition() - 1]);
        }
        if (currentTile.getCurrentYPosition() + 1 < grid.length) {
            addNeighborTileToOpenList(grid[currentTile.getCurrentXPosition()][currentTile.getCurrentYPosition() + 1]);
        }
        if (currentTile.getCurrentXPosition() - 1 >= 0) {
            addNeighborTileToOpenList(grid[currentTile.getCurrentXPosition() - 1][currentTile.getCurrentYPosition()]);
        }
        if (currentTile.getCurrentXPosition() + 1 < grid.length) {
            addNeighborTileToOpenList(grid[currentTile.getCurrentXPosition() + 1][currentTile.getCurrentYPosition()]);
        }
    }

    public void addNeighborTileToOpenList(Tile neighborTile) {

        // Makes new dummy tile
        Tile tileToDelete = null;

        // Checks if neighborTile is blocked (Already in closedList)
        if (!neighborTile.isBlocked()) {

            // Sets the previous coordinates in neighbor tile
            neighborTile.setPreviousXposition(currentTile.getCurrentXPosition());
            neighborTile.setPreviousYposition(currentTile.getCurrentYPosition());

            // Calculates neighborTiles H, G and F
            neighborTile.calculateH(xEndposition, yEndposition);
            neighborTile.calculateG(currentTile.getG());
            neighborTile.calculateF();

            // Checks if neighborTile is already in openList.
            for (Tile tile : openList) {
                if (neighborTile.getCurrentXPosition() == tile.getCurrentXPosition() && neighborTile.getCurrentYPosition() == tile.getCurrentYPosition()) {

                    // If a tile with the same coordinates is already in openList, then check which has the lowest F value.
                    // If the existing tile in openList has the highest F, then it is copied into tileToDelete
                    if (neighborTile.getF() <= tile.getF()) {
                     tileToDelete = tile;

                     // If the neighborTile has the highest F, then return and dont add to openList.
                    } else return;
                }

            }

            // If there is a tile to delete, then delete
            if(tileToDelete != null)
            openList.remove(tileToDelete);

            // Add neighbor tile to openList
            openList.add(neighborTile);
        }
    }

    public void addTilesToClosedList() {

        // Blocks the current tile in the grid before it is moved to closedList.
        grid[openList.get(0).getCurrentXPosition()][openList.get(0).getCurrentYPosition()].setBlocked(true);
        closedList.add(openList.get(0));

        // Removes the tile from the openList.
        openList.remove(0);
    }

    public void calculatePath() {

        // While is true if the currentTile does not have the same x coordinate and the same y coordinate as the end Tile.
        while (!(currentTile.getCurrentXPosition() == xEndposition && currentTile.getCurrentYPosition() == yEndposition)) {

            // Add the valid tiles to openList
            checkNeighborValidity();

            // Sorts openList in ascending order
            openList.sort(new OpenListSorter());

            // Add the lowest cost tile to closedList
            addTilesToClosedList();

            // CurrentTile is now the top tile in closedList
            currentTile = closedList.get(closedList.size() - 1);

        }
        for (Tile tile : closedList) {
            System.out.println(tile.toString());

        }
    }
}
