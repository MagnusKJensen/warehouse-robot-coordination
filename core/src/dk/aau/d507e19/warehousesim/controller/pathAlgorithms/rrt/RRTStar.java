package dk.aau.d507e19.warehousesim.controller.pathAlgorithms.rrt;

import dk.aau.d507e19.warehousesim.controller.path.Step;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.robot.RobotController;
import dk.aau.d507e19.warehousesim.controller.server.Server;

import java.util.ArrayList;

public class RRTStar extends RRTBase {

    public RRTStar(RobotController robotController) {
        super(robotController);
    }

    public ArrayList<Step> generatePathFromEmpty(GridCoordinate start, GridCoordinate destination){
        super.generatePathFromEmpty(start,destination);
        attemptOptimise();
        return path;
    }

    public ArrayList<Step> generatePath(GridCoordinate start, GridCoordinate destination){
        //make sure we have a path
        super.generatePath(start,destination);
        //attempt to optimise the path
        attemptOptimise();
        return path;
    }

    @Override
    protected void growUntilPathFound(GridCoordinate destination){
        //growUntilFullyExplored();
        //Run until a route is found
        while (!allNodesMap.containsKey(destination)) {
            //grow tree by one each time(maybe inefficient?)
            growRRT(root, 1);
            //every time a new node is added, check to see if we can improve it
            improvePath(latestNode.getData());
            //check if any of the nodes in the vicinity can be rewired to reduce their cost
            rewire(latestNode);
        }
    }
    private void growUntilFullyExplored(){
        while (!isFullyExplored()) {
            //grow tree by one each time(maybe inefficient?)
            growRRT(root, 1);
            //every time a new node is added, check to see if we can improve it
            improvePath(latestNode.getData());
            //check if any of the nodes in the vicinity can be rewired to reduce their cost
            rewire(latestNode);
        }
    }
    private void rewire(Node<GridCoordinate> node){
        //find potential nodes that could have their paths improved.
        //for each one, check if their path would be better if node was their parent
        ArrayList<Node<GridCoordinate>> neighbours = trimImprovementsList(findNodesInRadius(node.getData(),1),node.getData());
        for(Node<GridCoordinate> n : neighbours){
            if(canBeRewired(node,n)){
                //make copyTree
                Node<GridCoordinate> copyRoot = root.copy();
                //set copy of node to be parent of copy of n
                Node<GridCoordinate> copyN = copyRoot.findNode(n.getData());
                copyN.setParent(copyRoot.findNode(node.getData()));
                //check if the cost of real tree to n is worse than copy version
                if(cost(n) > cost(copyN)){
                    //if n is worse copy, we make the copy a reality
                    n.setParent(node);
                }
            }
        }
    }
    private boolean canBeRewired(Node<GridCoordinate> node, Node<GridCoordinate> n){
        if(n.getParent() == null){
            return false;
        } else if (n.getParent().equals(node)) {
            return false;
        } else if(n.getParent().equals(root)){
            return false;
        } else if(node.getParent().equals(n)){
            return false;
        }else if (isInPathToRoot(node,n)){
            //if we get here then n is not the direct parent, but might be parents parent,
            //therefore its always beneficial for us to set nodes parent to n.
            node.setParent(n);
            return false;
        }else return true;
    }
    private boolean isInPathToRoot(Node<GridCoordinate> node, Node<GridCoordinate> n){
        if(node.getParent() == null){
            return false;
        }
        if(node.getParent().equals(n)){
            return true;
        }
        return isInPathToRoot(node.getParent(),n);
    }
    public void attemptOptimise(){
        //create a copy of the tree
        Node<GridCoordinate> copyRoot = root.copy();
        //then call optimise on the copy, if copy is better then root = copy
        if(optimise(copyRoot,copyRoot.findNode(destinationNode.getData()))){
            //check if it is actually faster
            if(cost(copyRoot.findNode(destinationNode.getData())) < cost(destinationNode)){
                root = copyRoot;
                destinationNode = root.findNode(destinationNode.getData());
                path = makePath(destinationNode);
                updateAllNodes(root);
            }
        }
    }
    private boolean optimise(Node<GridCoordinate> node, Node<GridCoordinate> destination){
        Node<GridCoordinate> bestNeighbour = null;
        //check if we have reached dest, if we have the return true
        if(node.equals(destination)){
            return true;
        }
        //check if node can connect to a node thats closer(distance) to dest, return false if there are no nodes better nodes nearby
        ArrayList<Node<GridCoordinate>> neighbours = trimImprovementsList(findNodesInRadius(node.getData(),1),node.getData());
        if(neighbours.isEmpty()){
            //should never happen since node is a part of a path, but makes intellij remove warning
            return false;
        }
        //find the "best" neighbour
        for (Node<GridCoordinate> neighbour : neighbours){
            if(bestNeighbour == null || distance(neighbour.getData(),destination.getData()) < distance(bestNeighbour.getData(),destination.getData())){
                //neighbour is a node from allNodesMap, meaning it is a part of the orignial root. We need to set bestNeighbour = to the copied version
                bestNeighbour = node.getRoot().findNode(neighbour.getData());
            }
        }
        //if best neighbour is worse than node, return false
        if(distance(bestNeighbour.getData(),destination.getData()) > distance(node.getData(),destination.getData())){
            return false;
        }
        //connect to node and run this method on next node in line
        bestNeighbour.setParent(node);
        return optimise(bestNeighbour,destination);


    }
    private void updateAllNodes(Node<GridCoordinate> node){
        allNodesMap.put(node.getData(),node);
        for(Node<GridCoordinate> child : node.getChildren()){
            updateAllNodes(child);
        }
    }
}
