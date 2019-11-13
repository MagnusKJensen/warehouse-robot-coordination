package dk.aau.d507e19.warehousesim.statistics;

import dk.aau.d507e19.warehousesim.Simulation;
import dk.aau.d507e19.warehousesim.SimulationApp;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.PathFinderEnum;
import dk.aau.d507e19.warehousesim.controller.server.taskAllocator.TaskAllocatorEnum;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class StatisticsAutomator {
    public static final String PATH_TO_RUN_CONFIGS = System.getProperty("user.dir") + File.separator + "warehouseconfigurations";
    private static final int TICKS_PER_RUN = 3000; // 10.000 is about 55min of "real time"
    private static final int PRINT_EVERY_TICK = 300;
    private static final String VERSION_NAME = "versionX";
    private static final String SPEC_FILE_NAME = "defaultSpecs.json";
    private static long[] SEEDS = new long[10];
    private static Random random = new Random(SimulationApp.DEFAULT_SEED);

    public static void main(String[] args) {
        generateSeeds();

        // Task allocators to use
        ArrayList<TaskAllocatorEnum> taskAllocators = new ArrayList<>(Arrays.asList(
                TaskAllocatorEnum.DUMMY_TASK_ALLOCATOR,
                TaskAllocatorEnum.NAIVE_SHORTEST_DISTANCE_TASK_ALLOCATOR
        ));

        // Path finders to use
        ArrayList<PathFinderEnum> pathFinders = new ArrayList<>(Arrays.asList(
                PathFinderEnum.DUMMYPATHFINDER
        ));

        // Run a single config with the specified taskAllocators and pathfinders
        runConfig(SPEC_FILE_NAME, VERSION_NAME, taskAllocators, pathFinders, SEEDS[0], SEEDS[1]);

        // Run with all configurations inside the .../core/assets/warehouseconfigurations/ folder
        // Run with one seed by exchanging 'SEEDS' for SEEDS[0]
        // runAllConfigurations(VERSION_NAME, taskAllocators, pathFinders, SEEDS);
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
