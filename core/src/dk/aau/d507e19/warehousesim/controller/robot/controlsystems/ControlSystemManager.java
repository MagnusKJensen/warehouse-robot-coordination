package dk.aau.d507e19.warehousesim.controller.robot.controlsystems;

import dk.aau.d507e19.warehousesim.controller.robot.RobotController;

import java.util.ArrayList;

public class ControlSystemManager {
    RobotController robotController;
    ArrayList<Sensor> sensors = new ArrayList<>();

    public ControlSystemManager(RobotController robotController) {
        this.robotController = robotController;
        initSensors();
    }
    private void initSensors(){
        //init our sensors
        sensors.add(new MechanicalSensor(SensorState.NOMINAL,robotController));
        sensors.add(new Battery(SensorState.NOMINAL,robotController));
    }

    public void checkSystem(){
        //todo @bau consider making the checks happen less frequently in here instead in the sensors
        //if battery is dry then we dont do anything
        if(!this.getBattery().isBatteryDry()){
         for(Sensor s : sensors){
                s.update();
                if(s.getState() == SensorState.FAILURE){
                    s.handleFailure();
                }
            }
        }
    }
    public Battery getBattery(){
        for(Sensor s : sensors){
            if(s instanceof Battery){
                return (Battery) s;
            }
        }
        throw new RuntimeException("ERROR: Battery not found in sensors");
    }
    public MechanicalSensor getMechanicalSensor(){
        for(Sensor s : sensors){
            if(s instanceof MechanicalSensor){
                return (MechanicalSensor) s;
            }
        }
        throw new RuntimeException("ERROR: MechanicalSensor not found in sensors");
    }
}
