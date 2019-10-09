package dk.aau.d507e19.warehousesim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import dk.aau.d507e19.warehousesim.controller.robot.Direction;

public class CameraMover implements InputProcessor {

    private OrthographicCamera camera;

    private static final float CAMERA_MOVEMENT_SPEED = 0.1f; // // TODO: 09/10/2019 Calculate from framerate
    private static final float CAMERA_ZOOM_SPEED = 0.1f; //

    private int lastMouseX, lastMouseY;


    public CameraMover(OrthographicCamera camera) {
        this.camera = camera;
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
        camera.position.x += dir.xDir * amount;
        camera.position.y += dir.yDir * amount;
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

        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        camera.zoom += CAMERA_ZOOM_SPEED * amount;
        return false;
    }
}
