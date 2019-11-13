package dk.aau.d507e19.warehousesim.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import dk.aau.d507e19.warehousesim.SimulationApp;
import dk.aau.d507e19.warehousesim.controller.robot.Direction;

public class CameraMover implements InputProcessor {

    private final SimulationApp simulationApp;
    private OrthographicCamera camera;
    private ScreenViewport simViewport;

    private static final float CAMERA_MOVEMENT_SPEED = 0.1f; // // TODO: 09/10/2019 Calculate from framerate
    private static final float CAMERA_ZOOM_SPEED = 0.1f; //

    private int lastMouseX, lastMouseY;


    public CameraMover(SimulationApp simulationApp, OrthographicCamera camera, ScreenViewport simViewport) {
        this.camera = camera;
        this.simViewport = simViewport;
        this.simulationApp = simulationApp;
    }

    public void update(){
        if(Gdx.input.isKeyPressed(Input.Keys.UP)){
            moveCamera(Direction.NORTH, CAMERA_MOVEMENT_SPEED);
        }

        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
            moveCamera(Direction.SOUTH, CAMERA_MOVEMENT_SPEED);
        }

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            moveCamera(Direction.WEST, CAMERA_MOVEMENT_SPEED);
        }

        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            moveCamera(Direction.EAST, CAMERA_MOVEMENT_SPEED);
        }
    }

    private void moveCamera(Direction dir, float amount){
        moveCamera(dir.xDir * amount, dir.yDir * amount);
    }

    private void moveCamera(float amountX, float amountY){
        camera.position.x += amountX;
        camera.position.y += amountY;
        simulationApp.refreshCamera();
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

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        lastMouseX = screenX;
        lastMouseY = screenY;
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        float deltaXInTiles = toTiles(lastMouseX - screenX) * camera.zoom;
        float deltaYInTiles = toTiles(screenY - lastMouseY) * camera.zoom;

        moveCamera(deltaXInTiles, deltaYInTiles);

        lastMouseX = screenX;
        lastMouseY = screenY;
        return false;
    }

    private float toTiles(int pixels){
        return pixels * simViewport.getUnitsPerPixel();
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        camera.zoom += CAMERA_ZOOM_SPEED * amount;
        if(camera.zoom < 0.1f) camera.zoom = 0.1f;
        simulationApp.refreshCamera();
        return true;
    }
}
