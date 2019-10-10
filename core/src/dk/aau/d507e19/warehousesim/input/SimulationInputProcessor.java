package dk.aau.d507e19.warehousesim.input;

import com.badlogic.gdx.InputProcessor;
import dk.aau.d507e19.warehousesim.Simulation;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;

import java.util.ArrayList;

public class SimulationInputProcessor implements InputProcessor {

    private Simulation simulation;

    private int lastMouseX, lastMouseY;

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
