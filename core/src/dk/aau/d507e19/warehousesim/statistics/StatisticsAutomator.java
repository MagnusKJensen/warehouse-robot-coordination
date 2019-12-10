package dk.aau.d507e19.warehousesim.statistics;

import dk.aau.d507e19.warehousesim.Simulation;
import dk.aau.d507e19.warehousesim.SimulationApp;
import dk.aau.d507e19.warehousesim.WarehouseSpecs;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.PathFinderEnum;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.server.taskAllocator.TaskAllocatorEnum;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class StatisticsAutomator {
    public static final String PATH_TO_RUN_CONFIGS = System.getProperty("user.dir") + File.separator + "warehouseconfigurations";
    public static final String PATH_TO_STATISTICS_FOLDER = System.getProperty("user.dir") + File.separator + "statistics";
    private static final int TICKS_PER_RUN = 216000; // 216.000 = 2 timers real time
    private static final int FILE_WRITE_INTERVAL_TICKS = 25000;
    private static final int NUMBER_OF_SEEDS = 8;
    private static final String VERSION_NAME = "v.Deadlock3";
    private static final String SPEC_FILE_NAME = "manyRobots.json";
    private static Random random = new Random(SimulationApp.DEFAULT_SEED);

    public static void main(String[] args) {
        long[] seeds = generateSeeds(NUMBER_OF_SEEDS);

        // Task allocators to use
        ArrayList<TaskAllocatorEnum> taskAllocators = new ArrayList<>(Arrays.asList(
                TaskAllocatorEnum.SMART_ALLOCATOR
        ));

        // Path finders to use
        ArrayList<PathFinderEnum> pathFinders = new ArrayList<>(Arrays.asList(
                PathFinderEnum.CHPATHFINDER
        ));

        WarehouseSpecs specs = Simulation.readWarehouseSpecsFromFile(SPEC_FILE_NAME);

        // Run a single config with the specified taskAllocators and pathfinders
        runConfig(specs, VERSION_NAME, taskAllocators, pathFinders, seeds);

        // Run with some amount of robots in a interval
        // runRobotInterval(specs, VERSION_NAME, taskAllocators, pathFinders, seeds, generateRobotIntervalArray(5, 8));

        // Run with a custom presets of robots
        // This can be used instead of generateRobotIntervalArray to run some specifications with custom amounts of robots.
        ArrayList<Integer> robotNumbers = new ArrayList<>(Arrays.asList(1, 25, 50, 75));
        // runRobotInterval(specs, VERSION_NAME, taskAllocators, pathFinders, seeds, robotNumbers);
    }

    private static ArrayList<Integer> generateRobotIntervalArray(int fewestRobots, int maxRobots){
        ArrayList<Integer> robots = new ArrayList<>();

        for(int robot = fewestRobots; robot <= maxRobots; ++robot){
            robots.add(robot);
        }

        return robots;
    }

    private static void runRobotInterval(WarehouseSpecs specs, String versionName, ArrayList<TaskAllocatorEnum> taskAllocators, ArrayList<PathFinderEnum> pathFinders, long[] seeds, ArrayList<Integer> robotNumbers){
        String versionNameOld = versionName;
        Simulation simulation;
        for(TaskAllocatorEnum taskAllocator : taskAllocators){
            for(PathFinderEnum pathFinder : pathFinders){
                int rowNumber = 1;
                for(Integer robots : robotNumbers){
                    specs.setNumberOfRobots(robots);
                    versionName = versionNameOld + "_" + robots + "robots";
                    int seedNumber = 1;
                    ArrayList<Double> averageOrderTimes = new ArrayList<>();
                    ArrayList<Double> ordersPerMinuteScores = new ArrayList<>();
                    double ultimateSlowestOrder = 0;
                    for(long seed : seeds){
                        System.out.println("TaskAllocator: " + taskAllocator.getName() + ", PathFinder: " + pathFinder.getName()
                                + ", Seed: " + seedNumber++ + "/" + seeds.length + " : " + seed + ", version name: " + versionName);
                        simulation = runSimulationWith(seed, specs.getName(), pathFinder, taskAllocator, specs, versionName);
                        // Add stats about this run for this amount of robots
                        averageOrderTimes.add(simulation.getStatisticsManager().getAverageOrderProcessingTime());
                        ordersPerMinuteScores.add(simulation.getStatisticsManager().getOrdersPerMinute());
                        if(ultimateSlowestOrder < simulation.getStatisticsManager().getSlowestOrder().getTimeSpentOnOrderInSec())
                            ultimateSlowestOrder = simulation.getStatisticsManager().getSlowestOrder().getTimeSpentOnOrderInSec();

                    }
                    String pathToRobotFile = PATH_TO_STATISTICS_FOLDER + File.separator +  "robotInterval_" + specs.getName() + "_" + taskAllocator.getName() + "_" + pathFinder.getName() + ".xlsx";
                    writeRobotStatsToFile(averageOrderTimes, ordersPerMinuteScores,
                            ultimateSlowestOrder, robots,  pathToRobotFile, rowNumber);
                    rowNumber++;
                    generateSeedAveragesForConfig(specs.getName(), versionName, taskAllocator, pathFinder);
                }
            }
        }
    }

    private static void writeRobotStatsToFile(ArrayList<Double> averageOrderTimes, ArrayList<Double> ordersPerMinuteScores,
                                              double ultimateSlowestOrder, int robots, String path, int rowNumber) {
        double averageOrderTime = getAverageValue(averageOrderTimes);
        double ordersPerMinuteAverage = getAverageValue(ordersPerMinuteScores);
        ExcelWriter excelWriter = new ExcelWriter(path);
        excelWriter.updateRobotIntervalFile(robots, rowNumber, averageOrderTime, ordersPerMinuteAverage, ultimateSlowestOrder);
    }

    private static void runConfig(WarehouseSpecs specs, String versionName, ArrayList<TaskAllocatorEnum> taskAllocators, ArrayList<PathFinderEnum> pathFinders, long[] seeds){
        System.out.println("WarehouseConfig: " + specs.getName());
        System.out.println("________________________________________________________________");
        for (TaskAllocatorEnum taskAllocator : taskAllocators) {
            for (PathFinderEnum pathFinder : pathFinders) {
                if (taskAllocator.works() && pathFinder.works()) {
                    int seedNumber = 1;
                    for(long seed : seeds){
                        System.out.println("TaskAllocator: " + taskAllocator.getName() + ", PathFinder: " + pathFinder.getName() + ", Seed: " + seedNumber++ + "/" + seeds.length + " : " + seed + ", version name: " + versionName);
                        runSimulationWith(seed, specs.getName(), pathFinder, taskAllocator, specs, versionName);
                    }
                    generateSeedAveragesForConfig(specs.getName(), versionName, taskAllocator, pathFinder);
                }
            }
        }
    }

    private static Simulation runSimulationWith(long seed, String runConfigName, PathFinderEnum pathFinder, TaskAllocatorEnum taskAllocator, WarehouseSpecs specs, String versionName){
        Simulation simulation = new Simulation(seed, specs.getName(), pathFinder, taskAllocator, specs);
        simulation.getStatisticsManager().setVERSION_NAME(versionName);
        while (simulation.getTimeInTicks() < TICKS_PER_RUN) {
            if (simulation.getTimeInTicks() % FILE_WRITE_INTERVAL_TICKS == 0) {
                simulation.getStatisticsManager().printStatistics();
                System.out.println(simulation.getTimeInTicks());
            }
            simulation.update();
        }
        simulation.getStatisticsManager().addSummaries();

        return simulation;
    }

    private static void generateSeedAveragesForConfig(String configFileName, String versionName, TaskAllocatorEnum taskAllocator, PathFinderEnum pathFinder){
        String pathToConfiguration = System.getProperty("user.dir") + File.separator + "statistics" + File.separator + configFileName + "_" + versionName + File.separator + taskAllocator + "___" + pathFinder;
        File configurationFolder = new File(pathToConfiguration);

        File[] seedFolders = getSubFolders(configurationFolder);
        AllSeedsOverview overview = new AllSeedsOverview();
        for(File seedFolder : seedFolders){
            overview.incrementSeedsVisited();
            File[] statsFiles = seedFolder.listFiles();
            for(File statFile : statsFiles){
                switch (statFile.getName()){
                    case "generalStats.xlsx" :
                        getGeneralStatsFromFile(statFile, overview);
                        break;
                    case "orderStats.xlsx" :
                        getOrderStatsFromFile(statFile, overview);
                        break;
                    case "robotStats.xlsx" :
                        getRobotStatsFromFile(statFile, overview);
                        break;
                }
            }
        }
        String fileName = configFileName + "_" + versionName;
        ExcelWriter excelWriter = new ExcelWriter(pathToConfiguration);
        excelWriter.writeOverviewFile(overview, fileName);

    }

    private static void generateSeedAverages(String configFileName, String versionName){
        File file = new File(System.getProperty("user.dir") + File.separator + "statistics" + File.separator + configFileName + "_" + versionName + File.separator);
        File[] configurationFolders = getSubFolders(file);

        // For each taskAllocator/PathFinder combination
        for(File configFolder : configurationFolders) {
            AllSeedsOverview overview = new AllSeedsOverview();
            File[] seedFolders = getSubFolders(configFolder);
            for(File seedFolder : seedFolders){
                overview.incrementSeedsVisited();
                File[] statsFiles = seedFolder.listFiles();
                for(File statFile : statsFiles){
                    switch (statFile.getName()){
                        case "generalStats.xlsx" :
                            getGeneralStatsFromFile(statFile, overview);
                            break;
                        case "orderStats.xlsx" :
                            getOrderStatsFromFile(statFile, overview);
                            break;
                        case "robotStats.xlsx" :
                            getRobotStatsFromFile(statFile, overview);
                            break;
                    }
                }
            }
            String fileName = configFolder.getName();
            ExcelWriter excelWriter = new ExcelWriter(configFolder.getPath());
            excelWriter.writeOverviewFile(overview, fileName);
        }
    }

    private static void getGeneralStatsFromFile(File statFile, AllSeedsOverview overview) {
        Workbook workbook = ExcelWriter.getOrCreateWorkbook(statFile.getPath());
        Sheet sheet = ExcelWriter.getOrCreateSheet(workbook,"Summary");

        int rowNum = 1;
        Row row;
        // Continue as long as the row exists / is not null.
        do {
            row = sheet.getRow(rowNum);
            switch (row.getCell(0).getStringCellValue()) {
                case "availableProductsLeft":
                    double availableProductsLeft = row.getCell(1).getNumericCellValue();
                    overview.addAvailableProductsAverage(availableProductsLeft);
                    break;
                case "ordersInQueue":
                    double ordersInQueue = row.getCell(1).getNumericCellValue();
                    overview.addOrdersInQueueAverage(ordersInQueue);
                    break;
                case "ordersFinished":
                    double ordersFinished = row.getCell(1).getNumericCellValue();
                    overview.addFinishedOrdersAverage(ordersFinished);
                    break;
                case "OrdersPerMinute":
                    double ordersPerMinute = row.getCell(1).getNumericCellValue();
                    overview.addOrdersPerMinuteAverage(ordersPerMinute);
                    break;
                // These metrics are ignored
                case "OrderGoal":
                case "CurrentTick":
                case "TasksInQueue":
                case "Longest standing task":
                    break;
                default:
                    throw new IllegalArgumentException("'" + row.getCell(0).getStringCellValue() + "' not found in file ' "
                            + statFile.getPath() + "'");
            }
            rowNum++;
        } while (sheet.getRow(rowNum) != null);
    }

    private static void getRobotStatsFromFile(File statFile, AllSeedsOverview overview) {
        Workbook workbook = ExcelWriter.getOrCreateWorkbook(statFile.getPath());
        Sheet sheet = ExcelWriter.getOrCreateSheet(workbook, "Summary");

        int rowNum = 1;
        Row row;
        // Continue as long as the row exists / is not null.
        do {
            row = sheet.getRow(rowNum);
            switch (row.getCell(0).getStringCellValue()) {
                case "Average Distance traveled":
                    double averageDistanceTraveled = row.getCell(1).getNumericCellValue();
                    overview.addAverageDistanceTraveled(averageDistanceTraveled);
                    break;
                case "Shortest distance":
                    double shortestDistanceTraveled = row.getCell(1).getNumericCellValue();
                    overview.addShortestDistanceTraveled(shortestDistanceTraveled);
                    break;
                case "Longest distance":
                    double longestDistance = row.getCell(1).getNumericCellValue();
                    overview.addLongestDistanceTraveled(longestDistance);
                    break;
                case "Least idle":
                    double leastIdle = row.getCell(1).getNumericCellValue();
                    overview.addLeastIdleTime(leastIdle);
                    break;
                case "Most idle":
                    double mostIdle = row.getCell(1).getNumericCellValue();
                    overview.addMostIdleTime(mostIdle);
                    break;
                case "Average idle time":
                    double averageIdle = row.getCell(1).getNumericCellValue();
                    overview.addAverageIdleTimeAverage(averageIdle);
                    break;
                case "Fewest deliveries":
                    double fewestDeliveries = row.getCell(1).getNumericCellValue();
                    overview.addFewestDeliveries((int)fewestDeliveries);
                    break;
                case "Most deliveries":
                    double mostDeliveries = row.getCell(1).getNumericCellValue();
                    overview.addMostDeliveries((int)mostDeliveries);
                    break;
                case "Average deliveries":
                    double averageDeliveries = row.getCell(1).getNumericCellValue();
                    overview.addAverageDeliveriesAverage(averageDeliveries);
                    break;
                // These metrics are ignored
                default:
                    throw new IllegalArgumentException("'" + row.getCell(0).getStringCellValue() + "' not found in file ' "
                            + statFile.getPath() + "'");
            }
            rowNum++;
        } while (sheet.getRow(rowNum) != null);
    }

    private static void getOrderStatsFromFile(File statFile, AllSeedsOverview overview) {
        Workbook workbook = ExcelWriter.getOrCreateWorkbook(statFile.getPath());
        Sheet sheet = ExcelWriter.getOrCreateSheet(workbook, "Summary");

        int rowNum = 1;
        Row row;
        // Continue as long as the row exists / is not null.
        do {
            row = sheet.getRow(rowNum);
            switch (row.getCell(0).getStringCellValue()) {
                case "Quickest order":
                    double quickestOrder = row.getCell(1).getNumericCellValue();
                    overview.addQuickestOrder(quickestOrder);
                    break;
                case "Slowest order":
                    double slowestOrder = row.getCell(1).getNumericCellValue();
                    overview.addSlowestOrder(slowestOrder);
                    break;
                case "Average order time":
                    double averageOrderTime = row.getCell(1).getNumericCellValue();
                    overview.addAverageOrder(averageOrderTime);
                    break;
                default:
                    throw new IllegalArgumentException("'" + row.getCell(0).getStringCellValue() + "' not found in file ' "
                            + statFile.getPath() + "'");
            }
            rowNum++;
        } while (sheet.getRow(rowNum) != null);
    }

    private static File[] getSubFolders(File file) {
        FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        };

        return file.listFiles(filter);
    }

    private static ArrayList<WarehouseSpecs> getAllRunConfigs() {
        ArrayList<WarehouseSpecs> runConfigs = new ArrayList<>();

        File runConfigFolder = new File(PATH_TO_RUN_CONFIGS);

        // Get all files in dir
        try {
            Files.list(runConfigFolder.toPath())
                    .forEach(path -> {
                        runConfigs.add(Simulation.readWarehouseSpecsFromFile(path.getFileName().toString()));
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return runConfigs;
    }

    private static long[] generateSeeds(int numberOfSeeds) {
        long[] seeds = new long[numberOfSeeds];
        for(int i = 0; i < numberOfSeeds; ++i){
            seeds[i] = random.nextLong();
        }
        return seeds;
    }

    private static double getAverageValue(ArrayList<Double> list){
        double sum = 0;
        for(Double d : list){
            sum += d;
        }
        return sum / list.size();
    }
}
