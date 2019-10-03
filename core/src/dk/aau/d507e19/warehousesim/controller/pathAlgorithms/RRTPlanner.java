package dk.aau.d507e19.warehousesim.controller.pathAlgorithms;

import dk.aau.d507e19.warehousesim.Position;
import dk.aau.d507e19.warehousesim.SimulationApp;
import dk.aau.d507e19.warehousesim.WarehouseSpecs;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import java.lang.Math;

public class RRTPlanner {

   public Node<Position> shortestLengthNode;
   private Node<Position> root;

    void generateRRTroute(Robot robot, Position destination){
        boolean hasRoute = false;
        root = new Node<Position>(robot.getCurrentPosition(),null);
        //Run until a route is found
        while(!hasRoute){
            growRRT(root);
            //traverse tree to see if destination is reachable
            hasRoute = root.containsData(root);
        }
    }
    private Node<Position> growRRT(Node<Position> tree){
        //Generate a new random location using seeded random
        Position randPos = generateRandomPos();
        shortestLengthNode = tree;
        Node<Position> nearest = findNearestNeighbour(tree,randPos);
        Node<Position> newNode = generateNewNode(nearest,randPos);
        nearest.addChild(newNode);
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
    private Node<Position> generateNewNode(Node<Position> nearest, Position randPos){
        Position pos = nearest.getData();
        //right
        pos = getDistanceBetweenPoints(new Position(pos.getX()+1,pos.getY()),randPos) < getDistanceBetweenPoints(pos,randPos) ? new Position(pos.getX()+1,pos.getY()) : pos;
        //left
        pos = getDistanceBetweenPoints(new Position(pos.getX()-1,pos.getY()),randPos) < getDistanceBetweenPoints(pos,randPos) ? new Position(pos.getX()+1,pos.getY()) : pos;
        //up
        pos = getDistanceBetweenPoints(new Position(pos.getX(),pos.getY()+1),randPos) < getDistanceBetweenPoints(pos,randPos) ? new Position(pos.getX()+1,pos.getY()) : pos;
        //down
        pos = getDistanceBetweenPoints(new Position(pos.getX(),pos.getY()-1),randPos) < getDistanceBetweenPoints(pos,randPos) ? new Position(pos.getX()+1,pos.getY()) : pos;

        return new Node<Position>(pos,null);
    }
    private Position generateRandomPos(){
        Position randPos;
        do{
            randPos = new Position(
                    SimulationApp.random.nextInt(WarehouseSpecs.wareHouseWidth),
                    SimulationApp.random.nextInt(WarehouseSpecs.wareHouseHeight));

        }while(doesNodeExist(randPos));

        return randPos;
    }

    private double getDistanceBetweenPoints(Position pos1, Position pos2){
        return Math.sqrt(Math.pow(pos2.getX()-pos1.getX(),2)+Math.pow(pos2.getY()-pos1.getY(),2));
    }
    private boolean doesNodeExist(Position newPos){
        return root.containsData(new Node<Position>(newPos,null));
    }
}
