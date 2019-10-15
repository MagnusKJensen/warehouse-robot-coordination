package dk.aau.d507e19.warehousesim.controller.robot.plan;

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

        // Add a pause action if the first Step in the path is a waiting step
        addStartAction();

        // Create a pause action for each pause step
        // Create a LineTraversal action for each line the path
        Step currentStep = strippedSteps.get(0);
        Step previousStep = currentStep;
        for(int i = 1; i < strippedSteps.size(); i++){
            currentStep = strippedSteps.get(i);
            if(currentStep.isWaitingStep())
                actions.add(new Pause(currentStep.getWaitTimeInTicks(), robot));
            else
                actions.add(createLineTraversal(previousStep, currentStep));
            previousStep = currentStep;
        }
    }

    private void addStartAction() {
        Step firstStep = path.getStrippedPath().get(0);
        // Add pause if the first step is a waiting step
        if(firstStep.isWaitingStep())
            actions.add(new Pause(firstStep.getWaitTimeInTicks(), robot));
    }

    private Action createLineTraversal(Step previousStep, Step currentStep){
        return new LineTraversal(robot, previousStep.getGridCoordinate(), currentStep.getGridCoordinate());
    }

    @Override
    public void perform() {
        if(isDone())
            throw new IllegalStateException("Attempted to perform action that was already done");

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
