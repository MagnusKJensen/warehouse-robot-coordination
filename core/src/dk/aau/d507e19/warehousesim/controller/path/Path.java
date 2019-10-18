package dk.aau.d507e19.warehousesim.controller.path;

import java.util.ArrayList;

public class Path {

    ArrayList<Step> strippedSteps = new ArrayList<>();
    ArrayList<Step> allSteps = new ArrayList<>();

    public Path(ArrayList<Step> pathToTarget) {
        this.strippedSteps = pathToTarget;
        allSteps.addAll(strippedSteps);
        if(pathToTarget.isEmpty())
            throw new IllegalArgumentException("Path must contain at least one coordinate");
        if(!isValidPath())
            throw new IllegalArgumentException("Paths must be continuous");
        strippedSteps = generateStrippedPath(allSteps);
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
        Line fromPrevToCurrent = new Line(previousStep.getGridCoordinate(), currentStep.getGridCoordinate());
        Line fromCurrentToNext = new Line(currentStep.getGridCoordinate(), nextStep.getGridCoordinate());

        return fromPrevToCurrent.getDirection() != fromCurrentToNext.getDirection();
    }

    public boolean isValidPath(){
        Step currentStep;
        Step previousStep = allSteps.get(0);

        for (int i = 1; i < allSteps.size(); i++) {
            currentStep = allSteps.get(i);

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
            lines.add(new Line(previousStep.getGridCoordinate(), currentStep.getGridCoordinate()));
            previousStep = currentStep;
        }

        return lines;
    }


}
