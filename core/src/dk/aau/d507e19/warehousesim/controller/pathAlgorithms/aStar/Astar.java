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

import java.util.ArrayList;
import java.util.Collections;

public class Astar implements PathFinder {

    public AStarTile[][] grid;
    public int xEndPosition;
    public int yEndPosition;
    public int xStart;
    public int yStart;

    public ArrayList<GridCoordinate> finalPath = new ArrayList<>();

    public ArrayList<AStarTile> openList = new ArrayList<>();
    public ArrayList<AStarTile> closedList = new ArrayList<>();
    public ArrayList<GridCoordinate> isReservedList = new ArrayList<>();

    public AStarTile currentTile;
    public AStarTile bestTile;
    public final ReservationManager reservationManager;
    public Server server;
    public Robot robot;

    public Astar(Server server, Robot robot) {
        this.grid = fillGrid(server.getGridWidth(), server.getGridHeight());
        this.reservationManager = server.getReservationManager();
        this.robot = robot;
        this.server = server;
    }

    public AStarTile[][] fillGrid(int gridWidth, int gridHeight) {
        AStarTile[][] grid = new AStarTile[gridWidth][gridHeight];
        // Fills grid with tiles matching the coordinates
        for (int i = 0; i < gridWidth; i++) {
            for (int j = 0; j < gridHeight; j++) {
                grid[i][j] = new AStarTile(i, j);
            }
        }
        return grid;
    }

    public void addStartTileToClosedList() {
        // Adds startTile to closedList
        closedList.add(grid[xStart][yStart]);

        // Blocks startTile so that it cannot be used anymore
        grid[xStart][yStart].setBlocked(true);

        // Sets currentTile to the top tile in closedList (startTile)
        currentTile = closedList.get(closedList.size() - 1);

    }

    public void checkNeighborValidity() {

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

    public void addNeighborTileToOpenList(GridCoordinate gcNeighbor) {

        // Make AstarTile copy from neighbor.
        AStarTile aStarNeighbor = grid[gcNeighbor.getX()][gcNeighbor.getY()].copy();

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

            // Makes copy original
            grid[aStarNeighbor.getCurrentXPosition()][aStarNeighbor.getCurrentYPosition()] = aStarNeighbor;

            // Add neighbor tile to openList
            openList.add(aStarNeighbor);
        }

    }

    public void addTilesToClosedList() {
        // Blocks the current tile in the grid before it is moved to closedList.
        grid[openList.get(0).getCurrentXPosition()][openList.get(0).getCurrentYPosition()].setBlocked(true);

        // Adds tile to closedList
        closedList.add(openList.get(0));

        // Removes the tile from the openList.
        openList.remove(0);
    }

    public void createPathListFromClosedList() {

        AStarTile currentTileClosedList = currentTile.copy();

        // If the list is bigger than one object, then go through the whole list
        if (closedList.size() > 1) {
            AStarTile prevTempTile = closedList.get(closedList.size() - 2);
            finalPath.add(new GridCoordinate(currentTileClosedList.getCurrentXPosition(), currentTileClosedList.getCurrentYPosition()));

            // Find the object which matches the previous tiles coordinates
            for (int i = closedList.size() - 2; i > 0; i--) {
                if (currentTileClosedList.getPreviousXPosition() == prevTempTile.getCurrentXPosition() && currentTileClosedList.getGetPreviousYPosition() == prevTempTile.getCurrentYPosition()) {
                    finalPath.add(new GridCoordinate(prevTempTile.getCurrentXPosition(), prevTempTile.getCurrentYPosition()));
                    currentTileClosedList = closedList.get(i);
                }
                prevTempTile = closedList.get(i - 1);
            }
        }

        // Add the first object to list
        finalPath.add(new GridCoordinate(closedList.get(0).getCurrentXPosition(), closedList.get(0).getCurrentYPosition()));

        // Reverses list
        Collections.reverse(finalPath);
    }

    public void calculatePath() {
        // Adds the starting tile to closed list.
        addStartTileToClosedList();

        calculatePath2();

        createPathListFromClosedList();

        if (isReserved()) {

            // Clear all lists except for isReservedList
            clear();

            // Calculate a new path with the reserved tiles in mind
            calculatePath();
        }
    }

    public void calculatePath2() {
        // While is true if the currentTile does not have the same x coordinate and the same y coordinate as the end Tile.
        while (!(currentTile.getCurrentXPosition() == xEndPosition && currentTile.getCurrentYPosition() == yEndPosition)) {
            // Add the valid tiles to openList
            checkNeighborValidity();


            // Small exceptions too see if it is stuck. Then it just returns
            if (openList.size() < 1) {
                return;
            }

            // Sorts openList in ascending order
            openList.sort(new OpenListSorter());

            // Add the lowest cost tile to closedList
            addTilesToClosedList();

            // CurrentTile is now the top tile in closedList
            currentTile = closedList.get(closedList.size() - 1);
        }
    }

    public void clear() {
        for (AStarTile tile : closedList) {
            tile.setBlocked(false);
        }
        openList.clear();
        closedList.clear();
        finalPath.clear();
        currentTile = null;
    }

    public boolean isReserved() {

        boolean i = false;

        // Makes the tempPath to steps
        Path path = new Path(Step.fromGridCoordinates(finalPath));

        // Returns false if finalPath is less than two (Standing on its own tile)
        if (finalPath.size() < 2) {
            return false;
        }

        // Calculates the path into a list of reservations.
        ArrayList<Reservation> listOfReservations = MovementPredictor.calculateReservations(robot, path, server.getTimeInTicks(), 0);

        Reservation lastReservation = listOfReservations.get(listOfReservations.size()-1);

        // Checks if the last reservation is reserved indefinitely, and if it can reserve indefinitely
        if (reservationManager.hasConflictingReservations(lastReservation) ||
                !reservationManager.canReserve(lastReservation.getGridCoordinate(), TimeFrame.indefiniteTimeFrameFrom(lastReservation.getTimeFrame().getStart()))) {

            bestTile.calculateH(xEndPosition, yEndPosition);
            bestTile.calculateG(currentTile.getG());
            bestTile.calculateF();

            // Make new end positions and calculate again
            xEndPosition = listOfReservations.get(listOfReservations.size()-2).getGridCoordinate().getX();
            yEndPosition = listOfReservations.get(listOfReservations.size()-2).getGridCoordinate().getY();

            AStarTile newEndTile = grid[xEndPosition][yEndPosition];

            if (bestTile.getF() < newEndTile.getF()){
                xEndPosition = bestTile.getCurrentXPosition();
                yEndPosition = bestTile.getCurrentYPosition();
            }

            i = true;
        }

        // Goes through every reservation, except for the first, that is always reserved (where the robot is standing)
        for (int j = 1; j < listOfReservations.size(); j++) {
            if (reservationManager.isReserved(listOfReservations.get(j).getGridCoordinate(), listOfReservations.get(j).getTimeFrame())) {

                // If the tile is already reserved at this time, then the tile is added to the isReservedList.
                isReservedList.add(listOfReservations.get(j).getGridCoordinate());

                // i is now true, so that it can calculate a new path.
                i = true;
            }
        }

        return i;
    }

    @Override
    public Path calculatePath(GridCoordinate start, GridCoordinate destination)  {

        if(start.equals(destination))
            return Path.oneStepPath(new Step(start));

        // Clears all lists and objects so that it is clean next time it calculates a path.
        isReservedList.clear();
        clear();

        xEndPosition = destination.getX();
        yEndPosition = destination.getY();

        xStart = start.getX();
        yStart = start.getY();

        bestTile = grid[xStart][yStart];

        // Calculates the optimal A* path
        calculatePath();

        return new Path(Step.fromGridCoordinates(finalPath));
    }

    @Override
    public boolean accountsForReservations() {
        return true;
    }
}