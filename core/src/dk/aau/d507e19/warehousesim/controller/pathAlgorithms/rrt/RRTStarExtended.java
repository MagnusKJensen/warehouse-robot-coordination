package dk.aau.d507e19.warehousesim.controller.pathAlgorithms.rrt;

import dk.aau.d507e19.warehousesim.SimulationApp;
import dk.aau.d507e19.warehousesim.controller.path.Path;
import dk.aau.d507e19.warehousesim.controller.path.Step;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.RobotController;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class RRTStarExtended extends RRTStar {
    public RRTStarExtended(RobotController robotController) {
        super(robotController);
    }


    @Override
    public void attemptOptimise(){
        //optimise with distance first
        //then optimise with time
        optimalPathOptimise(destinationNode);
            //System.out.println("DID NOT FIND OPTIMAL");
            //smartOptimiseTime(destinationNode);
        path = makePath(destinationNode);

    }
    public void smartOptimiseDistance(Node<GridCoordinate> node){
        if (node.getParent() != null) {
            for(Node<GridCoordinate> n : trimImprovementsList(findNodesInRadius(node.getData(),1),node.getData())){
                if(n.equals(node.getParent()) || node.getParent().equals(root)){
                    continue;
                }
                if(!canBeRewired(n,node)){
                    continue;
                }
                if(n.equals(root)){
                    node.setParent(n);
                }else{
                    if(distance(n.getData(),root.getData()) < distance(node.getParent().getData(),root.getData()) ){
                        node.setParent(n);
                    }
                }
            }
            smartOptimiseDistance(node.getParent());
        }
    }

    public void smartOptimiseTime(Node<GridCoordinate> node){
        if (node.getParent() != null) {
            for(Node<GridCoordinate> n : trimImprovementsList(findNodesInRadius(node.getData(),1),node.getData())){
                if(n.equals(node.getParent()) || node.getParent().equals(root)){
                    continue;
                }
                if(!canBeRewired(n,node)){
                    if(!hasBetterParent(n)){
                        continue;
                    }
                }
                if(n.equals(root)){
                    node.setParent(n);
                }else{
                    //parentOptimiser(n);
                    //might change node's parent to root so check to prevent
                    if(node.getParent().equals(root)){
                        continue;
                    }
                    if(cost(n) < cost(node.getParent()) ){
                        node.setParent(n);
                    }
                }
            }
            smartOptimiseTime(node.getParent());
        }
    }
    private void parentOptimiser(Node<GridCoordinate> n){
        ArrayList<Node<GridCoordinate>> neighbours = trimImprovementsList(findNodesInRadius(n.getData(),1),n.getData());
        if(neighbours.contains(root)){
            n.setParent(root);
        }
        for(Node<GridCoordinate> pn: neighbours){
            if(n.getParent().equals(root)){
                continue;
            }
            if(!canBeRewired(pn,n)){
                continue;
            }
            if(pn.equals(n.getParent())){
                parentOptimiser(pn);
            }
            if(pn.equals(root)){
                n.setParent(pn);
            }else{
                if(distance(pn.getData(),root.getData()) < distance(n.getParent().getData(),root.getData())){
                    n.setParent(pn);
                    //parentOptimiser(pn);
                }
            }
        }
    }
    private boolean hasBetterParent(Node<GridCoordinate> node){
        for(Node<GridCoordinate> n: trimImprovementsList(findNodesInRadius(node.getData(),1),node.getData())){
            if(node.getParent().equals(n)){
                continue;
            }
            if(distance(n.getData(),root.getData()) < distance(node.getParent().getData(),root.getData())){
                return true;
            }
        }
        return false;
    }
    public boolean optimalPathOptimise(Node<GridCoordinate> node){
        ArrayList<Node<GridCoordinate>> neighbours = trimImprovementsList(findNodesInRadius(node.getData(),1),node.getData());
        if(neighbours.contains(root)){
            node.setParent(root);
            System.out.println("MADE OPTIMAL PATH");
            return true;
        }
        if(destinationNode.getData().equals(new GridCoordinate(19,2)) && node.getData().equals(new GridCoordinate(8,2))){
            System.out.println();
        }
        for(Node<GridCoordinate> neighbour : neighbours) {
            if (!canBeRewired(neighbour, node)) {
                continue;
            }
            //if n is on the same axis as root and its distance is closer then make n new parent
            if (neighbour.getData().getX() == root.getData().getX() || neighbour.getData().getY() == root.getData().getY()) {
                if (distance(neighbour.getData(), root.getData()) < distance(node.getData(), root.getData())) {
                    node.setParent(neighbour);
                    return optimalPathOptimise(neighbour);
                }
            }
        }
        for(Node<GridCoordinate> neighbour : neighbours) {
            if (!canBeRewired(neighbour, node)) {
                continue;
            }
            //if n is on the same axis as root and its distance is closer then make n new parent
            if(calcDirection(node.getData(),root.getData()).equals(calcDirection(neighbour.getData(),root.getData()))){

                node.setParent(neighbour);
                return optimalPathOptimise(neighbour);
            }
        }
        return false;

    }
    @Override
    protected void growUntilPathFound(GridCoordinate destination) {
        //grow tree fully to ensure perfect paths todo change to only generate within bounds
        growUntilFullyExplored();
    }


}
