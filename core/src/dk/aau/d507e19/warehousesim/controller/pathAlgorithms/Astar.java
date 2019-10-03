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
        for (int i = 0; i < gridLength; i++) {
            for (int j = 0; j < gridLength; j++) {
                grid[i][j] = new Tile(i, j);
            }
        }
    }

    public void addStartTileToClosedList() {

        closedList.add(grid[xStartposition][yStartposition]);
        grid[xStartposition][yStartposition].setBlocked(true);
        closedList.get(0).setBlocked(true);
    }

    public void checkNeighborValidity() {
        if (!(currentTile.isBlocked())) {
            if (currentTile.getCurrentYPosition() - 1 >= 0) {
                addNeighborTileToOpenList(grid[currentTile.getCurrentXPosition()][currentTile.getCurrentYPosition() - 1]);
            } else if (currentTile.getCurrentYPosition() + 1 < gridLength) {
                addNeighborTileToOpenList(grid[currentTile.getCurrentXPosition()][currentTile.getCurrentYPosition() + 1]);
            } else if (currentTile.getCurrentXPosition() - 1 >= 0) {
                addNeighborTileToOpenList(grid[currentTile.getCurrentXPosition() - 1][currentTile.getCurrentYPosition()]);
            } else if (currentTile.getCurrentXPosition() + 1 < gridLength) {
                addNeighborTileToOpenList(grid[currentTile.getCurrentXPosition() + 1][currentTile.getCurrentYPosition()]);
            }
        }
    }

    public void addNeighborTileToOpenList(Tile neighborTile) {

        neighborTile.setPreviousXposition(currentTile.getCurrentXPosition());
        neighborTile.setPreviousYposition(currentTile.getCurrentYPosition());

        neighborTile.calculateH(xEndposition, yEndposition);

        neighborTile.calculateG(currentTile.getG());
        neighborTile.calculateF();
        for (Tile tile : openList) {
            if (neighborTile.getCurrentXPosition() == tile.getCurrentXPosition() && neighborTile.getCurrentYPosition() == tile.getCurrentYPosition()) {
                if (neighborTile.getF() <= tile.getF()) {
                    openList.remove(tile);

                } else return;
            }

        }
        openList.add(neighborTile);
    }

    public void addTilesToClosedList() {

        grid[openList.get(0).getCurrentXPosition()][openList.get(0).getCurrentYPosition()].setBlocked(true);
        closedList.add(openList.get(0));
        openList.remove(0);
    }


    public void calculatePath() {
        while (!(currentTile.getCurrentXPosition() == xEndposition && currentTile.getCurrentYPosition() == yEndposition)) {
            currentTile = closedList.get(closedList.size() - 1);
            if (closedList.size() > 1)
                previousTile = closedList.get(closedList.size() - 2);
            System.out.println(currentTile.getCurrentXPosition() + " " + currentTile.getCurrentYPosition());
            checkNeighborValidity();
            openList.sort(new OpenListSorter());
            addTilesToClosedList();

        }
    }
}
