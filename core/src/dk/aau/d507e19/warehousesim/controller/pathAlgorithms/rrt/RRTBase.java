package dk.aau.d507e19.warehousesim.controller.pathAlgorithms.rrt;

import dk.aau.d507e19.warehousesim.SimulationApp;
import dk.aau.d507e19.warehousesim.WarehouseSpecs;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public abstract class RRTBase {
    Robot robot;

    public RRTBase(Robot robot) {
        this.robot = robot;
    }

    public Node<GridCoordinate> root, destinationNode,shortestLengthNode;
    //Free list of all free points in the grid. populateFreeList() intializes the array with grid coordinates.
    public ArrayList<GridCoordinate> freeNodeList = populateFreeList();
    //blockedNodeList TODO: initialize the blockedNodeList with assignBlockedNodeStatus()
    public ArrayList<GridCoordinate> blockedNodeList;

    GridCoordinate dest;
    public HashMap<GridCoordinate,Node<GridCoordinate>> allNodesMap = new HashMap<>();

    protected void growRRT(Node<GridCoordinate> tree, int n) {
        //n is number of iterations
        //Generate a new random location using seeded random
        for(int i =0; i < n; i++){
            GridCoordinate randPos = generateRandomPos();
            shortestLengthNode = tree;
            Node<GridCoordinate> nearest = findNearestNeighbour(tree, randPos);
            Node<GridCoordinate> newNode = generateNewNode(nearest, randPos);
            newNode.setParent(nearest);
            allNodesMap.put(newNode.getData(),newNode);
            //Assign blocked nodes from server
            //assignBlockedNodeStatus(server.getReservedNotes);
        }

    }

    private Node<GridCoordinate> generateNewNode(Node<GridCoordinate> nearest, GridCoordinate randPos) {
        GridCoordinate originalPos = nearest.getData();
        GridCoordinate pos = nearest.getData();
        Edge edge = new Edge(pos, randPos);

        //right
        pos = edge.getDistanceBetweenPoints(new GridCoordinate(pos.getX() + 1, pos.getY()), randPos) < edge.getDistanceBetweenPoints(pos, randPos) ? new GridCoordinate(originalPos.getX() + 1, originalPos.getY()) : pos;
        //left
        pos = edge.getDistanceBetweenPoints(new GridCoordinate(pos.getX() - 1, pos.getY()), randPos) < edge.getDistanceBetweenPoints(pos, randPos) ? new GridCoordinate(originalPos.getX() -1, originalPos.getY()) : pos;
        //up
        pos = edge.getDistanceBetweenPoints(new GridCoordinate(pos.getX(), pos.getY() + 1), randPos) < edge.getDistanceBetweenPoints(pos, randPos) ? new GridCoordinate(originalPos.getX(), originalPos.getY() +1) : pos;
        //down
        pos = edge.getDistanceBetweenPoints(new GridCoordinate(pos.getX(), pos.getY() - 1), randPos) < edge.getDistanceBetweenPoints(pos, randPos) ? new GridCoordinate(originalPos.getX(), originalPos.getY() -1 ) : pos;

        //System.out.println("NEW: "+ pos.toString()+"\nNEAR: " + originalPos.toString() + "\nRAND: " + randPos.toString()+"\n");
        updateFreeList(pos);
        return new Node<>(pos, null, false);
    }

    public Node<GridCoordinate> findNearestNeighbour(Node<GridCoordinate> tree, GridCoordinate randPos) {
        Edge shortestEdge = new Edge(tree.getData(),randPos);
        for(Node<GridCoordinate> n : findKNodesInSquare(randPos,allNodesMap.size())){
            Edge newEdge = new Edge(n.getData(),randPos);

            if (newEdge.getDistance() < shortestEdge.getDistance()){
                shortestEdge = newEdge;
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

    private List<Node<GridCoordinate>> findKNodesInSquare(GridCoordinate randPos, int k){
        List<Node<GridCoordinate>> listOfNodes =  new ArrayList<>(),foundNodes;
        //GridCoordinate relativePos = new GridCoordinate(0,0);
        GridCoordinate topLeft = new GridCoordinate(randPos.getX(),randPos.getY());
        GridCoordinate bottomRight = new GridCoordinate(randPos.getX(),randPos.getY());
        while(listOfNodes.size() < k){
            //check if new corners are out of grid bounds
            // Create new corners (probably not necessary)
            topLeft = updateTopLeft(topLeft);
            bottomRight = updateBottomRight(bottomRight);
            foundNodes = findNodes(topLeft,bottomRight);
            if(!foundNodes.isEmpty()) listOfNodes.addAll(foundNodes);
        }
        return listOfNodes;
    }

    public List<Node<GridCoordinate>> findNodesInRadius(GridCoordinate randPos, int k){
        List<Node<GridCoordinate>> listOfNodes =  new ArrayList<>();
        List<Node<GridCoordinate>> foundNodes;
        GridCoordinate topLeft = new GridCoordinate(randPos.getX(),randPos.getY());
        GridCoordinate bottomRight = new GridCoordinate(randPos.getX(),randPos.getY());
        int radiusChecked = 0;
        while(radiusChecked != k){
            topLeft = updateTopLeft(topLeft);
            bottomRight = updateBottomRight(bottomRight);
            foundNodes = findNodes(topLeft,bottomRight);
            if(!foundNodes.isEmpty()) listOfNodes.addAll(foundNodes);
            radiusChecked++;
        }
        return listOfNodes;
    }

    private List<Node<GridCoordinate>> findNodes(GridCoordinate topLeft, GridCoordinate bottomRight){
        List<Node<GridCoordinate>> listOfNodes = new ArrayList<>();
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

    private GridCoordinate generateRandomPos() {
        //TODO possible infinite loop if there is a node on every tile currently prevented since generateRTT func returns as soon as dest node is created
        GridCoordinate randPos;
        do {
            randPos = freeNodeList.get(SimulationApp.random.nextInt(freeNodeList.size()));
        }while(doesNodeExist(randPos));

        return randPos;
    }
    boolean doesNodeExist(GridCoordinate newPos) {
        return allNodesMap.containsKey(newPos);
        //return root.containsNodeWithData(root,newPos);
    }

    protected ArrayList<GridCoordinate> makePath(Node<GridCoordinate> destNode){
        ArrayList<GridCoordinate> path = new ArrayList<>();
        if(destNode.getParent() == null){
            path.add(new GridCoordinate(destNode.getData().getX(),destNode.getData().getY()));
            return path;
        }
        path = makePath(destNode.getParent());
        path.add(new GridCoordinate(destNode.getData().getX(),destNode.getData().getY()));
        return path;
    }

    private void assignBlockedNodeStatus(ArrayList<GridCoordinate> nodesToBeUpdated){
        //find the nodes to be blocked and set its statuses to true
        for(GridCoordinate n: nodesToBeUpdated) {
            if (allNodesMap.containsKey(n)) {
                //checks if node is already in blockedNodeList
                if(!blockedNodeList.contains(n)) {
                    //add blocked node to blockedNodeList and sets the status of node to "blocked"
                    blockedNodeList.add(n);
                    allNodesMap.get(n).setBlockedStatus(true);
                }
            }
            //Find nodes that are not blocked anymore, and free them. TODO: make help functions to make function pretty
            if(blockedNodeList.size() != nodesToBeUpdated.size()){
                ArrayList<GridCoordinate> freeList = blockedNodeList;
                freeList.removeAll(nodesToBeUpdated);
                for(GridCoordinate m: freeList){
                    if(allNodesMap.containsKey(m)){
                        allNodesMap.get(m).setBlockedStatus(false);
                    }
                }

            }
        }
    }

    private ArrayList<GridCoordinate> populateFreeList(){
        ArrayList<GridCoordinate> freeListInitializer = new ArrayList<>();
        for(int i = 0; i < WarehouseSpecs.wareHouseWidth ; i++){
            for(int j = 0; j < WarehouseSpecs.wareHouseHeight; j++ ){
                freeListInitializer.add(new GridCoordinate(i,j));
            }
        }
        return freeListInitializer;
    }
    private void updateFreeList(GridCoordinate pos){
        for(int i = 0; i < freeNodeList.size(); i++){
            if(pos == freeNodeList.get(i)){
                freeNodeList.remove(i);
            }
        }
    }
}
