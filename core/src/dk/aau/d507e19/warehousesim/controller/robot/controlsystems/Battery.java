package dk.aau.d507e19.warehousesim.controller.robot.controlsystems;

import dk.aau.d507e19.warehousesim.controller.robot.RobotController;

public class Battery extends Sensor  {
    //todo better naming of fields
    private double batteryLevel = 100, secondsSinceStartedCharge= 0;
    private boolean isCharging = false;

    public Battery(SensorState state, RobotController robotController) {
        //todo @bau should batteries be able to initialize with dynamic battery levels?
        super(state, robotController);
    }

    @Override
    public void update() {
        //if state = maintenance we're charging
        switch (this.getState()){
            case NOMINAL: drain(); break;
            case FAILURE: break;
            case MAINTENANCE: charge(); break;
            default: throw new RuntimeException("no case for " + this.getState());
        }
    }

    private void drain(){
        //todo implement drain battery
    }

    private void charge(){
        //todo implement charge
        if(isCharging){
            //charge
        }
    }

    @Override
    public void handleFailure() {
        //if we have state failure then we're low on battery and should move to a charging station
        this.robotController.moveToChargingStation();
    }
}
