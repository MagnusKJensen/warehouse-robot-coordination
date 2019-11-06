package dk.aau.d507e19.warehousesim;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class RunConfigurator {
    public static final String PATH_TO_TEST_RUN_CONFIGS = System.getProperty("user.dir") + File.separator + "testassets" + File.separator + "warehouseconfigurations/";

    public static void setRunConfiguration(String runConfigurationFileName){
        File runConfigFile = new File(PATH_TO_TEST_RUN_CONFIGS + File.separator + runConfigurationFileName);
        Gson gson = new Gson();

        try(BufferedReader reader = new BufferedReader(new FileReader(runConfigFile))){
            WarehouseSpecs specs = gson.fromJson(reader, WarehouseSpecs.class);
            Simulation.setWarehouseSpecs(specs);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setDefaultRunConfiguration(){
        File runConfigFile = new File(PATH_TO_TEST_RUN_CONFIGS + File.separator + "testSpecs.json");
        Gson gson = new Gson();

        try(BufferedReader reader = new BufferedReader(new FileReader(runConfigFile))){
            WarehouseSpecs specs = gson.fromJson(reader, WarehouseSpecs.class);
            Simulation.setWarehouseSpecs(specs);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
