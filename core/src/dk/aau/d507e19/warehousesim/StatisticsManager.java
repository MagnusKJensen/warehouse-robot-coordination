package dk.aau.d507e19.warehousesim;

import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.server.order.Order;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.RoundingMode;
import java.sql.Time;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class StatisticsManager {
    private String ORDER_STATS_FILENAME = "orderStats_";
    private String ROBOT_STATS_FILENAME = "robotStats_";
    private String GENERAL_STATS_FILENAME = "generalStats_";
    private String PATH_TO_STATS_FOLDER = System.getProperty("user.dir") + File.separator + "statistics" + File.separator;
    private SimulationApp simulationApp;
    SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss'_'dd-MM-yyyy");


    public StatisticsManager(SimulationApp simulationApp) {
        this.simulationApp = simulationApp;
    }

    public void printStatistics(){
        String pathToSimulationFolder = createSimulationFolder();

        writeOrderStatsToFile(pathToSimulationFolder);

        writeRobotStatsToFile(pathToSimulationFolder);

        writeGeneralStatsToFile(pathToSimulationFolder);

    }

    private String createSimulationFolder() {
        // Add folder for this specific simulation run
        String pathToSimulationFolder = PATH_TO_STATS_FOLDER + dateFormatter.format(simulationApp.getSimulationStartTime()) + File.separator;

        File newDirectory = new File(pathToSimulationFolder);
        if(newDirectory.exists()) return pathToSimulationFolder;
        else {
            boolean folderCreated = newDirectory.mkdir();
            if(!folderCreated) {
                throw new IllegalArgumentException("Could not create folder " + newDirectory);
            }
            return pathToSimulationFolder;
        }
    }

    private void writeGeneralStatsToFile(String pathToSimulationFolder) {
        File file = new File(pathToSimulationFolder + GENERAL_STATS_FILENAME + simulationApp.getSimulation().getTimeInTicks() + ".csv");

        // Overwrite if clicked twice
        if(file.exists()){
            file.delete();
        }

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(file.getPath()))){
            // Write header
            String[] header = {",", "Measurement"};
            for(String str : header){
                writer.write(str);
            }
            writer.write("\n");

            long currentTick = simulationApp.getSimulation().getTimeInTicks();
            writer.write("CurrentTick, " + currentTick + '\n');

            long availableProductsLeft = simulationApp.getSimulation().getServer().getProductsAvailable().size();
            writer.write("availableProductsLeft," + availableProductsLeft + '\n');

            int ordersInQueue = simulationApp.getSimulation().getServer().getOrderManager().ordersInQueue();
            writer.write("ordersInQueue," + ordersInQueue + '\n');

            int ordersFinished = simulationApp.getSimulation().getServer().getOrderManager().ordersFinished();
            writer.write("ordersFinished," + ordersFinished + '\n');

            long msSinceStart = simulationApp.getSimulation().getSimulatedTimeInMS();
            double ordersPerMinute = simulationApp.getSimulation().getOrdersProcessed() / ((double) msSinceStart / 1000 / 60);
            NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
            DecimalFormat df = (DecimalFormat) nf;
            df.setRoundingMode(RoundingMode.HALF_UP);
            writer.write("OrdersPerMinute," + df.format(ordersPerMinute) + '\n');

            int tasksInQueue = simulationApp.getSimulation().getServer().getOrderManager().tasksInQueue();
            writer.write("tasksInQueue," + tasksInQueue + '\n');

            String orderGoalReached = simulationApp.getSimulation().getGoal().getStatsAsCSV();
            writer.write("orderGoalReached," + orderGoalReached + '\n');
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeRobotStatsToFile(String pathToSimulationFolder) {
        File file = new File(pathToSimulationFolder + ROBOT_STATS_FILENAME + simulationApp.getSimulation().getTimeInTicks() + ".csv");

        // Overwrite if clicked twice
        if(file.exists()){
            file.delete();
        }

        // Write to file
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(file.getPath()))){
            // Write header
            String[] header = {"ID,", "Deliveries_Completed,", "Distance_traveled_in_meters"};
            for(String str : header){
                writer.write(str);
            }
            writer.write("\n");

            // Write robot stats
            ArrayList<Robot> robots = simulationApp.getSimulation().getAllRobots();
            for(Robot robot : robots){
                writer.write(robot.getStatsAsCSV());
                writer.write("\n");
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    private void writeOrderStatsToFile(String pathToSimulationFolder){
        File file = new File(pathToSimulationFolder + ORDER_STATS_FILENAME + simulationApp.getSimulation().getTimeInTicks() + ".csv");

        // Overwrite if clicked twice
        if(file.exists()){
            file.delete();
        }

        // Write to file
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(file.getPath()))){
            // Write header
            String[] header = {"ID,", "Start_time_in_MS,", "Finish_time_in_MS,", "Process_time_in_MS"};
            for(String str : header){
                writer.write(str);
            }
            writer.write("\n");

            // Write robot stats
            ArrayList<Order> orders = simulationApp.getSimulation().getServer().getOrderManager().getOrdersFinished();
            for(Order order : orders){
                writer.write(order.getStatsAsCSV());
                writer.write("\n");
            }
        }

        catch(IOException e){
            e.printStackTrace();
        }
    }
}
