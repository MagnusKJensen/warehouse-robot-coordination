package dk.aau.d507e19.warehousesim.controller.robot.controlsystems;

import dk.aau.d507e19.warehousesim.SimulationApp;
import dk.aau.d507e19.warehousesim.controller.robot.RobotController;
import dk.aau.d507e19.warehousesim.controller.robot.Status;
import dk.aau.d507e19.warehousesim.controller.robot.plan.task.Charging;
import dk.aau.d507e19.warehousesim.controller.robot.plan.task.OutOfBattery;

public class Battery extends Sensor  {
    private double batteryLevel = 0;
    //todo @bau move to WarehouseSpecs?
    final private double chargeThreshold = 20.1;
    final private double drainPerSecond = 100.0/72000.0; //run out after 20hrs of usage
    final private double drainPerTick = drainPerSecond/SimulationApp.TICKS_PER_SECOND;
    final private int drainFactor = 200;
    private boolean createdTask = false;
    private boolean batteryDry = false;

    public boolean isBatteryDry() {
        return batteryDry;
    }

    public Battery(SensorState state, RobotController robotController) {
        //todo @bau should batteries be able to initialize with dynamic battery levels?
        super(state, robotController);
    }

    @Override
    public void update() {
        switch (this.getState()){
            case NOMINAL: drain(); break;
            case FAILURE: if(isCharging()){
                setState(SensorState.MAINTENANCE);
            } break;
            case MAINTENANCE: charge(); break;
            default: throw new RuntimeException("no case for " + this.getState());
        }
    }

    private void drain(){
        if(batteryLevel >= chargeThreshold){
            if(this.robotController.getRobot().getCurrentStatus().equals(Status.AVAILABLE)){
                //robot is idle therefore less energy is used
                batteryLevel = batteryLevel - drainPerTick;
            }
            else{
                //dont drain if we're in maintenance mode
                if(!this.robotController.getRobot().getCurrentStatus().equals(Status.MAINTENANCE)){
                    batteryLevel=batteryLevel-drainPerTick*drainFactor;
                }
            }
        }else{
            if(batteryLevel <= 0){
                batteryDead();
            }else{
                setState(SensorState.FAILURE);
            }
        }
    }
    private void charge(){
        if(isCharging()){
            //adds 0.01% per tick(way too much in reality, but decent for testing - should maybe be included in warehouse specs?
            batteryLevel = batteryLevel+0.01;
            if(batteryLevel >= 100){
                batteryLevel = 100;
                setState(SensorState.NOMINAL);
                createdTask=false;
            }
        }else{
            //the robot waiting for a free charging spot so sensors state is maintenance,
            // but we're not charging. We should still drain battery but at an idle level
            batteryLevel = batteryLevel - drainPerTick;
            //might run out of battery when idle
            if(batteryLevel <= 0){
                batteryDead();
            }
        }
    }

    @Override
    public void handleFailure() {
        //if we have state failure then we're low on battery and should move to a charging station
        //assign the task, but let other tasks finish first
        //only do this if we're not already charging
        if(batteryDry){
            return;
        }
        if(!this.robotController.getRobot().getCurrentStatus().equals(Status.CHARGING) && !createdTask){
            this.robotController.assignTask(new Charging(this.robotController));
            createdTask=true;
        }
    }
    private boolean isCharging(){
        return this.robotController.isCharging();
    }
    private void batteryDead(){
        setState(SensorState.FAILURE);
        batteryDry = true;
        this.robotController.assignImmediateTask(new OutOfBattery(this.robotController));
    }
}
