package dk.aau.d507e19.warehousesim.controller.path;

import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;

public class Step {

    private final GridCoordinate gridCoordinate;
    private StepType stepType;

    public enum StepType {
        WAITING_STEP, MOVEMENT_STEP;
    }

    public Step(GridCoordinate gridCoordinate, long waitTimeInTicks){
        StepType stepType = StepType.WAITING_STEP;
        this.gridCoordinate = gridCoordinate;
    }

    public Step(GridCoordinate gridCoordinate){
        this.stepType = StepType.MOVEMENT_STEP;
        this.gridCoordinate = gridCoordinate;

    }

    public Step(int x, int y){
        this(new GridCoordinate(x, y));
    }

    public Step(int x, int y, long waitTimeInTicks){
        this(new GridCoordinate(x, y), waitTimeInTicks);
    }

    public boolean isStepValidContinuationOf(Step previousStep){
        if(previousStep == null) return true;

        if(this.stepType == StepType.WAITING_STEP){
            boolean isPreviousStepMovementStep = previousStep.stepType == StepType.MOVEMENT_STEP;
            boolean hasSameCoords = this.gridCoordinate.equals(previousStep.gridCoordinate);
            return isPreviousStepMovementStep && hasSameCoords;
        }else{
            return this.gridCoordinate.isNeighbourOf(previousStep.gridCoordinate);
        }
    }

    public GridCoordinate getGridCoordinate(){
        return gridCoordinate;
    }

}
