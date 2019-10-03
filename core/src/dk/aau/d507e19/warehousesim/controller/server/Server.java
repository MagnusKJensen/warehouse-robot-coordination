package dk.aau.d507e19.warehousesim.controller.server;

import dk.aau.d507e19.warehousesim.Simulation;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;

import java.util.ArrayList;

public class Server {

    private Simulation simulation;

    public Server(Simulation simulation){
        this.simulation = simulation;
    }

    public ArrayList<Robot> getAllRobots(){
        return simulation.getAllRobots();
    }

}
