package dk.aau.d507e19.warehousesim.controller.server;

import dk.aau.d507e19.warehousesim.Simulation;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;

import java.util.ArrayList;

public class Server {

    private Simulation simulation;
    private ReservationManager reservationManager;

    public Server(Simulation simulation){
        this.simulation = simulation;
        this.reservationManager = new ReservationManager(simulation.getGridWidth(), simulation.getGridHeight(), this);
    }

    public ArrayList<Robot> getAllRobots(){
        return simulation.getAllRobots();
    }

    public int getGridHeight() {
        return simulation.getGridHeight();
    }

    public int getGridWidth() {
        return simulation.getGridWidth();
    }

    public long getTimeInSeconds() {
        return simulation.getSimulatedTime();
    }

    public long getTimeInTicks(){
        return simulation.getTimeInTicks();
    }

    public ReservationManager getReservationManager(){
        return reservationManager;
    }
}
