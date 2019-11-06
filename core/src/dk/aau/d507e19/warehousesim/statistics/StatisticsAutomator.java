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
    public static final String PATH_TO_RUN_CONFIGS = System.getProperty("user.dir") + File.separator + "core" + File.separator + "assets" + File.separator + "runconfigurations";

    public static void main(String[] args) {
        runAllConfigurations();
    }

    private static void runAllConfigurations(){
        ArrayList<WarehouseSpecs> runConfigs = getAllRunConfigs();
        Simulation simulation = new Simulation("defaultSpecs.json", PathFinderEnum.CHPATHFINDER, TaskAllocatorEnum.DUMMY_TASK_ALLOCATOR);
        simulation.getStatsManager().setPATH_TO_STATS_FOLDER(System.getProperty("user.dir") + File.separator + "core" + File.separator + "assets" + File.separator + "statistics" + File.separator);

        while(simulation.getTimeInTicks() < 100000){
            if(simulation.getTimeInTicks() % 10000 == 0){
                simulation.getStatsManager().printStatistics();
                System.out.println(simulation.getTimeInTicks());
            }
            simulation.update();
        }
    }

    private static ArrayList<WarehouseSpecs> getAllRunConfigs(){
        ArrayList<WarehouseSpecs> runConfigs = new ArrayList<>();

        File runConfigFolder = new File(PATH_TO_RUN_CONFIGS);

        ArrayList<File> files = new ArrayList<>();

        // Get all files in dir
        try {
            Files.list(runConfigFolder.toPath())
                    .forEach(path -> {
                        files.add(path.toFile());
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Deserialize all files from dir
        Gson gson = new Gson();
        for(File runConfig : files){
            try(BufferedReader reader = new BufferedReader(new FileReader(runConfig.getPath()))){
                WarehouseSpecs specs = gson.fromJson(reader, WarehouseSpecs.class);
                runConfigs.add(specs);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return runConfigs;
    }
}
