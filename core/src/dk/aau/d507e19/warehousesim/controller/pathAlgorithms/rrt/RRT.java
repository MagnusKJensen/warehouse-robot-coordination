package dk.aau.d507e19.warehousesim.controller.pathAlgorithms.rrt;

import dk.aau.d507e19.warehousesim.WarehouseSpecs;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;

import java.util.ArrayList;

public class RRT extends RRTBase {

    public RRT(Robot robot) {
        super(robot);
    }

    public ArrayList<GridCoordinate> generateRRTPathFromEmpty(GridCoordinate start, GridCoordinate destination) {
        boolean foundPath = false;
        dest = destination;
        root = new Node<GridCoordinate>(start, null, false);
        //add root node to list of nodes
        allNodesMap.put(root.getData(),root);
        //grow until we have a path
        //when function completes we know that we have a path
        growUntilPathFound(destination);
        destinationNode = allNodesMap.get(destination);
        return makePath(destinationNode);
    }
    public ArrayList<GridCoordinate> generateRRTPath(GridCoordinate start, GridCoordinate destination){
        //if tree is empty
        if(allNodesMap.isEmpty()){
            //always returns the first path it finds
            return generateRRTPathFromEmpty(start,destination);
        }
        //Set root to equal starting point
        root = allNodesMap.get(start);
        //only set root if it isnt already
        if(!(root.getParent()==null)){
            root.makeRoot();
        }
        //grow until we have a path
        //when function completes we know that we have a path
        growUntilPathFound(destination);
        destinationNode = allNodesMap.get(destination);
        return makePath(destinationNode);
    }
    private void growUntilPathFound(GridCoordinate destination){
        boolean foundPath = false;
        //Run until a route is found
        while (!foundPath) {
            //grow tree by one each time(maybe inefficient?)
            growRRT(root, 1);
            foundPath = doesNodeExist(destination);
        }
    }
    private void growKtimes(GridCoordinate destination, int k){
        for(int i = 0; i < k; i++){
            growRRT(root,k);
        }
    }
}
