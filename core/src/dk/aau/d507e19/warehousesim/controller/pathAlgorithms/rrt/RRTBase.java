package dk.aau.d507e19.warehousesim.controller.pathAlgorithms.rrt;

import dk.aau.d507e19.warehousesim.Simulation;
import dk.aau.d507e19.warehousesim.SimulationApp;
import dk.aau.d507e19.warehousesim.WarehouseSpecs;
import dk.aau.d507e19.warehousesim.controller.path.Path;
import dk.aau.d507e19.warehousesim.controller.path.Step;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.MovementPredictor;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.robot.RobotController;
import dk.aau.d507e19.warehousesim.controller.server.Reservation;
import dk.aau.d507e19.warehousesim.controller.server.ReservationManager;
import dk.aau.d507e19.warehousesim.controller.server.Server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public abstract class RRTBase {
    RobotController robotController;
    private Random random = new Random(Simulation.RANDOM_SEED);

    public RRTBase(RobotController robotController) {
        this.robotController = robotController;
    }


    public Node<GridCoordinate> root, destinationNode,latestNode;
    //Free list of all free points in the grid. populateFreeList() intializes the array with grid coordinates.
    public ArrayList<GridCoordinate> freeNodeList;

    protected ArrayList<Step> path = new ArrayList<>();
    public HashMap<GridCoordinate,Node<GridCoordinate>> allNodesMap = new HashMap<>();

    public ArrayList<Step> getPath() {
        return path;
    }

    protected void growRRT(Node<GridCoordinate> tree, int n) {
        //n is number of iterations
        //Generate a new random location using seeded random
        for(int i =0; i < n; i++){
            GridCoordinate randPos = generateRandomPos();
            Node<GridCoordinate> nearest = findNearestNeighbour(tree, randPos);
            Node<GridCoordinate> newNode = generateNewNode(nearest, randPos);
            if(allNodesMap.containsKey(newNode.getData())){
                System.out.println();
            }
            newNode.setParent(nearest);
            allNodesMap.put(newNode.getData(),newNode);
            latestNode = newNode;
            //Assign blocked nodes from server
            //assignBlockedNodeStatus(server.getReservedNotes);
        }
    }

    public void improvePath(GridCoordinate destination){
        List<Node<GridCoordinate>> potentialImprovements;
        if(!allNodesMap.containsKey(destination)){
            throw new RuntimeException("Can't be called if a path does not exist. Call generateRRTPath() before using this");
        }
        Node<GridCoordinate> currentParent = allNodesMap.get(destination).getParent();
        Node<GridCoordinate> bestParent = currentParent;
        //use find nodes in square function to find nodes
        potentialImprovements = trimImprovementsList(findNodesInRadius(destination,1),destination);

        if(!potentialImprovements.isEmpty()){
            //check number of steps to root and save the best node
            for (Node<GridCoordinate> n : potentialImprovements){
                //check if closer to root and if its in range
                if((isBetterParent(currentParent,n))){
                        bestParent = n;
                }
            }
            if(!(bestParent==currentParent && !allNodesMap.get(destination).getChildren().contains(bestParent))){
                /*System.out.println("Found better path! " +
                        bestParent.getData() +  " -> " + destination +
                        " Instead of " + currentParent.getData() +" -> " + destination);*/
                allNodesMap.get(destination).setParent(bestParent);
            }
        }
    }
    private boolean isBetterParent(Node<GridCoordinate> current, Node<GridCoordinate> possible){
        //remake function to only consider cost()
        //try cost as stepsToRoot first, then use
        if(current.equals(possible)){
            return false;
        }
        if(current.equals(root)){
            return false;
        }
        if(possible.equals(root)){
            return true;
        }
        return cost(current) > cost(possible);
    }

    public double cost(Node<GridCoordinate> node){
        return calculateTravelTime(new Path(makePath(node)));
    }
    protected long calculateTravelTime(Path p){
        //generate path from root to dest
        //find out how long it takes according to movement predictor
        //return time that it takes
        ArrayList<Reservation> list = MovementPredictor.calculateReservations(this.robotController.getRobot(),p,0,0);
        return list.get(list.size()-1).getTimeFrame().getStart();
    }

    protected ArrayList<Node<GridCoordinate>> trimImprovementsList(ArrayList<Node<GridCoordinate>> list, GridCoordinate dest){
        if(list.isEmpty()){
            return list;
        }
        list.removeIf(n-> distance(n.getData(),dest) !=1);
        return list;
    }

    protected double distance(GridCoordinate pos1, GridCoordinate pos2){
        return Math.sqrt(Math.pow(pos2.getX() - pos1.getX(), 2) + Math.pow(pos2.getY() - pos1.getY(), 2));
    }

    protected Node<GridCoordinate> generateNewNode(Node<GridCoordinate> nearest, GridCoordinate randPos) {
        //todo make this more readable
        GridCoordinate originalPos = nearest.getData();
        GridCoordinate pos = nearest.getData();
        //right
        pos = distance(new GridCoordinate(pos.getX() + 1, pos.getY()), randPos) < distance(pos, randPos) ? new GridCoordinate(originalPos.getX() + 1, originalPos.getY()) : pos;
        //left
        pos = distance(new GridCoordinate(pos.getX() - 1, pos.getY()), randPos) < distance(pos, randPos) ? new GridCoordinate(originalPos.getX() -1, originalPos.getY()) : pos;
        //up
        pos = distance(new GridCoordinate(pos.getX(), pos.getY() + 1), randPos) < distance(pos, randPos) ? new GridCoordinate(originalPos.getX(), originalPos.getY() +1) : pos;
        //down
        pos = distance(new GridCoordinate(pos.getX(), pos.getY() - 1), randPos) < distance(pos, randPos) ? new GridCoordinate(originalPos.getX(), originalPos.getY() -1 ) : pos;

        //remove the newly created note from the freeList
        updateFreeList(pos);
        return new Node<>(pos, null, false);
    }

    public Node<GridCoordinate> findNearestNeighbour(Node<GridCoordinate> tree, GridCoordinate randPos) {
        Node<GridCoordinate> shortest = null;
        for(Node<GridCoordinate> n : findNodesInSquare(randPos)){
            if (shortest == null || distance(n.getData(),randPos) < distance(shortest.getData(),randPos)){
                shortest = n;
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
        return shortest;
    }

    private ArrayList<Node<GridCoordinate>> findNodesInSquare(GridCoordinate randPos){
        ArrayList<Node<GridCoordinate>> listOfNodes =  new ArrayList<>(),foundNodes;
        GridCoordinate topLeft = new GridCoordinate(randPos.getX(),randPos.getY());
        GridCoordinate bottomRight = new GridCoordinate(randPos.getX(),randPos.getY());
        while(listOfNodes.isEmpty()){
            //check if new corners are out of grid bounds
            // Create new corners (probably not necessary)
            topLeft = updateTopLeft(topLeft);
            bottomRight = updateBottomRight(bottomRight);
            foundNodes = findNodes(topLeft,bottomRight);
            if(!foundNodes.isEmpty()) listOfNodes.addAll(foundNodes);
        }
        return listOfNodes;
    }

    public ArrayList<Node<GridCoordinate>> findNodesInRadius(GridCoordinate randPos, int k){
        ArrayList<Node<GridCoordinate>> listOfNodes =  new ArrayList<>();
        ArrayList<Node<GridCoordinate>> foundNodes;
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

    private ArrayList<Node<GridCoordinate>> findNodes(GridCoordinate topLeft, GridCoordinate bottomRight){
        ArrayList<Node<GridCoordinate>> listOfNodes = new ArrayList<>();
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
        if(bottomRight.getX() + 1 <= Simulation.getWarehouseSpecs().wareHouseWidth){
            bottomRight.setX(bottomRight.getX()+1);
        }
        if(bottomRight.getY() + 1 <= Simulation.getWarehouseSpecs().wareHouseHeight){
            bottomRight.setY(bottomRight.getY()+1);
        }
        return bottomRight;
    }

    protected GridCoordinate generateRandomPos() {
        GridCoordinate randPos;
        do {
            randPos = freeNodeList.get(random.nextInt(freeNodeList.size()));
            if(!freeNodeList.contains(randPos)){
                System.out.println();
            }
        }while(doesNodeExist(randPos));

        return randPos;
    }
    boolean doesNodeExist(GridCoordinate newPos) {
        return allNodesMap.containsKey(newPos);
        //return root.containsNodeWithData(root,newPos);
    }

    public ArrayList<Step> makePath(Node<GridCoordinate> destNode){
        ArrayList<Step> list = new ArrayList<>();
        for(Node<GridCoordinate> n : getNodesInPath(destNode)){
            list.add(new Step(n.getData()));
        }
        return list;
    }
    protected ArrayList<Node<GridCoordinate>> getNodesInPath(Node<GridCoordinate> destNode){
        ArrayList<Node<GridCoordinate>> path = new ArrayList<>();
        if(destNode.getParent() == null){
            path.add(destNode);
            return path;
        }
        path = getNodesInPath(destNode.getParent());
        path.add(destNode);
        return path;
    }

    protected void growUntilPathFound(GridCoordinate destination){
        boolean foundPath = false;
        //Run until a route is found
        while (!foundPath) {
            //grow tree by one each time(maybe inefficient?)
            growRRT(root, 1);
            foundPath = doesNodeExist(destination);
        }
    }
    protected void growUntilAllNodesFound(){
        boolean fullyExplored = false;
        while(!fullyExplored){
            growRRT(root,1);
            fullyExplored = isFullyExplored();
        }
    }
    protected boolean isFullyExplored(){
        if(allNodesMap.size() == Simulation.getWarehouseSpecs().wareHouseWidth * Simulation.getWarehouseSpecs().wareHouseHeight){
            return true;
        }
        return false;
    }

    private ArrayList<GridCoordinate> populateFreeList(){
        ArrayList<GridCoordinate> freeListInitializer = new ArrayList<>();
        for(int i = 0; i < Simulation.getWarehouseSpecs().wareHouseWidth ; i++){
            for(int j = 0; j < Simulation.getWarehouseSpecs().wareHouseHeight; j++ ){
                freeListInitializer.add(new GridCoordinate(i,j));
            }
        }
        //remove robot position when done populating
        freeListInitializer.remove(robotController.getRobot().getGridCoordinate());
        return freeListInitializer;
    }
    private void updateFreeList(GridCoordinate pos){
        if(freeNodeList.contains(pos)){
            freeNodeList.remove(pos);
        }
    }
    protected ArrayList<Step> generatePathFromEmpty(GridCoordinate start, GridCoordinate destination){
        root = new Node<>(start, null, false);
        //populate the freenodelist
        freeNodeList = populateFreeList();
        //add root node to list of nodes
        allNodesMap.put(root.getData(),root);
        //grow until we have a path
        //when function completes we know that we have a path
        growUntilPathFound(destination);
        destinationNode = allNodesMap.get(destination);
        path =  makePath(destinationNode);
        return path;

    }
    public ArrayList<Step> generatePath(GridCoordinate start, GridCoordinate destination){
        if(allNodesMap.isEmpty()){
            return generatePathFromEmpty(start,destination);
        }
        //Set root to equal starting point
        root = allNodesMap.get(start);
        //only set root if it isnt already
        if(!(root.getParent()==null)){
            root.makeRoot();
        }
        //grow until we have a path
        //when function completes we know that we have a path
        if(allNodesMap.size()!= Simulation.getWarehouseSpecs().wareHouseHeight * Simulation.getWarehouseSpecs().wareHouseWidth){
            growUntilPathFound(destination);
        }
        destinationNode = allNodesMap.get(destination);
        path = makePath(destinationNode);
        return makePath(destinationNode);
    }
    private void growKtimes(GridCoordinate destination, int k){
        for(int i = 0; i < k; i++){
            growRRT(root,k);
        }
    }

    //rewire tree to ignore collideable object
}
/*
    public void assignBlockedNodeStatus(ArrayList<Reservation> nodesToBeUpdated){
        ArrayList<GridCoordinate> nodesToBeUpdatedConverted = new ArrayList<>();
        for(Reservation n: nodesToBeUpdated){
            nodesToBeUpdatedConverted.add(n.getGridCoordinate());
        }
        //find the nodes to be blocked and set its statuses to true
        for(GridCoordinate n: nodesToBeUpdatedConverted) {
            if (allNodesMap.containsKey(n)) {
                //checks if node is already in blockedNodeList
                if(!blockedNodeList.contains(n)) {
                    //add blocked node to blockedNodeList and sets the status of node to "blocked"
                    blockedNodeList.add(n);
                    allNodesMap.get(n).setBlockedStatus(true);
                }
            }
        }
        //Find nodes that are not blocked anymore, and free them. TODO: make help functions to make function pretty
        if(blockedNodeList.size() != nodesToBeUpdated.size()){
            ArrayList<GridCoordinate> tempList = blockedNodeList;
            tempList.remove(nodesToBeUpdated);
            for(GridCoordinate m: tempList){
                if(allNodesMap.containsKey(m)){
                    allNodesMap.get(m).setBlockedStatus(false);
                }
            }
        }
    }
    public void rewireTreeFromCollision(Node<GridCoordinate> collideableNode, Node<GridCoordinate> tempDestNode){

        rewireToTempDestNode(collideableNode.getParent(), tempDestNode);
    }
    //Fubnction parameter is the node prior to the collision node
    private void rewireToTempDestNode(Node<GridCoordinate> currentNode, Node<GridCoordinate> tempDestNode){
        ArrayList<Node<GridCoordinate>> listOfNeighbours;
        //check if destNode is reached
        if(currentNode.getData() == tempDestNode.getData()){
            return;
        }
        //if there are other nodes around parent node of collideable object, rewire to this
        Edge edge = new Edge(null, null);
        Node<GridCoordinate> bestChildNode = currentNode;

        //generate missing notes. Has to handle each direction.
        listOfNeighbours = trimImprovementsList(findNodesInRadius(currentNode.getData(), 1), tempDestNode.getData());
        if(listOfNeighbours.size() != 4){
            for (int i = 0; i < 4 - currentNode.getChildren().size(); i++) {
                if (!(allNodesMap.containsKey(new GridCoordinate(currentNode.getData().getX() + 1, currentNode.getData().getY())))) {
                    Node<GridCoordinate> rightNode = new Node<GridCoordinate>(new GridCoordinate(currentNode.getData().getX() + 1, currentNode.getData().getY()), currentNode, false);
                    listOfNeighbours.add(rightNode);
                }
                if (!(allNodesMap.containsKey(new GridCoordinate(currentNode.getData().getX() - 1, currentNode.getData().getY())))) {
                    Node<GridCoordinate> leftNode = new Node<GridCoordinate>(new GridCoordinate(currentNode.getData().getX() - 1, currentNode.getData().getY()), currentNode, false);
                    listOfNeighbours.add(leftNode);
                }
                if (!(allNodesMap.containsKey(new GridCoordinate(currentNode.getData().getX(), currentNode.getData().getY() + 1)))) {
                    Node<GridCoordinate> upNode = new Node<GridCoordinate>(new GridCoordinate(currentNode.getData().getX(), currentNode.getData().getY() + 1), currentNode, false);
                    listOfNeighbours.add(upNode);
                }
                if (!(allNodesMap.containsKey(new GridCoordinate(currentNode.getData().getX(), currentNode.getData().getY() - 1)))) {
                    Node<GridCoordinate> downNode = new Node<GridCoordinate>(new GridCoordinate(currentNode.getData().getX(), currentNode.getData().getY() - 1), currentNode, false);
                    listOfNeighbours.add(downNode);
                }
            }
        }
        if(listOfNeighbours.size() == 4){ //Find the best child node to rewire tree to. Rewire and return the next node.
            //Update the nodes for collision objects
            assignBlockedNodeStatus(reservationManager.getAllCurrentReservations(robotController.getServer().getTimeInTicks()));
            double shortestDistanceNeighbour = edge.getDistanceBetweenPoints(listOfNeighbours.get(1).getData(), tempDestNode.getData());

            for(Node<GridCoordinate> n: listOfNeighbours){
                if(!(n.getBlockedStatus())){
                    double currentChildNode = edge.getDistanceBetweenPoints(n.getData(), tempDestNode.getData());
                    if(currentChildNode < shortestDistanceNeighbour){
                        shortestDistanceNeighbour = currentChildNode;
                        bestChildNode = n;
                    }
                }
            }
            bestChildNode.setParent(currentNode);
            rewireToTempDestNode(bestChildNode, tempDestNode);
        }
    }
 */
