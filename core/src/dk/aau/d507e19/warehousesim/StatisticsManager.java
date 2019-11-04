package dk.aau.d507e19.warehousesim;

import dk.aau.d507e19.warehousesim.controller.server.order.Order;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class StatisticsManager {
    private String ORDER_STATS_FILENAME = "orderStats_";
    private String ROBOT_STATS_FILENAME = "robotStats_";
    private String PATH_TO_STATS_FOLDER = System.getProperty("user.dir") + File.separator + "statistics" + File.separator;
    private SimulationApp simulationApp;

    public StatisticsManager(SimulationApp simulationApp) {
        this.simulationApp = simulationApp;
    }

    public void printStatistics(){
        writeOrderStatsToFile();

        writeRobotStatsToFile();
    }

    private void writeRobotStatsToFile() {
        File file = new File(PATH_TO_STATS_FOLDER + ROBOT_STATS_FILENAME + simulationApp.getSimulation().getTimeInTicks() + ".txt");

        // Overwrite if clicked twice
        if(file.exists()){
            file.delete();
        }

        // Write to file
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(file.getPath()))){
            writer.write("ROBOTTER"); // do something with the file we've opened

        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    private void writeOrderStatsToFile(){
        File file = new File(PATH_TO_STATS_FOLDER + ORDER_STATS_FILENAME + simulationApp.getSimulation().getTimeInTicks() + ".txt");

        // Overwrite if clicked twice
        if(file.exists()){
            file.delete();
        }

        // Write to file
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(file.getPath()))){
            writer.write(generateOrderStatsString()); // do something with the file we've opened

        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    private String generateOrderStatsString(){
        StringBuilder builder = new StringBuilder();

        ArrayList<Order> orders = simulationApp.getSimulation().getServer().getOrderManager().getOrdersFinished();

        for(Order order : orders){
            builder.append(order);
            builder.append(',');
            builder.append('\n');
        }

        return builder.toString();
    }
}
