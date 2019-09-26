package dk.aau.d507e19.warehousesim;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class SimulationWindow extends ApplicationAdapter {

	// The amount of tiles shown per row in the simulation when zoom is 1
	private static final float DEFAULT_SCREEN_HEIGHT_IN_TILES = 20f;

	// The camera that shows the simulation
	private OrthographicCamera simulationCamera;
	private ShapeRenderer simulationRenderer;

	// The camera that is used for the interface (menu and buttons)
	private OrthographicCamera interfaceCamera;

	@Override
	public void create () {
		simulationCamera = new OrthographicCamera(getSimulationCameraWidth(), DEFAULT_SCREEN_HEIGHT_IN_TILES);
		simulationCamera.position.x = simulationCamera.viewportWidth / 2f;
		simulationCamera.position.y = simulationCamera.viewportHeight / 2f;
		simulationCamera.update();

		simulationRenderer = new ShapeRenderer();
		simulationRenderer.setProjectionMatrix(simulationCamera.combined);
	}

	private float getSimulationCameraWidth(){
		float aspectRatio = getScreenAspectRatio();
		return DEFAULT_SCREEN_HEIGHT_IN_TILES * aspectRatio;
	}

	private float getScreenAspectRatio(){
		return (float) Gdx.graphics.getWidth() / (float) Gdx.graphics.getHeight();
	}

	@Override
	// Called when the simulation window is resized; adjusts the screen height to fit the new aspect ratio
	public void resize(int width, int height) {
		// TODO: 25/09/2019 Verify that this works with scaling/zooming
		simulationCamera.viewportWidth = getSimulationCameraWidth();
		simulationCamera.update();
	}

	@Override
	public void render () {
		simulationCamera.update();
		// TODO: 25/09/2019 Seperate logic and rendering into independent loops
		simulationRenderer.setProjectionMatrix(simulationCamera.combined);
		Gdx.gl.glClearColor(0,0,0,0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		simulationRenderer.begin(ShapeRenderer.ShapeType.Filled);
		simulationRenderer.setColor(Color.BLUE);
		simulationRenderer.rect(1,1, 1, 1);
		simulationRenderer.end();
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose () {
		simulationRenderer.dispose();
	}
}
