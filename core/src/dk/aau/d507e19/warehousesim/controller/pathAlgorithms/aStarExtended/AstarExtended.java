package dk.aau.d507e19.warehousesim.controller.pathAlgorithms.aStarExtended;

import dk.aau.d507e19.warehousesim.controller.path.Path;
import dk.aau.d507e19.warehousesim.controller.path.Step;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.aStar.AStarTile;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.aStar.Astar;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.server.Server;
import dk.aau.d507e19.warehousesim.exception.NoPathFoundException;

import java.util.ArrayList;


public class AstarExtended extends Astar {

    public AstarExtended(Server server, Robot robot) {
        super(server, robot);
    }

    @Override
    public void addNeighborTileToOpenList(GridCoordinate gcNeighbor) {

        // Make AstarTile from neighbor.
        AStarTile aStarNeighbor = grid[gcNeighbor.getX()][gcNeighbor.getY()];

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
            addCornersToG(gcNeighbor);

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

            // Adds corners to G so that the neighbor with the least corners gets picked.
            //addCornersToG(gcNeighbor);

            // Add neighbor tile to openList
            openList.add(aStarNeighbor);
        }

    }

    @Override
    public void addTilesToClosedList() {
        // Blocks the current tile in the grid before it is moved to closedList.
        grid[openList.get(0).getCurrentXPosition()][openList.get(0).getCurrentYPosition()].setBlocked(true);

        // Adds tile to closedList
        closedList.add(openList.get(0));

        // Removes the tile from the openList.
        openList.remove(0);
    }

    public void addCornersToG(GridCoordinate GCNeighbor){

        // Make new temp path
        ArrayList<GridCoordinate> temp = new ArrayList<>();
        createPathListFromClosedList();
        temp.add(GCNeighbor);

        // Calculate reservations
        Path path = new Path(Step.fromGridCoordinates(temp));

        // Get corners
        int corners = path.getStrippedPath().size();

        AStarTile neighbor = grid[GCNeighbor.getX()][GCNeighbor.getY()];

        // Adds corners to neighbors G
        neighbor.setG(neighbor.getG() + corners);
    }

    @Override
    public Path calculatePath(GridCoordinate start, GridCoordinate destination) throws NoPathFoundException {

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
