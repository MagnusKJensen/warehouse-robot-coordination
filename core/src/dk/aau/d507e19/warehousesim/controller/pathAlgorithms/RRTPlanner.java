package dk.aau.d507e19.warehousesim.controller.pathAlgorithms;
import dk.aau.d507e19.warehousesim.Position;
import dk.aau.d507e19.warehousesim.SimulationApp;
import dk.aau.d507e19.warehousesim.WarehouseSpecs;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;

import java.lang.Math;
import java.util.ArrayList;
import java.util.List;

public class RRTPlanner {

    public Node<Position> shortestLengthNode;
    private Node<Position> root;

    public List<GridCoordinate> generateRRTPath(Robot robot, Position destination) {
        boolean hasRoute = false;
        root = new Node<Position>(robot.getCurrentPosition(), null);
        //Run until a route is found
        while (!hasRoute) {
            for (int i = 0; i < 1; i++) {
                growRRT(root);
            }
            //traverse tree to see if destination is reachable
            hasRoute = root.containsData(destination);
        }
        //Find Destination Node
        Node<Position> destNode = findDestinationNode(root,destination);
        //Find parents and make list of coords
        return makePath(destNode);
    }

    private List<GridCoordinate> makePath(Node<Position> destNode){
        List<GridCoordinate> path = new ArrayList<>();
        if(destNode.getParent() == null){
            path.add(new GridCoordinate((int) destNode.getData().getX(),(int) destNode.getData().getY()));
            return path;
        }
        path = makePath(destNode.getParent());
        path.add(new GridCoordinate((int) destNode.getData().getX(), (int) destNode.getData().getY()));
        return path;
    }

    private Node<Position> findDestinationNode(Node<Position> root, Position destination){
        for( Node<Position> n : root.getChildren()){
            if(n.getData().equals(destination)){
                return n;
            }
            findDestinationNode(n,destination);
        }
        throw new RuntimeException("Destination node does not exist");
    }

    private Node<Position> growRRT(Node<Position> tree) {
        //Generate a new random location using seeded random
        Position randPos = generateRandomPos();
        shortestLengthNode = tree;
        Node<Position> nearest = findNearestNeighbour(tree, randPos);
        Node<Position> newNode = generateNewNode(nearest, randPos);
        nearest.addChild(newNode);
        return tree;
    }

    public Node<Position> findNearestNeighbour(Node<Position> tree, Position randPos) {
        for (Node<Position> n : tree.getChildren()) {
            double newDistance = getDistanceBetweenPoints(n.getData(), randPos);

            if (newDistance < getDistanceBetweenPoints(shortestLengthNode.getData(), randPos)) {
                shortestLengthNode = n;
            }
            findNearestNeighbour(n, randPos);
        }
        return shortestLengthNode;
    }

    private Node<Position> generateNewNode(Node<Position> nearest, Position randPos) {
        Position pos = nearest.getData();
        //right
        pos = getDistanceBetweenPoints(new Position(pos.getX() + 1, pos.getY()), randPos) < getDistanceBetweenPoints(pos, randPos) ? new Position(pos.getX() + 1, pos.getY()) : pos;
        //left
        pos = getDistanceBetweenPoints(new Position(pos.getX() - 1, pos.getY()), randPos) < getDistanceBetweenPoints(pos, randPos) ? new Position(pos.getX() + 1, pos.getY()) : pos;
        //up
        pos = getDistanceBetweenPoints(new Position(pos.getX(), pos.getY() + 1), randPos) < getDistanceBetweenPoints(pos, randPos) ? new Position(pos.getX() + 1, pos.getY()) : pos;
        //down
        pos = getDistanceBetweenPoints(new Position(pos.getX(), pos.getY() - 1), randPos) < getDistanceBetweenPoints(pos, randPos) ? new Position(pos.getX() + 1, pos.getY()) : pos;

        return new Node<Position>(pos, null);
    }

    private Position generateRandomPos() {
        //TODO possible infinite loop if there is a node on every tile
        Position randPos;
        do {
            randPos = new Position(
                    SimulationApp.random.nextInt(WarehouseSpecs.wareHouseWidth),
                    SimulationApp.random.nextInt(WarehouseSpecs.wareHouseHeight));
            //System.out.println("X: " + randPos.getX() + " Y: " + randPos.getY());
        } while (doesNodeExist(randPos));

        System.out.println("X: " + randPos.getX() + " Y: " + randPos.getY());

        return randPos;
    }

    private double getDistanceBetweenPoints(Position pos1, Position pos2) {
        return Math.sqrt(Math.pow(pos2.getX() - pos1.getX(), 2) + Math.pow(pos2.getY() - pos1.getY(), 2));
    }

    private boolean doesNodeExist(Position newPos) {
        return root.containsData(newPos);
    }
}
