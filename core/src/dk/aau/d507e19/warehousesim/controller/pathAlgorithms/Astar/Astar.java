package dk.aau.d507e19.warehousesim.controller.pathAlgorithms;


import dk.aau.d507e19.warehousesim.controller.path.Step;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.path.Path;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.server.ReservationManager;
import dk.aau.d507e19.warehousesim.controller.server.Server;

import java.util.ArrayList;
import java.util.Collections;

public class Astar implements PathFinder {

    private AStarTile[][] grid;
    private int xEndposition;
    private int yEndposition;
    int xStart;
    int yStart;
    ArrayList<GridCoordinate> finalPath = new ArrayList<>();
    ArrayList<AStarTile> openList = new ArrayList<>();
    ArrayList<AStarTile> closedList = new ArrayList<>();
    private AStarTile currentTile;
    private long simulatedTimeInSeconds;
    private int robotID;
    private float robotMaxSpeedPerBin;
    private final ReservationManager reservationManager;
    //private PathManager pathManager;

    public Astar(Server server, Robot robot) {
        this.robotID = robot.getRobotID();
        this.grid = fillGrid(server.getGridWidth(), server.getGridHeight());
        this.robotMaxSpeedPerBin = robot.getMaxSpeedBinsPerSecond();
        this.simulatedTimeInSeconds = server.getTimeInSeconds();
        this.reservationManager = server.getReservationManager();
    }

    public AStarTile[][] getGrid() {
        return grid;
    }

    public AStarTile[][] fillGrid(int gridLength, int gridHeight) {
        AStarTile[][] grid = new AStarTile[gridLength][gridHeight];
        // Fills grid with tiles matching the coordinates
        for (int i = 0; i < gridLength; i++) {
            for (int j = 0; j < gridHeight; j++) {
                grid[i][j] = new AStarTile(i, j);
            }
        }
        return grid;
    }

    public void addStartTileToClosedList(int xStartposition, int yStartposition) {

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
        if (currentTile.getCurrentYPosition() - 1 >= 0 && pathManager.isTileReserved(currentTile, simulatedTime,robotMaxSpeedPerBin)) {
            //  if (currentTile.getCurrentYPosition() - 1 >= 0 ) {
            // Adds Neighbor to openList if valid
            addNeighborTileToOpenList(grid[currentTile.getCurrentXPosition()][currentTile.getCurrentYPosition() - 1]);
        }
        if (currentTile.getCurrentYPosition() + 1 < grid.length && pathManager.isTileReserved(currentTile, simulatedTime,robotMaxSpeedPerBin)) {
            //  if (currentTile.getCurrentYPosition() + 1 < grid.length ) {
            addNeighborTileToOpenList(grid[currentTile.getCurrentXPosition()][currentTile.getCurrentYPosition() + 1]);
        }
        if (currentTile.getCurrentXPosition() - 1 >= 0 && pathManager.isTileReserved(currentTile, simulatedTime,robotMaxSpeedPerBin)) {
            //  if (currentTile.getCurrentXPosition() - 1 >= 0) {
            addNeighborTileToOpenList(grid[currentTile.getCurrentXPosition() - 1][currentTile.getCurrentYPosition()]);
        }
        if (currentTile.getCurrentXPosition() + 1 < grid.length && pathManager.isTileReserved(currentTile, simulatedTime,robotMaxSpeedPerBin)) {
            //  if (currentTile.getCurrentXPosition() + 1 < grid.length ) {
            addNeighborTileToOpenList(grid[currentTile.getCurrentXPosition() + 1][currentTile.getCurrentYPosition()]);
        }
    }

    public boolean isTileReserved(AStarTile currentTile) {
        // TODO: 14/10/2019 Use reservation manager instead
        ArrayList<Reservation>[][] gridOfResevations = pathManager.getGridOfResevations();
        for (Reservation res : gridOfResevations[currentTile.getCurrentXPosition()][currentTile.getCurrentYPosition()]) {
            if (Math.ceil(simulatedTime + robotMaxSpeedPerBin * currentTile.getG()) == Math.ceil(res.getTimeTileIsReserved())) {
                return false;
            }
        }
        return true;
        return true;
    }

    public void addNeighborTileToOpenList(AStarTile neighborTile) {

        // Makes new dummy tile
        AStarTile tileToDelete = null;

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
            for (AStarTile tile : openList) {
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
            if (tileToDelete != null)
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
    }

    public void addFinalPathToList() {
        AStarTile currTile = closedList.get(closedList.size() - 1);
        AStarTile prevTile = closedList.get(closedList.size() - 2);
        finalPath.add(new GridCoordinate(currTile.getCurrentXPosition(), currTile.getCurrentYPosition()));
        for (int i = closedList.size() - 2; i > 0; i--) {
            if (currTile.getPreviousXposition() == prevTile.getCurrentXPosition() && currTile.getGetPreviousYposition() == prevTile.getCurrentYPosition()) {
                finalPath.add(new GridCoordinate(prevTile.getCurrentXPosition(), prevTile.getCurrentYPosition()));
                currTile = closedList.get(i);
            }
            prevTile = closedList.get(i - 1);
        }
        finalPath.add(new GridCoordinate(closedList.get(0).getCurrentXPosition(), closedList.get(0).getCurrentYPosition()));
    }

    @Override
    public Path calculatePath(GridCoordinate start, GridCoordinate destination) {

        xEndposition = destination.getX();
        yEndposition = destination.getY();

        xStart = start.getX();
        yStart = start.getY();

        // Adds the starting tile to closed list.
        addStartTileToClosedList(start.getX(), start.getY());

        // Calculates the optimal A* path
        calculatePath();

        //adds final path to list
        addFinalPathToList();
        //Reverses final path so it is in correct order
        Collections.reverse(finalPath);

        // TODO: 14/10/2019 Use reservation manager instead
        //pathManager.addReservationToList(finalPath, simulatedTime, robotID, robotMaxSpeedPerBin);


        pathManager.addReservationToList(finalPath, simulatedTime, robotID, robotMaxSpeedPerBin);
        pathManager.addReservationToList(finalPath, simulatedTime, robotID, robotMaxSpeedPerBin,xEndposition,yEndposition);

        //  pathManager.printReservations();
        return new Path(Step.fromGridCoordinates(finalPath));
    }
}
