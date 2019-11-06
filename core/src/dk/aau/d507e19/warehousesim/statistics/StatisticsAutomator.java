package dk.aau.d507e19.warehousesim.statistics;

import com.google.gson.Gson;
import dk.aau.d507e19.warehousesim.Simulation;
import dk.aau.d507e19.warehousesim.WarehouseSpecs;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.PathFinderEnum;
import dk.aau.d507e19.warehousesim.controller.server.taskAllocator.TaskAllocatorEnum;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class StatisticsAutomator {
    public static final String PATH_TO_RUN_CONFIGS = System.getProperty("user.dir") + File.separator + "warehouseconfigurations";

    public static void main(String[] args) {
        runAllConfigurations();
    }

    private static void runAllConfigurations(){
        ArrayList<String> runConfigs = getAllRunConfigs();

        Simulation simulation;
        for(String warehouseConfig : runConfigs){
            simulation = new Simulation(warehouseConfig, PathFinderEnum.CHPATHFINDER, TaskAllocatorEnum.DUMMY_TASK_ALLOCATOR);

            while(simulation.getTimeInTicks() <= 100000){
                if(simulation.getTimeInTicks() % 10000 == 0){
                    simulation.getStatsManager().printStatistics();
                    System.out.println(simulation.getTimeInTicks());
                }
                simulation.update();
            }
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
