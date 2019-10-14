package dk.aau.d507e19.warehousesim.controller.pathAlgorithms;
import dk.aau.d507e19.warehousesim.SimulationApp;
import dk.aau.d507e19.warehousesim.WarehouseSpecs;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.path.Path;

import java.lang.Math;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RRTPlanner implements PathFinder{

    public Node<GridCoordinate> shortestLengthNode;
    private Node<GridCoordinate> root, destinationNode;
    private GridCoordinate dest;
    public HashMap<GridCoordinate,Node<GridCoordinate>> allNodesMap = new HashMap<>();
    private boolean foundPath;

    public ArrayList<GridCoordinate> generateRRTPath(GridCoordinate start, GridCoordinate destination) {
        dest = destination;
        root = new Node<GridCoordinate>(start, null);
        //add root node to list of nodes
        allNodesMap.put(root.getData(),root);
        //Run until a route is found
        while (!foundPath) {
            //  root.printTree(root);
            // System.out.println("END");
            growRRT(root);
        }
        //Find parents and make list of coords
        return makePath(destinationNode);
    }

    private ArrayList<GridCoordinate> makePath(Node<GridCoordinate> destNode){
        ArrayList<GridCoordinate> path = new ArrayList<>();
        if(destNode.getParent() == null){
            path.add(new GridCoordinate(destNode.getData().getX(),destNode.getData().getY()));
            return path;
        }
        path = makePath(destNode.getParent());
        path.add(new GridCoordinate(destNode.getData().getX(),destNode.getData().getY()));
        return path;
    }

    private void growRRT(Node<GridCoordinate> tree) {
        //Generate a new random location using seeded random
        GridCoordinate randPos = generateRandomPos();
        shortestLengthNode = tree;
        Node<GridCoordinate> nearest = findNearestNeighbour(tree, randPos);
        Node<GridCoordinate> newNode = generateNewNode(nearest, randPos);
        nearest.addChild(newNode);
        allNodesMap.put(newNode.getData(),newNode);
        if(newNode.getData().equals(dest)){
            foundPath = true;
            destinationNode = newNode;
        }
    }

    public Node<GridCoordinate> findNearestNeighbour(Node<GridCoordinate> tree, GridCoordinate randPos) {

        for(Node<GridCoordinate> n : findNodesInSquare(randPos)){
            double newDistance = getDistanceBetweenPoints(n.getData(),randPos);

            if (newDistance < getDistanceBetweenPoints(shortestLengthNode.getData(),randPos)){
                shortestLengthNode = n;
            }
        }
         /*
        for (Node<GridCoordinate> n : tree.getChildren()) {
            double newDistance = getDistanceBetweenPoints(n.getData(), randPos);

            if (newDistance < getDistanceBetweenPoints(shortestLengthNode.getData(), randPos)) {
                shortestLengthNode = n;
            }
            findNearestNeighbour(n, randPos);
        } */
        return shortestLengthNode;
    }

    private List<Node<GridCoordinate>>findNodesInSquare(GridCoordinate randPos){
        List<Node<GridCoordinate>> listOfNodes =  new ArrayList<>();
        //GridCoordinate relativePos = new GridCoordinate(0,0);
        GridCoordinate topLeft = new GridCoordinate(randPos.getX(),randPos.getY());
        GridCoordinate bottomRight = new GridCoordinate(randPos.getX(),randPos.getY());
        while(listOfNodes.isEmpty()){
            //check if new corners are out of grid bounds
            // Create new corners (probably not necessary)
            topLeft = updateTopLeft(topLeft);
            bottomRight = updateBottomRight(bottomRight);
            //check for nodes - if any nodes are found then add to listOfNodes
            for(int i = topLeft.getX(); i <= bottomRight.getX();i++){
                if(i!= topLeft.getX() && i!= bottomRight.getX()){
                    if (allNodesMap.containsKey(new GridCoordinate(i,topLeft.getY()))){
                        listOfNodes.add(allNodesMap.get(new GridCoordinate(i,topLeft.getY())));
                    }
                    if(allNodesMap.containsKey(new GridCoordinate(i,bottomRight.getY()))){
                        listOfNodes.add(allNodesMap.get(new GridCoordinate(i,bottomRight.getY())));
                    }
                    continue;
                }
                for(int j = topLeft.getY(); j <= bottomRight.getY();j++){
                    if(allNodesMap.containsKey(new GridCoordinate(i,j))){
                        listOfNodes.add(allNodesMap.get(new GridCoordinate(i,j))); 
                    }
                }
            }
        }
        return listOfNodes;
    }

    private GridCoordinate updateTopLeft(GridCoordinate old){
        GridCoordinate topLeft = new GridCoordinate(old.getX(), old.getY());
        if(topLeft.getX()-1 >= 0){
            topLeft.setX(topLeft.getX()-1);
        }
        if(topLeft.getY()-1 >= 0){
            topLeft.setY(topLeft.getY()-1);
        }
        return topLeft;
    }

    private GridCoordinate updateBottomRight(GridCoordinate old){
        GridCoordinate bottomRight = new GridCoordinate(old.getX(),old.getY());
        if(bottomRight.getX() + 1 <= WarehouseSpecs.wareHouseWidth){
            bottomRight.setX(bottomRight.getX()+1);
        }
        if(bottomRight.getY() + 1 <= WarehouseSpecs.wareHouseHeight){
            bottomRight.setY(bottomRight.getY()+1);
        }
        return bottomRight;
    }

    private Node<GridCoordinate> generateNewNode(Node<GridCoordinate> nearest, GridCoordinate randPos) {
        GridCoordinate originalPos = nearest.getData();
        GridCoordinate pos = nearest.getData();
        //right
        pos = getDistanceBetweenPoints(new GridCoordinate(pos.getX() + 1, pos.getY()), randPos) < getDistanceBetweenPoints(pos, randPos) ? new GridCoordinate(originalPos.getX() + 1, originalPos.getY()) : pos;
        //left
        pos = getDistanceBetweenPoints(new GridCoordinate(pos.getX() - 1, pos.getY()), randPos) < getDistanceBetweenPoints(pos, randPos) ? new GridCoordinate(originalPos.getX() -1, originalPos.getY()) : pos;
        //up
        pos = getDistanceBetweenPoints(new GridCoordinate(pos.getX(), pos.getY() + 1), randPos) < getDistanceBetweenPoints(pos, randPos) ? new GridCoordinate(originalPos.getX(), originalPos.getY() +1) : pos;
        //down
        pos = getDistanceBetweenPoints(new GridCoordinate(pos.getX(), pos.getY() - 1), randPos) < getDistanceBetweenPoints(pos, randPos) ? new GridCoordinate(originalPos.getX(), originalPos.getY() -1 ) : pos;

        //System.out.println("NEW: "+ pos.toString()+"\nNEAR: " + originalPos.toString() + "\nRAND: " + randPos.toString()+"\n");
        return new Node<>(pos, null);
    }

    private GridCoordinate generateRandomPos() {
        //TODO possible infinite loop if there is a node on every tile currently prevented since generateRTT func returns as soon as dest node is created
        GridCoordinate randPos;
        do {
            randPos = new GridCoordinate(
                    SimulationApp.random.nextInt(WarehouseSpecs.wareHouseWidth),
                    SimulationApp.random.nextInt(WarehouseSpecs.wareHouseHeight));
        }while(doesNodeExist(randPos));

        return randPos;
    }

    public double getDistanceBetweenPoints(GridCoordinate pos1, GridCoordinate pos2) {
        return Math.sqrt(Math.pow(pos2.getX() - pos1.getX(), 2) + Math.pow(pos2.getY() - pos1.getY(), 2));
    }

    private boolean doesNodeExist(GridCoordinate newPos) {
        return allNodesMap.containsKey(newPos);
        //return root.containsNodeWithData(root,newPos);
    }

    @Override
    public Path calculatePath(GridCoordinate start, GridCoordinate destination) {

        return new Path(generateRRTPath(start,destination));
    }
}
