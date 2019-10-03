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
        Astar astar = new Astar(grid, 0, 0,0,4);

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

    public void calculatePath() {

        // While is true if the currentTile does not have the same x coordinate and the same y coordinate as the end Tile.
        while (!(currentTile.getCurrentXPosition() == xEndposition && currentTile.getCurrentYPosition() == yEndposition)) {

            // If the closedList has more than one tile, then set previousTile
            // Previous tile is the last in closedList
            if (closedList.size() > 2) {
                previousTile = closedList.get(closedList.size() - 2);
                currentTile.setPreviousXposition(previousTile.getCurrentXPosition());
                currentTile.setPreviousYposition(previousTile.getCurrentYPosition());
            }

            // Add the valid tiles to openList
            addTilesToOpenList();

            // Add the lowest cost tile to closedList
            addTilesToClosedList();

            // CurrentTile is now the top tile in closedList
            currentTile = closedList.get(closedList.size() - 1);
        }
    }

    public void addTilesToOpenList() {

        // If the current tiles neighbor (x,y-1) is valid, and not blocked (in closedList)
        if (!(currentTile.getCurrentYPosition() - 1 < 0 || grid[currentTile.getCurrentXPosition()][currentTile.getCurrentYPosition() - 1].isBlocked())) {


            grid[currentTile.getCurrentXPosition()][currentTile.getCurrentXPosition()].setBlocked(true);
            if(!(openList.contains(grid[currentTile.getCurrentYPosition() - 1][currentTile.getCurrentXPosition()])|| closedList.contains(grid[currentTile.getCurrentYPosition()-1][currentTile.getCurrentXPosition()])))
            openList.add(grid[currentTile.getCurrentYPosition() - 1][currentTile.getCurrentXPosition()]);

        }
        if (!(currentTile.getCurrentYPosition() + 1 > 10 || grid[currentTile.getCurrentXPosition()][currentTile.getCurrentYPosition() + 1].isBlocked())) {
            grid[currentTile.getCurrentYPosition() + 1][currentTile.getCurrentXPosition()].setBlocked(true);
            if(!(openList.contains(grid[currentTile.getCurrentYPosition() + 1][currentTile.getCurrentXPosition()])|| closedList.contains(grid[currentTile.getCurrentYPosition()+1][currentTile.getCurrentXPosition()])))
            openList.add(grid[currentTile.getCurrentYPosition() + 1][currentTile.getCurrentXPosition()]);


        }
        if (!(currentTile.getCurrentXPosition() - 1 < 0 || grid[currentTile.getCurrentXPosition() - 1][currentTile.getCurrentYPosition()].isBlocked())) {
            grid[currentTile.getCurrentYPosition()][currentTile.getCurrentXPosition() - 1].setBlocked(true);
            if(!(openList.contains(grid[currentTile.getCurrentYPosition()][currentTile.getCurrentXPosition() - 1]) || closedList.contains(grid[currentTile.getCurrentYPosition()][currentTile.getCurrentXPosition() -1])))
                openList.add(grid[currentTile.getCurrentYPosition()][currentTile.getCurrentXPosition() - 1]);


        }
        if (!(currentTile.getCurrentXPosition() + 1 > 10 || grid[currentTile.getCurrentXPosition() + 1][currentTile.getCurrentYPosition()].isBlocked())) {
            grid[currentTile.getCurrentYPosition()][currentTile.getCurrentXPosition() + 1].setBlocked(true);
            if(!(openList.contains(grid[currentTile.getCurrentYPosition()][currentTile.getCurrentXPosition() + 1]) || closedList.contains(grid[currentTile.getCurrentYPosition()][currentTile.getCurrentXPosition() + 1])))
            openList.add(grid[currentTile.getCurrentYPosition()][currentTile.getCurrentXPosition() + 1]);

        }
        for (Tile tile : openList) {
            tile.calculateH(xEndposition, yEndposition);
            if (closedList.size() > 2)
                tile.calculateG(previousTile.getG());
            else
                tile.calculateG(currentTile.getG());
        }
        openList.sort(new OpenListSorter());
        for (Tile tile : openList) {
            System.out.println(tile.calculateF());

        }
        System.out.println("----------------------------------------");
    }

    public void addTilesToClosedList() {

        openList.get(0).setBlocked(true);
        closedList.add(openList.get(0));
        openList.remove(0);

    }



}
