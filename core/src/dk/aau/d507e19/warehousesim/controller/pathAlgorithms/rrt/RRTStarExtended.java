package dk.aau.d507e19.warehousesim.controller.pathAlgorithms.rrt;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.RobotController;
import java.util.ArrayList;

public class RRTStarExtended extends RRTStar {
    String currentDirection = "Still";
    public RRTStarExtended(RobotController robotController) {
        super(robotController);
    }


    @Override
    public void attemptOptimise(){
        //optimise with distance first
        //then optimise with time
        optimalPathOptimise(destinationNode);
        path = makePath(destinationNode);
    }
    private boolean hasBetterParent(Node<GridCoordinate> node){
        for(Node<GridCoordinate> n: trimImprovementsList(findNodesInRadius(node.getData(),1),node.getData())){
            if(node.getParent().equals(n)){
                if(distance(node.getParent().getData(),root.getData()) < distance(node.getData(),root.getData())){
                    return true;
                }
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
        ArrayList<Node<GridCoordinate>> bestNeighbours = new ArrayList<>();
        Node<GridCoordinate> bestNeighbour=null;
        if(neighbours.contains(root)){
            node.setParent(root);
            return true;
        }
        //Find all neighbours that could be good
        for(Node<GridCoordinate> neighbour : neighbours) {
            if (!canBeRewired(neighbour, node)) {
                if(!hasBetterParent(neighbour)){
                    continue;
                }

            }
            //if n to root is the same direction as neighbour to root i.e we dont have to turn add it to potential neighbours list
            if(calcDirection(node.getData(),root.getData()).equals(calcDirection(neighbour.getData(),root.getData()))){
                bestNeighbours.add(neighbour);
            }
            //if neighbour is on the same x or y axis as root, we should add it as well
            else if(neighbour.getData().getX() == root.getData().getX() ||neighbour.getData().getY() == root.getData().getY()){
                bestNeighbours.add(neighbour);
            }
        }
        if(!bestNeighbours.isEmpty()){
            //find best of best
            //if we have one on the same x or y axis as root, then thats the best
            for(Node<GridCoordinate> candidate : bestNeighbours){
                if(candidate.getData().getX() == root.getData().getX() || candidate.getData().getY() == root.getData().getY()){
                    //we might have two of these so we check to make sure we get the closest one
                    if(bestNeighbour==null||distance(candidate.getData(),root.getData()) < distance(bestNeighbour.getData(),root.getData())){
                        bestNeighbour = candidate;
                    }
                }
            }
            if(bestNeighbour==null){
                //if its null, that means that we did not have any neighbours that were on the same axis as root. In this case we pick the one thats closest to root
                for(Node<GridCoordinate> candidate : bestNeighbours){
                    if(bestNeighbour==null || distance(candidate.getData(),root.getData()) <= distance(bestNeighbour.getData(),root.getData())){
                        if(bestNeighbour == null){
                            bestNeighbour = candidate;
                        }else if(distance(candidate.getData(),root.getData()) == distance(bestNeighbour.getData(),root.getData())){
                            //we should check that the direction from nodes child in the path to node is the same as node to candidate
                            //This way we ensure that we always choose the straight paths
                            if(currentDirection.equals(calcDirection(node.getData(),candidate.getData()))){
                                bestNeighbour=candidate;
                            }
                        }else bestNeighbour = candidate;

                    }
                }
            }
        }
        if(bestNeighbour!=null){
            currentDirection = calcDirection(node.getData(),bestNeighbour.getData());
            node.setParent(bestNeighbour);
            return optimalPathOptimise(bestNeighbour);
        }
        return false;
    }
    /**
     * Overridden to ensure tree is fully grown as the optimise assumes that tree is fully grown
     * */
    @Override
    protected void growUntilPathFound(GridCoordinate destination) {
        //grow tree fully to ensure perfect paths todo change to only generate within bounds
        growUntilFullyExplored();
    }
}
