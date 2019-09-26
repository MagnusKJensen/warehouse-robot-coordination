package dk.aau.d507e19.warehousesim;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class SimulationApp extends ApplicationAdapter {

	private static final int MENU_WIDTH_IN_PIXELS = 300;
	// Size of a single square in the grid
	private static final int DEFAULT_PIXELS_PER_TILE = 64;

	private ShapeRenderer menuRenderer = new ShapeRenderer();
	private OrthographicCamera menuCamera = new OrthographicCamera();
	private ScreenViewport menuViewport;

	private ScreenViewport simulationViewport;
	private OrthographicCamera simulationCamera = new OrthographicCamera();
	private ShapeRenderer simulationRenderer = new ShapeRenderer();


	// Variables for simulation loop logic
	private UpdateMode updateMode = UpdateMode.MANUAL;


	@Override
	public void create () {
		simulationViewport = new ScreenViewport();
		menuViewport = new ScreenViewport();

		updateMenuScreenSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		updateSimulationScreenSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		centerCamera(simulationCamera);
		centerCamera(menuCamera);
	}

	private void centerCamera(OrthographicCamera camera) {
		camera.position.x = camera.viewportWidth / 2f;
		camera.position.y = camera.viewportHeight / 2f;
	}


	private void updateSimulationScreenSize(int windowWidth, int windowHeight){
		simulationViewport.setScreenWidth(windowWidth - MENU_WIDTH_IN_PIXELS); // todo Fix below zero error
		simulationViewport.setScreenHeight(windowHeight);

		float simCamWidthInTiles = (float) simulationViewport.getScreenWidth() / (float) DEFAULT_PIXELS_PER_TILE;
		float simCamHeightInTiles = (float) simulationViewport.getScreenHeight() / (float) DEFAULT_PIXELS_PER_TILE;

		simulationCamera.viewportWidth = simCamWidthInTiles;
		simulationCamera.viewportHeight = simCamHeightInTiles;
	}

	private void updateMenuScreenSize(int width, int height){
		menuViewport.setScreenWidth(MENU_WIDTH_IN_PIXELS);
		menuViewport.setScreenHeight(height);
		menuViewport.setScreenPosition(width - MENU_WIDTH_IN_PIXELS, 0);

		menuCamera.viewportWidth = MENU_WIDTH_IN_PIXELS;
		menuCamera.viewportHeight = height;
	}

	@Override
	// Called when the simulation window is resized; adjusts the screen height to fit the new aspect ratio
	public void resize(int width, int height) {
		updateSimulationScreenSize(width, height);
		updateMenuScreenSize(width, height);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0,0,0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


		renderMenu();
		renderSimulation();
	}


	private void renderMenu(){
		menuCamera.update();
		menuViewport.apply();
		menuRenderer.setProjectionMatrix(menuCamera.combined);
	}

	private void renderSimulation(){
		simulationCamera.update();
		simulationViewport.apply();
		simulationRenderer.setProjectionMatrix(simulationCamera.combined);
	}

	@Override
	public void dispose () {
		simulationRenderer.dispose();
		menuRenderer.dispose();
	}


	public void startRealTime(){
		updateMode = UpdateMode.REAL_TIME;
	}

	public void pause(){
		updateMode = UpdateMode.MANUAL;
	}

	public void fastForward(){
		updateMode = UpdateMode.FAST_FORWARD;
	}

	public void simulateWithoutGrapihcs(){
		updateMode = UpdateMode.FAST_NO_GRAPHICS;
	}

}
