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
        if (currentTile.getCurrentYPosition() - 1 >= 0) {
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
       Tile tiletoDelete = null;
        if (!neighborTile.isBlocked()) {

            neighborTile.setPreviousXposition(currentTile.getCurrentXPosition());
            neighborTile.setPreviousYposition(currentTile.getCurrentYPosition());

            neighborTile.calculateH(xEndposition, yEndposition);

            neighborTile.calculateG(currentTile.getG());
            neighborTile.calculateF();
            for (Tile tile : openList) {
                if (neighborTile.getCurrentXPosition() == tile.getCurrentXPosition() && neighborTile.getCurrentYPosition() == tile.getCurrentYPosition()) {
                    if (neighborTile.getF() <= tile.getF()) {
                     tiletoDelete = tile;
                    } else return;
                }

            }
            if(tiletoDelete != null)
            openList.remove(tiletoDelete);

            openList.add(neighborTile);
        }
    }

    public void addTilesToClosedList() {

        grid[openList.get(0).getCurrentXPosition()][openList.get(0).getCurrentYPosition()].setBlocked(true);
        closedList.add(openList.get(0));
        openList.remove(0);
    }

    public void calculatePath() {

        // While is true if the currentTile does not have the same x coordinate and the same y coordinate as the end Tile.
        while (!(currentTile.getCurrentXPosition() == xEndposition && currentTile.getCurrentYPosition() == yEndposition)) {

            // Add the valid tiles to openList
            checkNeighborValidity();

            //sorts openlist in ascending order
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
