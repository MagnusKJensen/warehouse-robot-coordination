package dk.aau.d507e19.warehousesim.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import dk.aau.d507e19.warehousesim.Simulation;
import dk.aau.d507e19.warehousesim.UpdateMode;
import dk.aau.d507e19.warehousesim.WarehouseSpecs;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.storagegrid.StorageGrid;
import dk.aau.d507e19.warehousesim.ui.TileInfoMenu;

import java.util.ArrayList;

public class SimulationInputProcessor implements InputProcessor {

    private Simulation simulation;

    public SimulationInputProcessor(Simulation simulation) {
        this.simulation = simulation;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        ArrayList<Robot> robots = simulation.getAllRobots();

        for(Robot robot : robots){
            if(robot.collidesWith(simulation.screenToWorldPosition(screenX, screenY)))
                simulation.selectRobot(robot);
        }

        // Run through all tiles. If one is selected show info in tileMenu
        StorageGrid grid = simulation.getStorageGrid();
        for(int x = 0; x < WarehouseSpecs.wareHouseWidth; x++){
            for(int y = 0; y < WarehouseSpecs.wareHouseHeight; ++y){
                if(grid.getTile(x,y).collidesWith(simulation.screenToWorldPosition(screenX, screenY))){
                    simulation.selectTile(grid.getTile(x,y));
                }
            }
        }

        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.SPACE) {
            if(simulation.getSimulationApp().getUpdateMode() == UpdateMode.MANUAL) simulation.getSimulationApp().play();
            else simulation.getSimulationApp().pause();
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
            return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }
}
