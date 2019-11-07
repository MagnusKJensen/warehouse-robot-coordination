package dk.aau.d507e19.warehousesim.controller.pathAlgorithms.rrt;

import com.badlogic.gdx.math.Vector2;
import dk.aau.d507e19.warehousesim.controller.path.Step;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.robot.RobotController;
import dk.aau.d507e19.warehousesim.controller.server.Server;

import java.util.ArrayList;
import java.util.List;

public class RRTStar extends RRTBase {

    public RRTStar(RobotController robotController) {
        super(robotController);
    }

    public ArrayList<Step> generatePathFromEmpty(GridCoordinate start, GridCoordinate destination){
        super.generatePathFromEmpty(start,destination);
        attemptOptimise();
        //attemptSmoothPath();
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
        ArrayList<Node<GridCoordinate>> nodesInPath = getNodesInPath(copyRoot.findNode(destinationNode.getData()));
        Node<GridCoordinate> backupCpy = root.copy();
        //then call optimise on the copy, if copy is better then root = copy
        //we check for every node in the path starting from root, if we find a optimization we end the loop immediately
            for(int i=0; i <nodesInPath.size(); i++){
                if(optimise(nodesInPath.get(i),copyRoot.findNode(destinationNode.getData()))){
                    //check if it is actually faster
                    if(cost(copyRoot.findNode(destinationNode.getData())) < cost(destinationNode)){
                       // possibleOptimisations.add(copyRoot);
                        root = copyRoot;
                        destinationNode = copyRoot.findNode(destinationNode.getData());
                        path = makePath(destinationNode);
                        updateAllNodes(root);
                        break;
                    }
                }
                //reset copied tree
                copyRoot = backupCpy;
            }
            //reset copied tree
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
        //Check if we can choose the smoothest path before we try to pick best neighbour only do this after we're past root
        //if we're at the same x or y axis as dest, we shouldnt use this method
       if(node.getParent()!=null && !isSameAxis(node,destination)){
            String nodeDirection = calcDirection(node.getParent().getData(),node.getData());
            for(Node<GridCoordinate> n : neighbours){
                if(calcDirection(node.getData(),n.getData()).equals(nodeDirection) && !n.getData().equals(node.getParent().getData())){
                    //if n is in the same direction as we're currently going and its not the parent node of turnnode then we can rewire
                    bestNeighbour = node.getRoot().findNode(n.getData());
                }
            }
       }

        //find the "best" neighbour - if bestneighbour is not null, then we've already set it and can skip this
        if(bestNeighbour==null){
            for (Node<GridCoordinate> neighbour : neighbours){
                if(bestNeighbour == null || distance(neighbour.getData(),destination.getData()) < distance(bestNeighbour.getData(),destination.getData())){
                    //neighbour is a node from allNodesMap, meaning it is a part of the orignial root. We need to set bestNeighbour = to the copied version
                    bestNeighbour = node.getRoot().findNode(neighbour.getData());
                }
            }
        }

        if(bestNeighbour == null){
            //this happens for some unknown reason so if it does we cant optimise(for now) todo fix nullpointer here - potentially due to copies of trees
            return false;
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
    private boolean isSameAxis(Node<GridCoordinate> n1, Node<GridCoordinate> n2){
        return n1.getData().getX() == n2.getData().getX() || n1.getData().getY() == n2.getData().getY();
    }
    public ArrayList<Node<GridCoordinate>> findTurns(ArrayList<Node<GridCoordinate>> path){
        ArrayList<Node<GridCoordinate>> turnNodes = new ArrayList<>();
        //set i to start from 1, so that we skip root
        //set loop to end at path.size()-1, so that we dont call i+1 when i is equal to number of elements in list
        for(int i =1; i < path.size()-1; i++){
            if(!(calcDirection(path.get(i-1).getData(),path.get(i).getData()).equals(calcDirection(path.get(i).getData(),path.get(i+1).getData())))){
                turnNodes.add(path.get(i));
            }
        }
        return turnNodes;
    }
    private String calcDirection (GridCoordinate p0, GridCoordinate p1){
        Vector2 up,down,left,right;
        up = new Vector2(0,-1);
        down = new Vector2(0,1);
        left = new Vector2(-1,0);
        right = new Vector2(1,0);
        //create a vector from points
        Vector2 v = new Vector2(p1.getX() - p0.getX(),p1.getY()-p0.getY());
        if(v.equals(up)){
            return "Up";
        }else if(v.equals(down)){
            return "Down";
        }else if(v.equals(left)){
            return "Left";
        }else if(v.equals(right)){
            return "Right";
        }else{
            throw new RuntimeException("Vector " +  v.toString() + "is not up,down,left or right");
        }
    }
}
