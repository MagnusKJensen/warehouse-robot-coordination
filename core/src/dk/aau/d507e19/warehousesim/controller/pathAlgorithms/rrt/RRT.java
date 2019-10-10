package dk.aau.d507e19.warehousesim.controller.pathAlgorithms.rrt;

import dk.aau.d507e19.warehousesim.WarehouseSpecs;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.Node;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;

import java.util.ArrayList;

public class RRT extends RRTBase {


    public ArrayList<GridCoordinate> generateRRTPath(GridCoordinate start, GridCoordinate destination) {
        boolean foundPath = false;
        dest = destination;
        root = new Node<GridCoordinate>(start, null);
        //add root node to list of nodes
        allNodesMap.put(root.getData(),root);
        //Run until a route is found
        while (!foundPath) {
            //Grow tree by 10% of combined gridsize (could be an issue if grid is very large)
            growRRT(root, (int) ((WarehouseSpecs.wareHouseHeight*WarehouseSpecs.wareHouseWidth)*0.1));
            foundPath = doesNodeExist(destination);
        }
        //Find parents and make list of coords
        return makePath(destinationNode);
    }


}
