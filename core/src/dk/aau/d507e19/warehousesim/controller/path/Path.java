package dk.aau.d507e19.warehousesim.controller.path;

import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;

import java.util.ArrayList;

public class Path {

    ArrayList<Step> strippedSteps = new ArrayList<>();
    ArrayList<Step> allSteps = new ArrayList<>();

    public Path(ArrayList<Step> pathToTarget) {
        if(pathToTarget.isEmpty())
            throw new IllegalArgumentException("Path must contain at least one coordinate");
        if(!isValidPath(pathToTarget))
            throw new IllegalArgumentException("Paths must be continuous" + stepsToString(pathToTarget));
        this.strippedSteps = collapseWaitingSteps(pathToTarget);

        allSteps.addAll(strippedSteps);



        strippedSteps = generateStrippedPath(allSteps);
    }

    private String stepsToString(ArrayList<Step> pathToTarget) {
        StringBuilder stringBuilder = new StringBuilder();
        for(Step step : pathToTarget){
            stringBuilder.append(step.toString());
        }
        return stringBuilder.toString();
    }

    private ArrayList<Step> collapseWaitingSteps(ArrayList<Step> pathToTarget) {
        ArrayList<Step> collapsedSteps = new ArrayList<>();

        // Remove first step if immediately followed by waiting step
        if(pathToTarget.size() > 1 && pathToTarget.get(1).isWaitingStep()) {
            pathToTarget.remove(0);
        }

        Step previousStep = pathToTarget.get(0);
        for(int i = 1; i < pathToTarget.size(); i++){
            Step currentStep = pathToTarget.get(i);
            if(previousStep.isWaitingStep() && currentStep.isWaitingStep()){
                previousStep = combineWaitingSteps(previousStep, currentStep);
                continue;
            }else{
                collapsedSteps.add(previousStep);
            }

            previousStep = currentStep;
        }

        // Add last step
        collapsedSteps.add(previousStep);

        return collapsedSteps;
    }

    private Step combineWaitingSteps(Step previousStep, Step currentStep) {
        if(!previousStep.getGridCoordinate().equals(currentStep.getGridCoordinate()))
            throw new IllegalArgumentException("Cannot combine waiting steps because they do not have the same coordinate");

        return new Step(previousStep.getGridCoordinate(), previousStep.getWaitTimeInTicks() +
                currentStep.getWaitTimeInTicks());
    }


    private static ArrayList<Step> generateStrippedPath(ArrayList<Step> allSteps) {
        ArrayList<Step> strippedSteps = new ArrayList<>();

        // The first step should always be included
        strippedSteps.add(allSteps.get(0));

        for(int i = 1; i < allSteps.size(); i++){
            if(isStoppingPoint(allSteps, i))
                strippedSteps.add(allSteps.get(i));
        }

        return strippedSteps;
    }

    private static boolean isStoppingPoint(ArrayList<Step> allSteps, int index) {
        if(index == 0 || index == allSteps.size() - 1) return true; // First & last steps are always stopping points

        Step currentStep = allSteps.get(index);
        if(currentStep.isWaitingStep()) return true;

        Step previousStep = allSteps.get(index - 1);
        Step nextStep = allSteps.get(index + 1);

        // If next step is the same, then that step is a waiting step; and that will be considered a stopping point
        // instead of this one
        if(nextStep.getGridCoordinate().equals(currentStep.getGridCoordinate()))
            return false;

        // Otherwise check if the currentStep is a corner
        Line fromPrevToCurrent = new Line(previousStep, currentStep);
        Line fromCurrentToNext = new Line(currentStep, nextStep);

        return fromPrevToCurrent.getDirection() != fromCurrentToNext.getDirection();
    }

    public static Path oneStepPath(Step step) {
        ArrayList<Step> steps = new ArrayList<>();
        steps.add(step);
        return new Path(steps);
    }

    public static boolean isValidPath(ArrayList<Step> steps){
        Step currentStep;
        Step previousStep = steps.get(0);

        for (int i = 1; i < steps.size(); i++) {
            currentStep = steps.get(i);

            if(!currentStep.isStepValidContinuationOf(previousStep))
                return false;

            previousStep = currentStep;
        }
        return true;
    }

    public ArrayList<Step> getStrippedPath() {
        return strippedSteps;
    }

    public ArrayList<Step> getFullPath() {
        return allSteps;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("(");

        for(Step step : strippedSteps)
            builder.append( "(" + step.getX() + " : " + step.getY() + ")" + ",");

        builder.append(")");
        return builder.toString();
    }

    public ArrayList<Line> getLines(){
        ArrayList<Line> lines = new ArrayList<>();
        Step currentStep;
        Step previousStep = getStrippedPath().get(0);

        for(int i = 1; i < strippedSteps.size(); i++){
            currentStep = strippedSteps.get(i);
            lines.add(new Line(previousStep, currentStep));
            previousStep = currentStep;
        }

        return lines;
    }

    public static Path join(Path path1, Path path2){
        /*if(!path1.getLastStep().getGridCoordinate().equals(path2.getFirstStep().getGridCoordinate()))
            throw new IllegalArgumentException("End of first path must be start of seconds path");*/

        ArrayList<Step> newSteps = new ArrayList<>();
        newSteps.addAll(path1.getFullPath());
        newSteps.addAll(path2.getFullPath());

        return new Path(newSteps);
    }


    public Step getLastStep() {
        return allSteps.get(allSteps.size() - 1);
    }
}
