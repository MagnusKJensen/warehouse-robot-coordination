package dk.aau.d507e19.warehousesim.controller.robot.plan;

import dk.aau.d507e19.warehousesim.controller.robot.RobotController;
import java.util.ArrayList;

public class RobotControllerNode {

    private final RobotController robotController;
    private ArrayList<RobotControllerNode> children = new ArrayList<>();
    private boolean exhausted = false;
    private RobotControllerNode parent;

    public RobotControllerNode(RobotController robotController) {
        this.robotController = robotController;
    }

    public RobotControllerNode(RobotController robotController, RobotControllerNode parent) {
        this.robotController = robotController;
        this.parent = parent;
    }

    public void addChild(RobotControllerNode child){
        children.add(child);
        // If a non-exhausted child is added, this branch is no longer exhausted
        if(!child.isExhausted())
            setExhausted(false);
    }

    public boolean isExhausted() {
        return exhausted;
    }

    public void setExhausted(boolean exhausted) {
        this.exhausted = exhausted;
        if(hasParent()) parent.checkIfExhausted();
    }

    private void checkIfExhausted() {
        for(RobotControllerNode child : children)
            if(!child.isExhausted()) return;

        setExhausted(true);
        if(hasParent()) parent.checkIfExhausted();
    }

    public ArrayList<RobotControllerNode> getNonExhaustedLeafNodes(){
        ArrayList<RobotControllerNode> leaves = new ArrayList<>();

        if(exhausted) // This branch is exhausted return no leaves
            return leaves;

        if(children.size() == 0){
            // This is a leaf node
            leaves.add(this);
            return leaves;
        }

        for(RobotControllerNode child : children)
            leaves.addAll(child.getNonExhaustedLeafNodes());

        // This branch has been exhausted if it has no non-exhausted leaves
        if(leaves.isEmpty())
            setExhausted(true);

        return leaves;
    }

    public RobotController getRobotController() {
        return robotController;
    }

    public boolean hasParent() {
        return parent != null;
    }

    public RobotControllerNode getParent() {
        return parent;
    }

    public int getTotalNodeCount() {
        int childCount = 0;
        for(RobotControllerNode child : children){
            childCount++;
            childCount += child.getTotalNodeCount();
        }

        return childCount;
    }
}
