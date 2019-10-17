package dk.aau.d507e19.warehousesim.controller.path;

import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.plan.Pause;

import java.util.ArrayList;

public class Path {

    ArrayList<Step> strippedSteps = new ArrayList<>();
    ArrayList<Step> allSteps = new ArrayList<>();

    public Path(ArrayList<Step> pathToTarget) {
        this.strippedSteps = pathToTarget;
        allSteps.addAll(strippedSteps);
        if(pathToTarget.isEmpty()) throw new IllegalArgumentException("Path must contain at least one coordinate");
        //if(!isValidPath()) throw new IllegalArgumentException("Paths must be continuous");
        removeAllButCorners();
    }

    private void removeAllButCorners() {
        ArrayList<Step> corners = new ArrayList<>();

        boolean xChanged;
        boolean yChanged;
        String lastDirection;

        // Add start position
        corners.add(strippedSteps.get(0));

        if(strippedSteps.size() > 1){
            // If x changed
            if(strippedSteps.get(0).getX() != strippedSteps.get(1).getX()) lastDirection = "x";
            else lastDirection = "y"; // if y changed

            for (int i = 1; i < strippedSteps.size(); i++) {
                xChanged = strippedSteps.get(i - 1).getX() != strippedSteps.get(i).getX();
                yChanged = strippedSteps.get(i - 1).getY() != strippedSteps.get(i).getY();

                // Check if turned around. It cannot turn around with only 2 moves.
                if(i > 2){
                    // If turning around the same way in x direction
                    if(strippedSteps.get(i).getX() == strippedSteps.get(i - 2).getX()
                            && strippedSteps.get(i).getY() == strippedSteps.get(i - 2).getY()){
                        corners.add(strippedSteps.get(i - 1));
                        lastDirection = "x";
                        continue;
                    }

                    // If turning around the same way in y direction
                    if(strippedSteps.get(i).getY() == strippedSteps.get(i - 2).getY()
                            && strippedSteps.get(i).getX() == strippedSteps.get(i - 2).getX()){
                        corners.add(strippedSteps.get(i - 1));
                        lastDirection = "y";
                        continue;
                    }
                }

                if(xChanged && lastDirection.equals("y")){
                    corners.add(strippedSteps.get(i - 1));
                    lastDirection = "x";
                    continue;
                }

                if(yChanged && lastDirection.equals("x")){
                    corners.add(strippedSteps.get(i - 1));
                    lastDirection = "y";

                }

            }
            // add last target
            corners.add(strippedSteps.get(strippedSteps.size() -1));
        }

        strippedSteps = corners;
    }

    public boolean isValidPath(){
        for (int i = 0; i < allSteps.size() - 1; i++) {
            // If moving along the x axis
            if((Math.abs(allSteps.get(i).getX() - allSteps.get(i + 1).getX()) == 1)
                    && allSteps.get(i).getY() == allSteps.get(i + 1).getY()){
                continue;
            }
            // If moving along the y axis
            if((Math.abs(allSteps.get(i).getY() - allSteps.get(i + 1).getY()) == 1)
                    && allSteps.get(i).getX() == allSteps.get(i + 1).getX()){
                continue;
            }

            System.err.println("------------------- PATH INVALID -------------------");
            System.err.println("Noncontinuous coordinates : " + allSteps.get(i) + " to " + allSteps.get(i + 1));
            System.err.println("Full Path : " + allSteps);
            return false;
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
