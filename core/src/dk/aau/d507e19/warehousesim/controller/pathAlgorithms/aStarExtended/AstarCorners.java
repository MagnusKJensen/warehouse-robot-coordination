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

public class AstarCorners extends Astar {

    public AstarCorners(Server server, Robot robot) {
        super(server, robot);
    }

    @Override
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
            addCornersToG(aStarNeighbor);

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

    public void addCornersToG(AStarTile neighbor){

        GridCoordinate gcNeighbor = new GridCoordinate(neighbor.getCurrentXPosition(), neighbor.getCurrentYPosition());

        // Make new temp path
        createPathListFromClosedList();
        finalPath.add(gcNeighbor);

        // Calculate reservations
        Path path = new Path(Step.fromGridCoordinates(finalPath));

        // Get corners
        int corners = path.getStrippedPath().size();

        // Adds corners to neighbors G
        neighbor.setG(neighbor.getG() + corners);

        finalPath.clear();
    }
}
