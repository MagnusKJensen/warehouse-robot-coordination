package dk.aau.d507e19.warehousesim.controller.pathAlgorithms.rrt;

import dk.aau.d507e19.warehousesim.Simulation;
import dk.aau.d507e19.warehousesim.WarehouseSpecs;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.RobotController;

import java.util.ArrayList;

public class RRTStarExtended extends RRTStar {
    String currentDirection = "Still";

    public RRTStarExtended(RobotController robotController) {
        super(robotController);
    }


    @Override
    public void attemptOptimise() {
        if(allNodesMap.size()!= Simulation.getWarehouseSpecs().wareHouseWidth * Simulation.getWarehouseSpecs().wareHouseHeight){
            //todo handle more elegantly
            throw new RuntimeException("Can not use rrt*extended optimise if tree is not fully grown");
        }
        //optimise with distance first
        //then optimise with time
        optimalPathOptimise(destinationNode);
        path = makePath(destinationNode);
    }

    private boolean hasBetterParent(Node<GridCoordinate> node) {
        for (Node<GridCoordinate> n : trimImprovementsList(findNodesInRadius(node.getData(), 1), node.getData())) {
            if (node.getParent().equals(n)) {
                if (distance(node.getParent().getData(), root.getData()) < distance(node.getData(), root.getData())) {
                    return true;
                }
                continue;
            }
            if (distance(n.getData(), root.getData()) < distance(node.getParent().getData(), root.getData())) {
                return true;
            }
        }
        return false;
    }

    public boolean optimalPathOptimise(Node<GridCoordinate> node) {
        if (node.getData().equals(new GridCoordinate(22, 6)) && destinationNode.getData().equals(new GridCoordinate(22, 10))) {
            System.out.println();
        }
        ArrayList<Node<GridCoordinate>> neighbours = trimImprovementsList(findNodesInRadius(node.getData(), 1), node.getData());
        ArrayList<Node<GridCoordinate>> bestNeighbours = new ArrayList<>();
        Node<GridCoordinate> bestNeighbour = null;
        if (neighbours.contains(root)) {
            node.setParent(root);
            return true;
        }
        //Find all neighbours that could be good
        for (Node<GridCoordinate> neighbour : neighbours) {
            if (!canBeRewired(neighbour, node)) {
                if (!hasBetterParent(neighbour)) {
                    continue;
                }

            }
            //if n to root is the same direction as neighbour to root i.e we dont have to turn add it to potential neighbours list
            if (calcDirection(node.getData(), root.getData()).equals(calcDirection(neighbour.getData(), root.getData()))) {
                bestNeighbours.add(neighbour);
            }
            //if neighbour is on the same x or y axis as root, we should add it as well
            else if (neighbour.getData().getX() == root.getData().getX() || neighbour.getData().getY() == root.getData().getY()) {
                bestNeighbours.add(neighbour);
            }
        }
        if (!bestNeighbours.isEmpty()) {
            //find best of best
            //if we have one on the same x or y axis as root, then thats the best
            for (Node<GridCoordinate> candidate : bestNeighbours) {
                if (candidate.getData().getX() == root.getData().getX() || candidate.getData().getY() == root.getData().getY()) {
                    //we might have two of these so we check to make sure we get the closest one
                    if (bestNeighbour == null || distance(candidate.getData(), root.getData()) < distance(bestNeighbour.getData(), root.getData())) {
                        bestNeighbour = candidate;
                    }
                }
            }
            if (bestNeighbour == null) {
                //if its null, that means that we did not have any neighbours that were on the same axis as root. In this case we pick the one thats closest to root
                for (Node<GridCoordinate> candidate : bestNeighbours) {
                    //if bestneighbour is null we just set it as our current candidate
                    if (bestNeighbour == null) {
                        bestNeighbour = candidate;
                    //if node == destinationNode, then we need to prioritise closest distance, else we always prioritise a straight path
                    } else if (node.equals(destinationNode)) {
                        if (distance(candidate.getData(), root.getData()) <= distance(bestNeighbour.getData(), root.getData())) {
                            bestNeighbour = candidate;
                        }
                    //if our current direction is the same as the one we would get from connecting to candidate, then bestNeigbour = candidate (to get straight paths)
                    } else if (currentDirection.equals(calcDirection(node.getData(), candidate.getData()))) {
                        bestNeighbour = candidate;
                    }
                }
            }
        }
        if (bestNeighbour != null) {
            currentDirection = calcDirection(node.getData(), bestNeighbour.getData());
            node.setParent(bestNeighbour);
            return optimalPathOptimise(bestNeighbour);
        }
        return false;
    }

    /**
     * Overridden to ensure tree is fully grown as the optimise assumes that tree is fully grown
     * todo make the naming better
     */
    @Override
    protected void growUntilPathFound(GridCoordinate destination) {
        //grow tree fully to ensure perfect paths todo change to only generate within bounds
        growUntilFullyExplored();
    }
}
