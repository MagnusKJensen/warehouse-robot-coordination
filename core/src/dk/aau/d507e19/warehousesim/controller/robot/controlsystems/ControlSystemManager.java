package dk.aau.d507e19.warehousesim.controller.robot.controlsystems;

import java.util.ArrayList;

public class ControlSystemManager {
    ArrayList<Sensor> sensors = new ArrayList<>();

    public ControlSystemManager() {
        initSensors();
    }
    private void initSensors(){
        //init our sensors
    }

    public void checkSystem(){
        for(Sensor s : sensors){
            if(s.getState() == SensorState.FAILURE){
                //todo should maybe take RobotController
                s.handleFailure();
            }
        }
    }
}
