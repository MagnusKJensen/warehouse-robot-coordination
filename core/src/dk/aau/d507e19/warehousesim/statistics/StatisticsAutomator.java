package dk.aau.d507e19.warehousesim.statistics;

import dk.aau.d507e19.warehousesim.Simulation;
import dk.aau.d507e19.warehousesim.SimulationApp;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.PathFinder;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.PathFinderEnum;
import dk.aau.d507e19.warehousesim.controller.server.taskAllocator.TaskAllocatorEnum;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class StatisticsAutomator {
    public static final String PATH_TO_RUN_CONFIGS = System.getProperty("user.dir") + File.separator + "warehouseconfigurations";
    private static final int TICKS_PER_RUN = 2000; // 10.000 is about 55min per run
    private static final int PRINT_EVERY_TICK = 100;
    private static final String VERSION_NAME = "single";
    private static final String SPEC_FILE_NAME = "defaultSpecs.json";
    private static final long DEFAULT_RANDOM_SEED = SimulationApp.DEFAULT_SEED;

    public static void main(String[] args) {
        // Run with all configurations inside the .../core/assets/warehouseconfigurations/ folder
        // runAllConfigurations(VERSION_NAME);

        // Run a single configuration with all taskAllocators and PathFinders.
        runOneConfig(SPEC_FILE_NAME, VERSION_NAME);

        // Run a single configuration only with only on taskAllocator, but all pathfinders
        // runOneConfig(SPEC_FILE_NAME, VERSION_NAME, TaskAllocatorEnum.DUMMY_TASK_ALLOCATOR);

        // Run a single configuration only with one pathFinder but all taskAllocators
        // runOneConfig(SPEC_FILE_NAME, VERSION_NAME, PathFinderEnum.DUMMYPATHFINDER);

        // Run a single configuration with a singe pathFinder and a single TaskAllocator
        // runOneConfig(SPEC_FILE_NAME, VERSION_NAME, TaskAllocatorEnum.DUMMY_TASK_ALLOCATOR, PathFinderEnum.DUMMYPATHFINDER);
    }

    private static void runOneConfig(String configFileName, String versionName, TaskAllocatorEnum taskAllocator, PathFinderEnum pathFinder) {
        Simulation simulation;

        System.out.println("WarehouseConfig: " + configFileName);
        System.out.println("________________________________________________________________");

        if (taskAllocator.works() && pathFinder.works()) {
            System.out.println("TaskAllocator: " + taskAllocator.getName() + ", PathFinder: " + pathFinder.getName());
            simulation = new Simulation(DEFAULT_RANDOM_SEED, configFileName, pathFinder, taskAllocator);
            simulation.getStatisticsManager().setVERSION_NAME(versionName);
            while (simulation.getTimeInTicks() <= TICKS_PER_RUN) {
                if (simulation.getTimeInTicks() % PRINT_EVERY_TICK == 0) {
                    simulation.getStatisticsManager().printStatistics();
                    System.out.println(simulation.getTimeInTicks());
                }
                simulation.update();
            }
        }
    }

    private static void runOneConfig(String configFileName, String versionName, TaskAllocatorEnum taskAllocator) {
        Simulation simulation;

        System.out.println("WarehouseConfig: " + configFileName);
        System.out.println("________________________________________________________________");
        for (PathFinderEnum pathFinder : PathFinderEnum.values()) {
            if (taskAllocator.works() && pathFinder.works()) {
                System.out.println("TaskAllocator: " + taskAllocator.getName() + ", PathFinder: " + pathFinder.getName());
                simulation = new Simulation(DEFAULT_RANDOM_SEED, configFileName, pathFinder, taskAllocator);
                simulation.getStatisticsManager().setVERSION_NAME(versionName);
                while (simulation.getTimeInTicks() <= TICKS_PER_RUN) {
                    if (simulation.getTimeInTicks() % PRINT_EVERY_TICK == 0) {
                        simulation.getStatisticsManager().printStatistics();
                        System.out.println(simulation.getTimeInTicks());
                    }
                    simulation.update();
                }
            }
        }
    }

    private static void runOneConfig(String configFileName, String versionName, PathFinderEnum pathFinder) {
        Simulation simulation;

        System.out.println("WarehouseConfig: " + configFileName);
        System.out.println("________________________________________________________________");
        for (TaskAllocatorEnum taskAllocator : TaskAllocatorEnum.values()) {
            if (taskAllocator.works() && pathFinder.works()) {
                System.out.println("TaskAllocator: " + taskAllocator.getName() + ", PathFinder: " + pathFinder.getName());
                simulation = new Simulation(DEFAULT_RANDOM_SEED, configFileName, pathFinder, taskAllocator);
                simulation.getStatisticsManager().setVERSION_NAME(versionName);
                while (simulation.getTimeInTicks() <= TICKS_PER_RUN) {
                    if (simulation.getTimeInTicks() % PRINT_EVERY_TICK == 0) {
                        simulation.getStatisticsManager().printStatistics();
                        System.out.println(simulation.getTimeInTicks());
                    }
                    simulation.update();
                }
            }
        }
    }

    private static void runOneConfig(String configFileName, String versionName) {
        Simulation simulation;

        System.out.println("WarehouseConfig: " + configFileName);
        System.out.println("________________________________________________________________");
        for (TaskAllocatorEnum taskAllocator : TaskAllocatorEnum.values()) {
            for (PathFinderEnum pathFinder : PathFinderEnum.values()) {
                if (taskAllocator.works() && pathFinder.works()) {
                    System.out.println("TaskAllocator: " + taskAllocator.getName() + ", PathFinder: " + pathFinder.getName());
                    simulation = new Simulation(DEFAULT_RANDOM_SEED, configFileName, pathFinder, taskAllocator);
                    simulation.getStatisticsManager().setVERSION_NAME(versionName);
                    while (simulation.getTimeInTicks() <= TICKS_PER_RUN) {
                        if (simulation.getTimeInTicks() % PRINT_EVERY_TICK == 0) {
                            simulation.getStatisticsManager().printStatistics();
                            System.out.println(simulation.getTimeInTicks());
                        }
                        simulation.update();
                    }
                }
            }
        }
    }

    private static void runAllConfigurations(String versionName) {
        ArrayList<String> runConfigs = getAllRunConfigs();

        Simulation simulation;
        for (String warehouseConfig : runConfigs) {
            System.out.println("WarehouseConfig: " + warehouseConfig);
            System.out.println("________________________________________________________________");
            for (TaskAllocatorEnum taskAllocator : TaskAllocatorEnum.values()) {
                for (PathFinderEnum pathFinder : PathFinderEnum.values()) {
                    if (taskAllocator.works() && pathFinder.works()) {
                        System.out.println("TaskAllocator: " + taskAllocator.getName() + ", PathFinder: " + pathFinder.getName());
                        simulation = new Simulation(DEFAULT_RANDOM_SEED, warehouseConfig, pathFinder, taskAllocator);
                        simulation.getStatisticsManager().setVERSION_NAME(versionName);
                        while (simulation.getTimeInTicks() <= TICKS_PER_RUN) {
                            if (simulation.getTimeInTicks() % PRINT_EVERY_TICK == 0) {
                                simulation.getStatisticsManager().printStatistics();
                                System.out.println(simulation.getTimeInTicks());
                            }
                            simulation.update();
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
}
