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
    // This changes the name of the folder containing the statistics to reflect the version number.
    // Should be changed in the StatisticsAutomater
    private String VERSION_NAME = "version";

    // For statistics file names
    private final String ORDER_STATS_FILENAME = "orderStats_";
    private final String ROBOT_STATS_FILENAME = "robotStats_";
    private final String GENERAL_STATS_FILENAME = "generalStats_";

    // Path to the upper statistics folder. This should be .../core/assets/statistics/
    private final String PATH_TO_STATS_FOLDER = System.getProperty("user.dir") + File.separator + "statistics" + File.separator;

    // Formatting date and decimals in file names and statistics
    // Has to be ; instead og :, because windows does not accept : in file name - Philip
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("HH;mm;ss'_'dd-MM-yyyy");
    private DecimalFormat decimalFormatter = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);
    private ExcelWriter excelWriter;

    private Simulation simulation;

    public StatisticsManager(Simulation simulation) {
        this.simulation = simulation;

        // Apply patterns to the decimal formatter, that is used in the statistics files
        decimalFormatter.applyPattern("###.00");
        decimalFormatter.setRoundingMode(RoundingMode.HALF_UP);
        decimalFormatter.setGroupingUsed(false);
    }

    public void addSummaries(){
        createStatisticsFolder();

        String runConfigFolder = createRunConfigFolder();

        String pathToSimulationFolder = createSimulationFolder(runConfigFolder);

        this.excelWriter = new ExcelWriter(simulation, pathToSimulationFolder);

        excelWriter.summarizeRobotStats();

        excelWriter.summarizeOrderStats();
    }

    public void printStatistics(){
        // Create statistics folder if it does not exist
        // .../core/assets/statistics/
        createStatisticsFolder();

        // Create folder for current warehouse specs / run config
        // .../core/assets/statistics/*runConfig*_*versionName*/
        String runConfigFolder = createRunConfigFolder();

        // Create folder for this specific simulation run
        // .../core/assets/statistics/*runConfig*_*versionName*/*TaskAllocator___PathFinder*/
        String pathToSimulationFolder = createSimulationFolder(runConfigFolder);

        // .../core/assets/statistics/*runConfig*_*versionName*/*TaskAllocator___PathFinder*/*statsFiles*
        this.excelWriter = new ExcelWriter(simulation, pathToSimulationFolder);
        excelWriter.writeGeneralStats();
        excelWriter.writeOrderStats();
        excelWriter.writeRobotStats();

        // Copy file with specs from the run. Only done, if it is not already copied once.
        copySpecsFile(pathToSimulationFolder);
    }

    private String createSimulationFolder(String runConfigFolder) {
        // Add folder for this specific simulation run
        String simulationFolderName = Simulation.getTaskAllocator() + "___" + Simulation.getPathFinder();

        String pathToSimulationFolder = runConfigFolder + File.separator + simulationFolderName + File.separator;

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

    private void createStatisticsFolder() {
        File statisticsFolder = new File(PATH_TO_STATS_FOLDER);

        if(statisticsFolder.exists()) return;
        else statisticsFolder.mkdir();
    }

    private String createRunConfigFolder() {
        // Add folder for this specific simulation run
        String runConfigFolderName = Simulation.CURRENT_RUN_CONFIG + "_" + VERSION_NAME;

        String pathToRunConfigFolder = PATH_TO_STATS_FOLDER + runConfigFolderName + File.separator;

        File newDirectory = new File(pathToRunConfigFolder);
        if(newDirectory.exists()) return pathToRunConfigFolder;
        else {
            boolean folderCreated = newDirectory.mkdir();
            if(!folderCreated) {
                throw new IllegalArgumentException("Could not create folder " + newDirectory);
            }
            return pathToRunConfigFolder;
        }
    }

    private void copySpecsFile(String pathToSimulationFolder) {
        String configFileToCopy = Simulation.PATH_TO_RUN_CONFIGS + Simulation.CURRENT_RUN_CONFIG;

        String newPath = pathToSimulationFolder + Simulation.CURRENT_RUN_CONFIG;

        try {
            if(!new File(newPath).exists()){
                Files.copy(Paths.get(configFileToCopy), Paths.get(newPath));
            }
        } catch (IOException e) {
            e.printStackTrace();
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

    public String getVERSION_NAME() {
        return VERSION_NAME;
    }

    public void setVERSION_NAME(String VERSION_NAME) {
        this.VERSION_NAME = VERSION_NAME;
    }

    public Robot getRobotWithShortestDistance(){
        Robot shortestDistanceRobot = null;
        for(Robot robot : simulation.getAllRobots()){
            if(shortestDistanceRobot == null){
                shortestDistanceRobot = robot;
                continue;
            }
            if(shortestDistanceRobot.getDistanceTraveledInMeters() > robot.getDistanceTraveledInMeters())
                shortestDistanceRobot = robot;
        }

        return shortestDistanceRobot;
    }

    public int averageDistanceTraveled(){
        ArrayList<Robot> robots = simulation.getAllRobots();
        int numberOfRobots = robots.size();
        int sum = 0;
        for(Robot robot : robots){
            sum += robot.getDistanceTraveledInMeters();
        }

        return sum / numberOfRobots;
    }

    public Robot getRobotWithLongestDistance(){
        Robot longestDistance = null;
        for(Robot robot : simulation.getAllRobots()){
            if(longestDistance == null){
                longestDistance = robot;
                continue;
            }
            if(longestDistance.getDistanceTraveledInMeters() < robot.getDistanceTraveledInMeters())
                longestDistance = robot;
        }

        return longestDistance;
    }
}
