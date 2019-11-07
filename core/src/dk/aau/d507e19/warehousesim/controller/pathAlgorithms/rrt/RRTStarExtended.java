package dk.aau.d507e19.warehousesim.controller.pathAlgorithms.rrt;

import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.RobotController;

public class RRTStarExtended extends RRTStar {
    public RRTStarExtended(RobotController robotController) {
        super(robotController);
    }

    @Override
    public void attemptOptimise(){
        smartOptimise(destinationNode);
        path = makePath(destinationNode);
        updateAllNodes(root);
    }

    public void smartOptimise(Node<GridCoordinate> node){
        if (node.getParent() != null) {
            for(Node<GridCoordinate> n : trimImprovementsList(findNodesInRadius(node.getData(),1),node.getData())){
                if(n.equals(node.getParent()) || node.getParent()==root){
                    continue;
                }
                if(n.equals(root)){
                    node.setParent(n);
                }else{
                    if(cost(n) < cost(node.getParent()) ){
                        node.setParent(n);
                    }
                }
            }
            smartOptimise(node.getParent());
        }
    }

}
