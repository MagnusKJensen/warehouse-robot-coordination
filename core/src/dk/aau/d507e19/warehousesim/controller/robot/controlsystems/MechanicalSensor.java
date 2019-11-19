package dk.aau.d507e19.warehousesim.controller.robot.controlsystems;
import dk.aau.d507e19.warehousesim.Simulation;
import dk.aau.d507e19.warehousesim.SimulationApp;
import dk.aau.d507e19.warehousesim.controller.robot.RobotController;
import dk.aau.d507e19.warehousesim.controller.robot.Status;
import dk.aau.d507e19.warehousesim.controller.robot.plan.task.EmergencyStop;
import dk.aau.d507e19.warehousesim.controller.robot.plan.task.Maintenance;

import java.util.Random;

public class MechanicalSensor extends Sensor {
    final private int failureCheckFrequency = 30; //checks for error every 30 ticks. if this is 1 then we check every tick
    final private int failureBoundary = 1000; // chance for failure is 1/errorBoundary ie. high number = low chance, low number = high chance
    final private int failureNumber = 3; //if we random this number then there is a failure
    final private long maintenanceTimeInTicks = 10000;
    private long maintenanceStartTick = -1;
    private boolean createdTask=false;

    public MechanicalSensor(SensorState state, RobotController robotController) {
        super(state, robotController);
    }

    @Override
    public void update() {
        switch(this.state){
            case NOMINAL: checkForFailure(); break;
            case FAILURE: if(this.robotController.isUnderMaintenance())this.setState(SensorState.MAINTENANCE); break;
            case MAINTENANCE: undergoMaintenance(); break;
            default: throw new RuntimeException("unknown state: "+ this.state);
        }
    }
    private void checkForFailure(){
        //avoid checking at tick 0?
        if(this.robotController.getServer().getTimeInTicks() % failureCheckFrequency == 0 && !this.robotController.isCharging()){
            if(failureNumber == random.nextInt(failureBoundary)){
                this.setState(SensorState.FAILURE);
            }
        }
    }
    private void undergoMaintenance(){
        //how long should maintenance take?
        if(this.robotController.isUnderMaintenance()){
            if(maintenanceStartTick == -1){
                maintenanceStartTick = this.robotController.getServer().getTimeInTicks();
            }else if(this.robotController.getServer().getTimeInTicks() >= maintenanceStartTick + maintenanceTimeInTicks){
                this.setState(SensorState.NOMINAL);
                createdTask=false;
            }
        }
    }

    @Override
    public void handleFailure() {
        //todo @bau read comment and implement
        //if we encounter a mechanical failure then ideally we should drive straight to maintenance, in practice however
        //this means that we might hold a bin while this is happening, making that task take WAY too long
        //for now we're just going to wait, but ideally another robot will come and pick up the bin/order from us before we are allowed to move
        if(!this.robotController.getRobot().getCurrentStatus().equals(Status.MAINTENANCE) && !createdTask){
            this.robotController.assignImmediateTask(new Maintenance(this.robotController));
            createdTask = true;
        }
    }
}
