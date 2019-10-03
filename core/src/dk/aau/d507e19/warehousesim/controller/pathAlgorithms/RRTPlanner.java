package dk.aau.d507e19.warehousesim.controller.pathAlgorithms;
import dk.aau.d507e19.warehousesim.SimulationApp;
import dk.aau.d507e19.warehousesim.WarehouseSpecs;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.Node;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;

import java.lang.Math;
import java.util.ArrayList;
import java.util.List;

public class RRTPlanner {

    public Node<GridCoordinate> shortestLengthNode;
    private Node<GridCoordinate> root, destinationNode;
    private GridCoordinate dest;
    private boolean foundPath;

    public List<GridCoordinate> generateRRTPath(Robot robot, GridCoordinate destination) throws InterruptedException {
        dest = destination;
        boolean hasRoute = false;
        root = new Node<GridCoordinate>(new GridCoordinate((int)robot.getCurrentPosition().getX(),(int)robot.getCurrentPosition().getY()), null);
        //Run until a route is found
        while (!foundPath) {
            for (int i = 0; i < 1; i++) {
                //  root.printTree(root);
                // System.out.println("END");
                growRRT(root);
            }
        }
        //Find parents and make list of coords
        return makePath(destinationNode);
    }

    private List<GridCoordinate> makePath(Node<GridCoordinate> destNode){
        List<GridCoordinate> path = new ArrayList<>();
        if(destNode.getParent() == null){
            path.add(new GridCoordinate((int) destNode.getData().getX(),(int) destNode.getData().getY()));
            return path;
        }
        path = makePath(destNode.getParent());
        path.add(new GridCoordinate((int) destNode.getData().getX(), (int) destNode.getData().getY()));
        return path;
    }

    private void growRRT(Node<GridCoordinate> tree) {
        //Generate a new random location using seeded random
        GridCoordinate randPos = generateRandomPos();
        shortestLengthNode = tree;
        Node<GridCoordinate> nearest = findNearestNeighbour(tree, randPos);
        Node<GridCoordinate> newNode = generateNewNode(nearest, randPos);
        nearest.addChild(newNode);
        if(newNode.getData().equals(dest)){
            foundPath = true;
            destinationNode = newNode;
        }
    }

    public Node<GridCoordinate> findNearestNeighbour(Node<GridCoordinate> tree, GridCoordinate randPos) {
        for (Node<GridCoordinate> n : tree.getChildren()) {
            double newDistance = getDistanceBetweenPoints(n.getData(), randPos);

            if (newDistance < getDistanceBetweenPoints(shortestLengthNode.getData(), randPos)) {
                shortestLengthNode = n;
            }
            findNearestNeighbour(n, randPos);
        }
        return shortestLengthNode;
    }

    private Node<GridCoordinate> generateNewNode(Node<GridCoordinate> nearest, GridCoordinate randPos) {
        GridCoordinate originalPos = nearest.getData();
        GridCoordinate pos = nearest.getData();
        //right
        pos = getDistanceBetweenPoints(new GridCoordinate(pos.getX() + 1, pos.getY()), randPos) < getDistanceBetweenPoints(pos, randPos) ? new GridCoordinate(originalPos.getX() + 1, originalPos.getY()) : pos;
        //left
        pos = getDistanceBetweenPoints(new GridCoordinate(pos.getX() - 1, pos.getY()), randPos) < getDistanceBetweenPoints(pos, randPos) ? new GridCoordinate(originalPos.getX() -1, originalPos.getY()) : pos;
        //up
        pos = getDistanceBetweenPoints(new GridCoordinate(pos.getX(), pos.getY() + 1), randPos) < getDistanceBetweenPoints(pos, randPos) ? new GridCoordinate(originalPos.getX(), originalPos.getY() +1) : pos;
        //down
        pos = getDistanceBetweenPoints(new GridCoordinate(pos.getX(), pos.getY() - 1), randPos) < getDistanceBetweenPoints(pos, randPos) ? new GridCoordinate(originalPos.getX(), originalPos.getY() -1 ) : pos;

        //System.out.println("NEW: "+ pos.toString()+"\nNEAR: " + originalPos.toString() + "\nRAND: " + randPos.toString()+"\n");
        return new Node<>(pos, null);
    }

    private GridCoordinate generateRandomPos() {
        //TODO possible infinite loop if there is a node on every tile
        GridCoordinate randPos;
        do {
            randPos = new GridCoordinate(
                    SimulationApp.random.nextInt(WarehouseSpecs.wareHouseWidth),
                    SimulationApp.random.nextInt(WarehouseSpecs.wareHouseHeight));
        }while(doesNodeExist(randPos));

        return randPos;
    }

    private double getDistanceBetweenPoints(GridCoordinate pos1, GridCoordinate pos2) {
        return Math.sqrt(Math.pow(pos2.getX() - pos1.getX(), 2) + Math.pow(pos2.getY() - pos1.getY(), 2));
    }

    private boolean doesNodeExist(GridCoordinate newPos) {
        /*System.out.println("---TREE---");
        root.printTree(root);
        System.out.println("---TREE END---");*/
        //System.out.println("CHECKING FOR: " + newPos.toString() + "RETURN: " + root.containsNodeWithData(root,newPos));
        return root.containsNodeWithData(root,newPos);
    }
}
