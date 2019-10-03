package dk.aau.d507e19.warehousesim.controller.pathAlgorithms;

import java.util.ArrayList;

public class Astar {
    private int gridLength = 10;
    private Tile grid[][] = new Tile[gridLength][gridLength];
    private int xEndposition = 0;
    private int yEndposition = 4;
    private int xStartposition = 0;
    private int yStartposition = 0;

    ArrayList<Tile> openList = new ArrayList<>();
    ArrayList<Tile> closedList = new ArrayList<>();
    private Tile currentTile;
    private Tile previousTile;

    public static void main(String[] args) {
        Astar astar = new Astar();
        astar.fillGrid();
        astar.addStartTileToClosedList();
        astar.currentTile = astar.closedList.get(astar.closedList.size() - 1);
        astar.calculatePath();

    }

    public void fillGrid() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                grid[i][j] = new Tile(i, j);

            }
        }
    }

    public void addStartTileToClosedList() {

        closedList.add(grid[xStartposition][yStartposition]);
        grid[xStartposition][yStartposition].setBlocked(true);
    }

    public void addTilesToOpenList() {

        if (!(currentTile.getCurrentYPosition() - 1 < 0 || grid[currentTile.getCurrentXPosition()][currentTile.getCurrentYPosition() - 1].isBlocked() == true)) {
            grid[currentTile.getCurrentYPosition() - 1][currentTile.getCurrentXPosition()].setPreviousYposition(currentTile.getCurrentYPosition());
            grid[currentTile.getCurrentYPosition() - 1][currentTile.getCurrentXPosition()].setPreviousXposition(currentTile.getCurrentXPosition());
            grid[currentTile.getCurrentYPosition() + 1][currentTile.getCurrentXPosition()].setBlocked(true);
            if(!(openList.contains(grid[currentTile.getCurrentYPosition() - 1][currentTile.getCurrentXPosition()])|| closedList.contains(grid[currentTile.getCurrentYPosition()-1][currentTile.getCurrentXPosition()])))
            openList.add(grid[currentTile.getCurrentYPosition() - 1][currentTile.getCurrentXPosition()]);

        }
        if (!(currentTile.getCurrentYPosition() + 1 > 10 || grid[currentTile.getCurrentXPosition()][currentTile.getCurrentYPosition() + 1].isBlocked() == true)) {
            grid[currentTile.getCurrentYPosition() + 1][currentTile.getCurrentXPosition()].setPreviousYposition(currentTile.getCurrentYPosition());
            grid[currentTile.getCurrentYPosition() + 1][currentTile.getCurrentXPosition()].setPreviousXposition(currentTile.getCurrentXPosition());
            grid[currentTile.getCurrentYPosition() + 1][currentTile.getCurrentXPosition()].setBlocked(true);
            if(!(openList.contains(grid[currentTile.getCurrentYPosition() + 1][currentTile.getCurrentXPosition()])|| closedList.contains(grid[currentTile.getCurrentYPosition()+1][currentTile.getCurrentXPosition()])))
            openList.add(grid[currentTile.getCurrentYPosition() + 1][currentTile.getCurrentXPosition()]);


        }
        if (!(currentTile.getCurrentXPosition() - 1 < 0 || grid[currentTile.getCurrentXPosition() - 1][currentTile.getCurrentYPosition()].isBlocked() == true)) {
            grid[currentTile.getCurrentYPosition()][currentTile.getCurrentXPosition() - 1].setPreviousYposition(currentTile.getCurrentYPosition());
            grid[currentTile.getCurrentYPosition()][currentTile.getCurrentXPosition() - 1].setPreviousXposition(currentTile.getCurrentXPosition());
            grid[currentTile.getCurrentYPosition()][currentTile.getCurrentXPosition() - 1].setBlocked(true);
            if(!(openList.contains(grid[currentTile.getCurrentYPosition()][currentTile.getCurrentXPosition() - 1]) || closedList.contains(grid[currentTile.getCurrentYPosition()][currentTile.getCurrentXPosition() -1])))
                openList.add(grid[currentTile.getCurrentYPosition()][currentTile.getCurrentXPosition() - 1]);


        }
        if (!(currentTile.getCurrentXPosition() + 1 > 10 || grid[currentTile.getCurrentXPosition() + 1][currentTile.getCurrentYPosition()].isBlocked() == true)) {
            grid[currentTile.getCurrentYPosition()][currentTile.getCurrentXPosition() + 1].setBlocked(true);
            grid[currentTile.getCurrentYPosition()][currentTile.getCurrentXPosition() + 1].setPreviousYposition(currentTile.getCurrentYPosition());
            grid[currentTile.getCurrentYPosition()][currentTile.getCurrentXPosition() + 1].setPreviousXposition(currentTile.getCurrentXPosition());
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


    public void calculatePath() {
        while (!(currentTile.getCurrentXPosition() == xEndposition && currentTile.getCurrentYPosition() == yEndposition)) {
            currentTile = closedList.get(closedList.size() - 1);
            if (closedList.size() > 2)
                previousTile = closedList.get(closedList.size() - 2);

            addTilesToOpenList();
            addTilesToClosedList();
        }
    }


}
