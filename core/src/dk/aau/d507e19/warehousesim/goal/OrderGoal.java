package dk.aau.d507e19.warehousesim.goal;

import dk.aau.d507e19.warehousesim.Simulation;
import dk.aau.d507e19.warehousesim.SimulationApp;
import dk.aau.d507e19.warehousesim.controller.server.Server;

public class OrderGoal implements Goal {
    private int orderGoal;
    private long finishTime;
    private Simulation simulation;
    private boolean done = false;

    public OrderGoal(int orderGoal, Simulation simulation) {
        this.orderGoal = orderGoal;
        this.simulation = simulation;
    }

    @Override
    public boolean isReached() {
        return done;
    }

    @Override
    public long goalReachedAtTime() {
        return finishTime;
    }

    @Override
    public String toString() {
        if(done){
            double finishTimeInSeconds = (double)finishTime / 1000;
            return "OrderGoal of " + orderGoal + "\nReached after " + finishTimeInSeconds + " seconds";
        } else return "OrderGoal of " + orderGoal + "\nNot yet reached.";

    }

    // Call in the simulation update method
    @Override
    public void update() {
        if(simulation.getServer().getOrderManager().ordersFinished() >= orderGoal && !done){
            done = true;
            finishTime = simulation.getSimulatedTimeInMS();
        }
    }

    @Override
    public String getStatsAsCSV() {
        if(done){
            double finishTimeInSeconds = (double)finishTime / 1000;
            return "OrderGoal of " + orderGoal + " Reached after " + finishTimeInSeconds + " seconds";
        } else return "OrderGoal of " + orderGoal + " not yet reached.";
    }
}
