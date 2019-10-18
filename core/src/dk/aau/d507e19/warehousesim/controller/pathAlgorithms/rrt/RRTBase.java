package dk.aau.d507e19.warehousesim.controller.pathAlgorithms.rrt;

import dk.aau.d507e19.warehousesim.SimulationApp;
import dk.aau.d507e19.warehousesim.WarehouseSpecs;
import dk.aau.d507e19.warehousesim.controller.path.Path;
import dk.aau.d507e19.warehousesim.controller.path.Step;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.MovementPredictor;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.server.Reservation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public abstract class RRTBase {
    Robot robot;

    public RRTBase(Robot robot) {
        this.robot = robot;
    }

    public Node<GridCoordinate> root, destinationNode,shortestLengthNode,latestNode;
    //Free list of all free points in the grid. populateFreeList() intializes the array with grid coordinates.
    public ArrayList<GridCoordinate> freeNodeList = populateFreeList();
    //blockedNodeList
    public ArrayList<GridCoordinate> blockedNodeList = new ArrayList<>();

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
                if((isBetterParent(currentParent,n,allNodesMap.get(destination)))){
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

    private boolean isBetterParent(Node<GridCoordinate> current, Node<GridCoordinate> possible, Node<GridCoordinate> child){
        if(current.equals(possible)){
            return false;
        }
        if(current.stepsToRoot() > possible.stepsToRoot()){
            return true;
        } else if(current.stepsToRoot() == possible.stepsToRoot()){
            /*
            //Make a copy of our tree(ugh) todo find better way to do this
            Node<GridCoordinate> posTree = root.copy();
            //find possible node in tree
            Node<GridCoordinate> posTreeGoal = posTree.findNode(destinationNode.getData());
            //rewire child node to have possible as parent
            posTree.findNode(child.getData()).setParent(possible);
            //return the fastest path from either to the goal
            return calculateTravelTime(current,destinationNode) > calculateTravelTime(posTree,posTreeGoal);*/
        }

        return false;
    }
    private long calculateTravelTime(Node<GridCoordinate> root, Node<GridCoordinate> dest){
        //generate path from root to dest
        //find out how long it takes according to movement predictor
        //return time that it takes
        Path p = new Path(makePathBetweenTwoNodes(root,dest));
        ArrayList<Reservation> list = MovementPredictor.calculateReservations(this.robot,p,0,0);
        return list.get(list.size()-1).getTimeFrame().getStart();
    }
    private List<Node<GridCoordinate>> trimImprovementsList(List<Node<GridCoordinate>> list, GridCoordinate dest){
        if(list.isEmpty()){
            return list;
        }
        list.removeIf(n-> distance(n.getData(),dest) !=1);
        return list;
    }

    public void improveEntirePath(Node<GridCoordinate> destination){
        //Optimize for dest, then optimize for this.getparent
        if(destination.getParent()!=null){
            improvePath(destination.getData());
            improveEntirePath(destination.getParent());
        }
    }

    private double distance(GridCoordinate pos1, GridCoordinate pos2){
        return Math.sqrt(Math.pow(pos2.getX() - pos1.getX(), 2) + Math.pow(pos2.getY() - pos1.getY(), 2));
    }

    protected Node<GridCoordinate> generateNewNode(Node<GridCoordinate> nearest, GridCoordinate randPos) {
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

        //remove the newly created note from the freeList
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

    protected GridCoordinate generateRandomPos() {
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

    public ArrayList<Step> makePath(Node<GridCoordinate> destNode){
        ArrayList<Step> path = new ArrayList<>();
        if(destNode.getParent() == null){
            path.add(new Step(new GridCoordinate(destNode.getData().getX(),destNode.getData().getY())));
            return path;
        }
        path = makePath(destNode.getParent());
        path.add(new Step(new GridCoordinate(destNode.getData().getX(),destNode.getData().getY())));
        return path;
    }
    public ArrayList<Step> makePathBetweenTwoNodes(Node<GridCoordinate> startNode, Node<GridCoordinate> destNode){
        ArrayList<Step> path = new ArrayList<>();
        if(destNode.getParent()== null || destNode.equals(startNode)){
            path.add(new Step(new GridCoordinate(destNode.getData().getX(),destNode.getData().getY())));
            return path;
        }
        path = makePathBetweenTwoNodes(startNode,destNode.getParent());
        path.add(new Step(new GridCoordinate(destNode.getData().getX(),destNode.getData().getY())));
        return path;
    }

    public void assignBlockedNodeStatus(ArrayList<GridCoordinate> nodesToBeUpdated){
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
        }
        //Find nodes that are not blocked anymore, and free them. TODO: make help functions to make function pretty
        if(blockedNodeList.size() != nodesToBeUpdated.size()){
            //tempList is used to find the unique nodes
            ArrayList<GridCoordinate> tempList = blockedNodeList;
            tempList.removeAll(nodesToBeUpdated);
            for(GridCoordinate m: tempList){
                if(allNodesMap.containsKey(m)){
                    allNodesMap.get(m).setBlockedStatus(false);
                }
            }
        }
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
    private boolean isFullyExplored(){
        if(allNodesMap.size() == WarehouseSpecs.wareHouseWidth*WarehouseSpecs.wareHouseHeight){
            return true;
        }
        return false;
    }

    private ArrayList<GridCoordinate> populateFreeList(){
        ArrayList<GridCoordinate> freeListInitializer = new ArrayList<>();
        for(int i = 0; i < WarehouseSpecs.wareHouseWidth ; i++){
            for(int j = 0; j < WarehouseSpecs.wareHouseHeight; j++ ){
                //If coordinate is root(The robot's position), dont add.
                freeListInitializer.add(new GridCoordinate(i,j));
            }
        }
        return freeListInitializer;
    }
    private void updateFreeList(GridCoordinate pos){
        if(freeNodeList.contains(pos)){
            freeNodeList.remove(pos);
        }
    }
    protected ArrayList<Step> generatePathFromEmpty(GridCoordinate start, GridCoordinate destination){
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
        if(allNodesMap.size()!=WarehouseSpecs.wareHouseHeight*WarehouseSpecs.wareHouseWidth){
            growUntilPathFound(destination);
        }
        destinationNode = allNodesMap.get(destination);
        return makePath(destinationNode);
    }
    private void growKtimes(GridCoordinate destination, int k){
        for(int i = 0; i < k; i++){
            growRRT(root,k);

        }
    }
}
