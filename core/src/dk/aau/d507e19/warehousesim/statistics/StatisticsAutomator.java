package dk.aau.d507e19.warehousesim.statistics;

import dk.aau.d507e19.warehousesim.Simulation;
import dk.aau.d507e19.warehousesim.SimulationApp;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.PathFinderEnum;
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
    private static final int TICKS_PER_RUN = 10000; // 10.000 is about 55min of "real time"
    private static final int PRINT_EVERY_TICK = 2500;
    private static final String VERSION_NAME = "bigVersion";
    private static final String SPEC_FILE_NAME = "manyRobots.json";
    private static final int numberOfSeeds = 5;
    private static long[] SEEDS = new long[numberOfSeeds];
    private static Random random = new Random(SimulationApp.DEFAULT_SEED);
    public static final String PATH_TO_RUN_CONFIGS_RESULTS = System.getProperty("user.dir") + File.separator + "statistics" + File.separator + SPEC_FILE_NAME + "_" + VERSION_NAME + File.separator;

    public static void main(String[] args) {
        generateSeeds();

        // Task allocators to use
        ArrayList<TaskAllocatorEnum> taskAllocators = new ArrayList<>(Arrays.asList(
                TaskAllocatorEnum.DUMMY_TASK_ALLOCATOR
                //TaskAllocatorEnum.NAIVE_SHORTEST_DISTANCE_TASK_ALLOCATOR
        ));

        // Path finders to use
        ArrayList<PathFinderEnum> pathFinders = new ArrayList<>(Arrays.asList(
                PathFinderEnum.DUMMYPATHFINDER//,
                //PathFinderEnum.CHPATHFINDER,
                //PathFinderEnum.ASTAR
        ));

        // Run a single config with the specified taskAllocators and pathfinders
        runConfig(SPEC_FILE_NAME, VERSION_NAME, taskAllocators, pathFinders, SEEDS);

        // Run with all configurations inside the .../core/assets/warehouseconfigurations/ folder
        //runAllConfigurations(VERSION_NAME, taskAllocators, pathFinders, SEEDS);
    }

    private static void runConfig(String configFileName, String versionName, ArrayList<TaskAllocatorEnum> taskAllocators, ArrayList<PathFinderEnum> pathFinders, long ...seeds){
        Simulation simulation;

        System.out.println("WarehouseConfig: " + configFileName);
        System.out.println("________________________________________________________________");
        for (TaskAllocatorEnum taskAllocator : taskAllocators) {
            for (PathFinderEnum pathFinder : pathFinders) {
                if (taskAllocator.works() && pathFinder.works()) {
                    for(long seed : seeds){
                        System.out.println("TaskAllocator: " + taskAllocator.getName() + ", PathFinder: " + pathFinder.getName() + ", Seed: " + seed);
                        simulation = new Simulation(seed, configFileName, pathFinder, taskAllocator);
                        simulation.getStatisticsManager().setVERSION_NAME(versionName);
                        while (simulation.getTimeInTicks() < TICKS_PER_RUN) {
                            if (simulation.getTimeInTicks() % PRINT_EVERY_TICK == 0) {
                                simulation.getStatisticsManager().printStatistics();
                                System.out.println(simulation.getTimeInTicks());
                            }
                            simulation.update();
                        }
                        simulation.getStatisticsManager().addSummaries();
                    }
                }
            }
        }
        generateSeedAverages();
    }

    private static void runAllConfigurations(String versionName, ArrayList<TaskAllocatorEnum> taskAllocators, ArrayList<PathFinderEnum> pathFinders, long ...seeds) {
        ArrayList<String> runConfigs = getAllRunConfigs();

        Simulation simulation;
        for (String warehouseConfig : runConfigs) {
            System.out.println("WarehouseConfig: " + warehouseConfig);
            System.out.println("________________________________________________________________");
            for (TaskAllocatorEnum taskAllocator : taskAllocators) {
                for (PathFinderEnum pathFinder : pathFinders) {
                    if (taskAllocator.works() && pathFinder.works()) {
                        for(long seed : seeds){
                            System.out.println("TaskAllocator: " + taskAllocator.getName() + ", PathFinder: " + pathFinder.getName() + ", Seed: " + seed);
                            simulation = new Simulation(seed, warehouseConfig, pathFinder, taskAllocator);
                            simulation.getStatisticsManager().setVERSION_NAME(versionName);
                            while (simulation.getTimeInTicks() < TICKS_PER_RUN) {
                                if (simulation.getTimeInTicks() % PRINT_EVERY_TICK == 0) {
                                    simulation.getStatisticsManager().printStatistics();
                                    System.out.println(simulation.getTimeInTicks());
                                }
                                simulation.update();
                            }
                            simulation.getStatisticsManager().addSummaries();
                        }
                    }
                }
            }
            System.out.println("\n");
        }
        generateSeedAverages();
    }

    private static void generateSeedAverages(){
        double availableProductsLeftAverage;

        File file = new File(PATH_TO_RUN_CONFIGS_RESULTS);
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

    private static ArrayList<String> getAllRunConfigs() {
        ArrayList<String> runConfigs = new ArrayList<>();

        File runConfigFolder = new File(PATH_TO_RUN_CONFIGS);

        // Get all files in dir
        try {
            Files.list(runConfigFolder.toPath())
                    .forEach(path -> {
                        runConfigs.add(path.getFileName().toString());
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return runConfigs;
    }

    private static void generateSeeds() {
        for(int i = 0; i < SEEDS.length; ++i){
            SEEDS[i] = random.nextLong();
        }
    }
}
