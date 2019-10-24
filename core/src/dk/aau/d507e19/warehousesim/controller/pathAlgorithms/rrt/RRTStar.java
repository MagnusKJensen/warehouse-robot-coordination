package dk.aau.d507e19.warehousesim.controller.pathAlgorithms.rrt;

import dk.aau.d507e19.warehousesim.controller.path.Step;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;

import java.util.ArrayList;

public class RRTStar extends RRTBase {
    public RRTStar(Robot robot) {
        super(robot);
    }

    public ArrayList<Step> generatePathFromEmpty(GridCoordinate start, GridCoordinate destination){
        return super.generatePathFromEmpty(start,destination);
    }

    public ArrayList<Step> generatePath(GridCoordinate start, GridCoordinate destination){
        //make sure we have a path
        return super.generatePath(start,destination);
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
}
