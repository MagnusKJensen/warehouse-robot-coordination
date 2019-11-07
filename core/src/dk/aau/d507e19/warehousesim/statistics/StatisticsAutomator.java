package dk.aau.d507e19.warehousesim.statistics;

import dk.aau.d507e19.warehousesim.Simulation;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.PathFinderEnum;
import dk.aau.d507e19.warehousesim.controller.server.taskAllocator.TaskAllocatorEnum;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class StatisticsAutomator {
    public static final String PATH_TO_RUN_CONFIGS = System.getProperty("user.dir") + File.separator + "warehouseconfigurations";
    private static final int TICKS_PER_RUN = 100000;
    private static final int PRINT_EVERY_TICK = 50000;

    public static void main(String[] args) {
        // Run with all configurations
        // runAllConfigurations();

        // Run a single configuration with all taskAllocators and PathFinders.
        runOneConfig("defaultSpecs.json");
    }

    private static void runOneConfig(String configFileName){
        Simulation simulation;

        System.out.println("WarehouseConfig: " + configFileName);
        System.out.println("________________________________________________________________");
        for(TaskAllocatorEnum taskAllocator : TaskAllocatorEnum.values()) {
            for (PathFinderEnum pathFinder : PathFinderEnum.values()) {
                if (taskAllocator.works() && pathFinder.works()) {
                    System.out.println("TaskAllocator: " + taskAllocator.getName() + ", PathFinder: " + pathFinder.getName());
                    simulation = new Simulation(configFileName, pathFinder, taskAllocator);
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

    private static void runAllConfigurations(){
        ArrayList<String> runConfigs = getAllRunConfigs();

        Simulation simulation;
        for(String warehouseConfig : runConfigs){
            System.out.println("WarehouseConfig: " + warehouseConfig);
            System.out.println("________________________________________________________________");
            for(TaskAllocatorEnum taskAllocator : TaskAllocatorEnum.values()){
                for(PathFinderEnum pathFinder : PathFinderEnum.values()){
                    if(taskAllocator.works() && pathFinder.works()){
                        System.out.println("TaskAllocator: " + taskAllocator.getName() + ", PathFinder: " + pathFinder.getName());
                        simulation = new Simulation(warehouseConfig, pathFinder, taskAllocator);
                        while(simulation.getTimeInTicks() <= TICKS_PER_RUN){
                            if(simulation.getTimeInTicks() % PRINT_EVERY_TICK == 0){
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

    private static ArrayList<String> getAllRunConfigs(){
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
