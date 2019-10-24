package dk.aau.d507e19.warehousesim.controller.robot.plan;

import dk.aau.d507e19.warehousesim.controller.path.Line;
import dk.aau.d507e19.warehousesim.controller.path.Path;
import dk.aau.d507e19.warehousesim.controller.path.Step;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.robot.Status;

import java.io.IOException;
import java.util.ArrayList;

public class PathTraversal implements Action{

    private Robot robot;
    private Path path;

    private ArrayList<Action> actions = new ArrayList<>();

    public PathTraversal(Robot robot, Path path) {
        this.robot = robot;
        this.path = path;
        planActions();
    }

    // Creates a list of actions from the path
    private void planActions() {
        ArrayList<Step> strippedSteps = path.getStrippedPath();
        actions = new ArrayList<>();

        // Create a pause action for each pause step
        // Create a LineTraversal action for each line the path
        Step currentStep = strippedSteps.get(0);
        Step previousStep = currentStep;
        for(int i = 1; i < strippedSteps.size(); i++){
            currentStep = strippedSteps.get(i);
            actions.add(createLineTraversal(previousStep, currentStep));
            previousStep = currentStep;
        }
    }

    private Action createLineTraversal(Step previousStep, Step currentStep){
        return new LineTraversal(robot, new Line(previousStep, currentStep));
    }

    @Override
    public void perform() {
        if(isDone())
            throw new IllegalStateException("Attempted to traverse path that has already finished traversing");

        Action currentAction = actions.get(0);
        currentAction.perform();
        if(currentAction.isDone())
            actions.remove(currentAction);
    }

    @Override
    public boolean isDone() {
        return actions.isEmpty();
    }

    public ArrayList<Action> getRemainingActions(){
        return actions;
    }

    @Override
    public Status getStatus() {
        if(robot.isCarrying()) return Status.TASK_ASSIGNED_CARRYING;
        else return Status.BUSY;
    }
}
