package dk.aau.d507e19.warehousesim.controller.pathAlgorithms;

import dk.aau.d507e19.warehousesim.Position;
import dk.aau.d507e19.warehousesim.SimulationApp;
import dk.aau.d507e19.warehousesim.WarehouseSpecs;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import java.lang.Math;

public class RRTPlanner {

   public Node<Position> shortestLengthNode;

    void generateRRTroute(Robot robot, Position destination){
        boolean hasRoute = false;
        Node<Position> Tree = new Node<Position>(robot.getCurrentPosition(),null);
        //Run until a route is found
        while(!hasRoute){
            growRRT(Tree);
            //traverse tree to see if destination is reachable
            hasRoute = Tree.containsData(Tree);
        }
    }
    private Node<Position> growRRT(Node<Position> tree){
        //Generate a new random location using seeded random
        Position randPos = new Position(
                SimulationApp.random.nextInt(WarehouseSpecs.wareHouseWidth),
                SimulationApp.random.nextInt(WarehouseSpecs.wareHouseHeight));
        shortestLengthNode = tree;
        Node<Position> nearest = findNearestNeighbour(tree,randPos);
        //Todo find a good way to add the new point to the tree
        return tree;
    }
    public Node<Position> findNearestNeighbour(Node<Position> tree, Position randPos){
        for(Node<Position> n : tree.getChildren()){
            double newDistance = getDistanceBetweenPoints(n.getData(),randPos);

            if( newDistance < getDistanceBetweenPoints(shortestLengthNode.getData(),randPos)){
                shortestLengthNode = n;
            }
            findNearestNeighbour(n, randPos);
        }
        return shortestLengthNode;
    }


    private double getDistanceBetweenPoints(Position pos1, Position pos2){
        return Math.sqrt(Math.pow(pos2.getX()-pos1.getX(),2)+Math.pow(pos2.getY()-pos1.getY(),2));
    }

}
