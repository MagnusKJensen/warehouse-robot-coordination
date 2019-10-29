package dk.aau.d507e19.warehousesim.controller.pathAlgorithms.aStar;


import dk.aau.d507e19.warehousesim.controller.path.Step;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.PathFinder;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.path.Path;
import dk.aau.d507e19.warehousesim.controller.robot.MovementPredictor;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.server.Reservation;
import dk.aau.d507e19.warehousesim.controller.server.ReservationManager;
import dk.aau.d507e19.warehousesim.controller.server.Server;
import dk.aau.d507e19.warehousesim.controller.server.TimeFrame;
import dk.aau.d507e19.warehousesim.exception.DestinationReservedIndefinitelyException;
import dk.aau.d507e19.warehousesim.exception.NoPathFoundException;
import dk.aau.d507e19.warehousesim.exception.NoValidPathException;
import dk.aau.d507e19.warehousesim.exception.pathExceptions.BlockedEndDestinationException;
import dk.aau.d507e19.warehousesim.exception.pathExceptions.NoValidNeighborException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

public class Astar implements PathFinder {

    private AStarTile[][] grid;
    private int xEndPosition;
    private int yEndPosition;
    private int xStart;
    private int yStart;

    private ArrayList<GridCoordinate> finalPath = new ArrayList<>();

    private ArrayList<AStarTile> openList = new ArrayList<>();
    private ArrayList<AStarTile> closedList = new ArrayList<>();
    private ArrayList<GridCoordinate> isReservedList = new ArrayList<>();

    private AStarTile currentTile;
    private final ReservationManager reservationManager;
    private Server server;
    private Robot robot;

    public Astar(Server server, Robot robot) {
        this.grid = fillGrid(server.getGridWidth(), server.getGridHeight());
        this.reservationManager = server.getReservationManager();
        this.robot = robot;
        this.server = server;
    }

    public AStarTile[][] getGrid() {
        return grid;
    }

    private AStarTile[][] fillGrid(int gridLength, int gridHeight) {
        AStarTile[][] grid = new AStarTile[gridLength][gridHeight];
        // Fills grid with tiles matching the coordinates
        for (int i = 0; i < gridLength; i++) {
            for (int j = 0; j < gridHeight; j++) {
                grid[i][j] = new AStarTile(i, j);
            }
        }
        return grid;
    }

    private void addStartTileToClosedList(int xStartposition, int yStartposition) {

        // Adds startTile to closedList
        closedList.add(grid[xStartposition][yStartposition]);

        // Blocks startTile so that it cannot be used anymore
        grid[xStartposition][yStartposition].setBlocked(true);

        // Sets currentTile to the top tile in closedList (startTile)
        currentTile = closedList.get(closedList.size() - 1);

    }

    private void checkNeighborValidity() {

        //Checks every potential neighbor to currentTile the same way.
        GridCoordinate aboveNeighbor = new GridCoordinate(currentTile.getCurrentXPosition(), currentTile.getCurrentYPosition() + 1);
        GridCoordinate downstairsNeighbor = new GridCoordinate(currentTile.getCurrentXPosition(), currentTile.getCurrentYPosition() - 1);
        GridCoordinate leftNeighbor = new GridCoordinate(currentTile.getCurrentXPosition() - 1, currentTile.getCurrentYPosition());
        GridCoordinate rightNeighbor = new GridCoordinate(currentTile.getCurrentXPosition() + 1, currentTile.getCurrentYPosition());

        // Checks if neighbor is valid with a valid coordinate
        if (downstairsNeighbor.getY() >= 0) {

            // If the current tile coordinates are valid, and the neighbor tile is not blocked then proceed.
            if (currentTile.getCurrentYPosition() - 1 >= 0 && !(grid[downstairsNeighbor.getX()][downstairsNeighbor.getY()].isBlocked())) {

                // Add neighbor tile to openList.
                addNeighborTileToOpenList(downstairsNeighbor);
            }
        }

        // Checks if neighbor is valid with a valid coordinate
        if (aboveNeighbor.getY() <= server.getGridHeight()) {

            // If the current tile coordinates are valid, and the neighbor tile is not blocked then proceed.
            if (currentTile.getCurrentYPosition() + 1 < grid.length && !(grid[aboveNeighbor.getX()][aboveNeighbor.getY()].isBlocked())) {

                // Add neighbor tile to openList.
                addNeighborTileToOpenList(aboveNeighbor);
            }
        }

        // Checks if neighbor is valid with a valid coordinate
        if (leftNeighbor.getX() >= 0) {

            // If the current tile coordinates are valid, and the neighbor tile is not blocked then proceed.
            if (currentTile.getCurrentXPosition() - 1 >= 0 && !(grid[leftNeighbor.getX()][leftNeighbor.getY()].isBlocked())) {

                // Add neighbor tile to openList.
                addNeighborTileToOpenList(leftNeighbor);
            }
        }

        // Checks if neighbor is valid with a valid coordinate
        if (rightNeighbor.getX() <= server.getGridWidth()) {

            // If the current tile coordinates are valid, and the neighbor tile is not blocked then proceed.
            if (currentTile.getCurrentXPosition() + 1 < grid.length && !(grid[rightNeighbor.getX()][rightNeighbor.getY()].isBlocked())) {

                // Add neighbor tile to openList.
                addNeighborTileToOpenList(rightNeighbor);
            }
        }
    }

    private TimeFrame getTimeFrameFromLastReservation(ArrayList<GridCoordinate> tempPath) {

        ArrayList<Reservation> listOfReservations;

        // Makes the tempPath to steps
        Path path = new Path(Step.fromGridCoordinates(tempPath));

        // Calculates the path into a list of reservations.
        listOfReservations = MovementPredictor.calculateReservations(robot, path, server.getTimeInTicks(), 0);

        // Returns the timeFrame of the last reservations.
        return listOfReservations.get(listOfReservations.size() - 1).getTimeFrame();
    }

    private void addNeighborTileToOpenList(GridCoordinate gcNeighbor) {

        ArrayList<GridCoordinate> temporaryPath;

        // Make AstarTile from neighbor.
        AStarTile aStarNeighbor = grid[gcNeighbor.getX()][gcNeighbor.getY()];

        // Creates a temp path to the neighbor tile.
        temporaryPath = createTemporaryPath(currentTile, gcNeighbor);

        // If the neighbor tile is not reserved in the right timeFrame, then proceed.
        if (!(isReservedList.contains(gcNeighbor))) {

            // Makes new dummy tile
            AStarTile tileToDelete = null;

            // Sets the previous coordinates in neighbor tile
            aStarNeighbor.setPreviousXPosition(currentTile.getCurrentXPosition());
            aStarNeighbor.setPreviousYPosition(currentTile.getCurrentYPosition());

            // Calculates neighborTiles H, G and F
            aStarNeighbor.calculateH(xEndPosition, yEndPosition);
            aStarNeighbor.calculateG(currentTile.getG());
            aStarNeighbor.calculateF();

            // Checks if neighborTile is already in openList.
            for (AStarTile tile : openList) {

                // If a tile with the same coordinates is already in openList, then check which has the lowest F value.
                if (aStarNeighbor.getCurrentXPosition() == tile.getCurrentXPosition() && aStarNeighbor.getCurrentYPosition() == tile.getCurrentYPosition()) {

                    // If the existing tile in openList has the highest F, then it is copied into tileToDelete
                    if (aStarNeighbor.getF() <= tile.getF()) {
                        tileToDelete = tile;

                        // If the neighborTile has the highest F, then return and dont add to openList.
                    } else return;
                }
            }

            // If there is a tile to delete, then delete
            if (tileToDelete != null)
                openList.remove(tileToDelete);

            // Add neighbor tile to openList
            openList.add(aStarNeighbor);
        }

    }

    private void addTilesToClosedList() {
        // Blocks the current tile in the grid before it is moved to closedList.
        grid[openList.get(0).getCurrentXPosition()][openList.get(0).getCurrentYPosition()].setBlocked(true);

        // Adds tile to closedList
        closedList.add(openList.get(0));

        // Removes the tile from the openList.
        openList.remove(0);
    }

    private ArrayList<GridCoordinate> createTemporaryPath(AStarTile currentTile, GridCoordinate neighborTile) {

        ArrayList<GridCoordinate> temp = new ArrayList<>();

        // Creates temp path of gridCoordinates
        createPathListFromClosedList(currentTile, temp);

        // Adds the last gridCoordinate to list.
        temp.add(neighborTile);

        return temp;
    }

    private void createPathListFromClosedList(AStarTile currentTile, ArrayList<GridCoordinate> temp) {

        // If the list is bigger than one object, then go through the whole list
        if (closedList.size() > 1) {
            AStarTile prevTempTile = closedList.get(closedList.size() - 2);
            temp.add(new GridCoordinate(currentTile.getCurrentXPosition(), currentTile.getCurrentYPosition()));

            // Find the object which matches the previous tiles coordinates
            for (int i = closedList.size() - 2; i > 0; i--) {
                if (currentTile.getPreviousXPosition() == prevTempTile.getCurrentXPosition() && currentTile.getGetPreviousYPosition() == prevTempTile.getCurrentYPosition()) {
                    temp.add(new GridCoordinate(prevTempTile.getCurrentXPosition(), prevTempTile.getCurrentYPosition()));
                    currentTile = closedList.get(i);
                }
                prevTempTile = closedList.get(i - 1);
            }
        }

        // Add the first object to list
        temp.add(new GridCoordinate(closedList.get(0).getCurrentXPosition(), closedList.get(0).getCurrentYPosition()));

        // Reverses list
        Collections.reverse(temp);
    }

    private void calculatePath() throws NoPathFoundException {
        // Adds the starting tile to closed list.
        addStartTileToClosedList(xStart, yStart);

        // While is true if the currentTile does not have the same x coordinate and the same y coordinate as the end Tile.
        while (!(currentTile.getCurrentXPosition() == xEndPosition && currentTile.getCurrentYPosition() == yEndPosition)) {
            // Add the valid tiles to openList
            checkNeighborValidity();

            // Small exceptions too see if it is stuck or if end destination is blocked.
            if (openList.size() < 1) {
                if (closedList.size() > 1) {
                  //  throw new BlockedEndDestinationException(robot, closedList.size());
                    GridCoordinate startGC = new GridCoordinate(xStart,yStart);
                    GridCoordinate endGC = new GridCoordinate(xEndPosition,yEndPosition);
                    throw new DestinationReservedIndefinitelyException(startGC,endGC);
                }
                throw new NoValidPathException(new GridCoordinate(xStart,yStart), new GridCoordinate(xEndPosition,yEndPosition),"No valid Neighbor could be found");
               // throw new NoValidNeighborException(robot);
            }

            // Sorts openList in ascending order
            openList.sort(new OpenListSorter());

            // Add the lowest cost tile to closedList
            addTilesToClosedList();

            // CurrentTile is now the top tile in closedList
            currentTile = closedList.get(closedList.size() - 1);
        }

        createPathListFromClosedList(currentTile, finalPath);

        if (isReserved()) {
            clear();
            calculatePath();
        }
    }

    private void clear() {
        for (AStarTile tile : closedList) {
            tile.setBlocked(false);
        }
        openList.clear();
        closedList.clear();
        finalPath.clear();
        currentTile = null;
    }

    public boolean isReserved() throws NoPathFoundException {

        boolean i = false;

        // Makes the tempPath to steps
        Path path = new Path(Step.fromGridCoordinates(finalPath));

        if (finalPath.size() < 2) {
            return i;
        }

        // Calculates the path into a list of reservations.
        ArrayList<Reservation> listOfReservations = MovementPredictor.calculateReservations(robot, path, server.getTimeInTicks(), 0);

        for (int j = 1; j < listOfReservations.size(); j++) {
            if (reservationManager.isReserved(listOfReservations.get(j).getGridCoordinate(), listOfReservations.get(j).getTimeFrame())) {
                isReservedList.add(listOfReservations.get(j).getGridCoordinate());
                i = true;
            } else if (reservationManager.hasConflictingReservations(listOfReservations.get(listOfReservations.size() - 1))) {
                throw new NoPathFoundException(listOfReservations.get(0).getGridCoordinate(), listOfReservations.get(listOfReservations.size() - 1).getGridCoordinate());
            }
        }
        return i;
    }

    @Override
    public Path calculatePath(GridCoordinate start, GridCoordinate destination) throws NoPathFoundException {
        // Clears all lists and objects so that it is clean next time it calculates a path.
        isReservedList.clear();
        clear();

        xEndPosition = destination.getX();
        yEndPosition = destination.getY();

        xStart = start.getX();
        yStart = start.getY();

        // Calculates the optimal A* path
        calculatePath();
        return new Path(Step.fromGridCoordinates(finalPath));
    }
}