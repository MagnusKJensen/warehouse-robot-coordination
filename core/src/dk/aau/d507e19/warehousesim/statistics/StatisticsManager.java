package dk.aau.d507e19.warehousesim.statistics;

import dk.aau.d507e19.warehousesim.Simulation;
import dk.aau.d507e19.warehousesim.SimulationApp;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.server.order.Order;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    private Simulation simulation;
    // Has to be ; instead og :, because windows does not accept : in file name - Philip
    SimpleDateFormat dateFormatter = new SimpleDateFormat("HH;mm;ss'_'dd-MM-yyyy");
    DecimalFormat decimalFormatter = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);


    public StatisticsManager(Simulation simulation) {
        this.simulation = simulation;
    }

    public void printStatistics(){
        decimalFormatter.applyPattern("###.00");
        decimalFormatter.setRoundingMode(RoundingMode.HALF_UP);
        decimalFormatter.setGroupingUsed(false);
        // Create statistics folder if it does not exist
        createStatisticsFolder();

        // Create folder for this specific simulation run
        String pathToSimulationFolder = createSimulationFolder();

        // Write all statistics to files
        writeOrderStatsToFile(pathToSimulationFolder);
        writeRobotStatsToFile(pathToSimulationFolder);
        writeGeneralStatsToFile(pathToSimulationFolder);

        // Copy file with specs from the run. Only done, if it is not already copied once.
        copySpecsFile(pathToSimulationFolder);
    }

    private void copySpecsFile(String pathToSimulationFolder) {
        String pathToSpecFile = SimulationApp.PATH_TO_RUN_CONFIGS + SimulationApp.CURRENT_RUN_CONFIG;

        String pathToNewFile = pathToSimulationFolder + File.separator + SimulationApp.CURRENT_RUN_CONFIG;
        try {
            if(!new File(pathToNewFile).exists()){
                Files.copy(Paths.get(pathToSpecFile), Paths.get(pathToNewFile));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createStatisticsFolder() {
        File statisticsFolder = new File(PATH_TO_STATS_FOLDER);

        if(statisticsFolder.exists()) return;
        else statisticsFolder.mkdir();
    }

    private String createSimulationFolder() {
        // Add folder for this specific simulation run
        String pathToSimulationFolder = PATH_TO_STATS_FOLDER + dateFormatter.format(simulation.getSimulationStartTime()) + File.separator;

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
        File file = new File(pathToSimulationFolder + GENERAL_STATS_FILENAME + simulation.getTimeInTicks() + ".csv");

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

            long currentTick = simulation.getTimeInTicks();
            writer.write("CurrentTick, " + currentTick + '\n');

            long availableProductsLeft = simulation.getServer().getProductsAvailable().size();
            writer.write("availableProductsLeft," + availableProductsLeft + '\n');

            int ordersInQueue = simulation.getServer().getOrderManager().ordersInQueue();
            writer.write("ordersInQueue," + ordersInQueue + '\n');

            int ordersFinished = simulation.getServer().getOrderManager().ordersFinished();
            writer.write("ordersFinished," + ordersFinished + '\n');

            long msSinceStart = simulation.getSimulatedTimeInMS();
            double ordersPerMinute = simulation.getOrdersProcessed() / ((double) msSinceStart / 1000 / 60);

            writer.write("OrdersPerMinute," + decimalFormatter.format(ordersPerMinute) + '\n');

            int tasksInQueue = simulation.getServer().getOrderManager().tasksInQueue();
            writer.write("tasksInQueue," + tasksInQueue + '\n');

            String orderGoalReached = simulation.getGoal().getStatsAsCSV();
            writer.write("orderGoalReached," + orderGoalReached + '\n');
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeRobotStatsToFile(String pathToSimulationFolder) {
        File file = new File(pathToSimulationFolder + ROBOT_STATS_FILENAME + simulation.getTimeInTicks() + ".csv");

        // Overwrite if clicked twice
        if(file.exists()){
            file.delete();
        }

        // Write to file
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(file.getPath()))){
            // Write header
            String[] header = {"ID,", "Deliveries_Completed,", "Distance_traveled_in_meters,", "IdleTimeInSeconds"};
            for(String str : header){
                writer.write(str);
            }
            writer.write("\n");

            // Write robot stats
            ArrayList<Robot> robots = simulation.getAllRobots();
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
        File file = new File(pathToSimulationFolder + ORDER_STATS_FILENAME + simulation.getTimeInTicks() + ".csv");

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
            ArrayList<Order> orders = simulation.getServer().getOrderManager().getOrdersFinished();
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
