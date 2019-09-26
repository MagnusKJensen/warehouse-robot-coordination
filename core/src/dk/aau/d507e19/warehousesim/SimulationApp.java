package dk.aau.d507e19.warehousesim;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class SimulationApp extends ApplicationAdapter {

	private static final int MENU_WIDTH_IN_PIXELS = 300;
	// Size of a single square/tile in the grid
	private static final int DEFAULT_PIXELS_PER_TILE = 64;
	private static final int MAX_UPDATES_PER_FRAME = 10;

	private OrthographicCamera menuCamera = new OrthographicCamera();
	private OrthographicCamera simulationCamera = new OrthographicCamera();

	private ScreenViewport menuViewport;
	private ScreenViewport simulationViewport;

	// Variables for simulation loop logic
	private static final int TICKS_PER_SECOND = 20;
	private static final long MILLIS_PER_TICK = 1000 / TICKS_PER_SECOND;

	private UpdateMode updateMode = UpdateMode.REAL_TIME;
	private long millisSinceUpdate = 0L;
	private long lastUpdateTime = 0L;


	//
	private Simulation simulation;
	private SideMenu sideMenu;

	@Override
	public void create () {
		simulation = new Simulation();
		sideMenu = new SideMenu();

		simulationViewport = new ScreenViewport();
		menuViewport = new ScreenViewport();

		updateMenuScreenSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		updateSimulationScreenSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		centerCamera(simulationCamera);
		centerCamera(menuCamera);
		lastUpdateTime = System.currentTimeMillis();
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
		centerCamera(menuCamera);
		centerCamera(simulationCamera); // TODO: 26/09/2019 Add more intelligent system for repositioning camera when resizing
	}


	@Override
	// Called repeatedly by the libgdx framework
	public void render () {
		int updatesSinceLastRender = 0;
		while(shouldUpdateSimulation() && updatesSinceLastRender < MAX_UPDATES_PER_FRAME){
			simulation.update();
			updatesSinceLastRender++;
		}

		updateMenu();
		clearScreen();
		renderMenu();
		if(updateMode != UpdateMode.FAST_NO_GRAPHICS)
			renderSimulation();
	}

	// Determines whether it is time to update to simulation
	// by comparing the time that has passed
	private boolean shouldUpdateSimulation(){
		// Always update when in fast mode
		if(updateMode == UpdateMode.FAST_FORWARD || updateMode == UpdateMode.FAST_NO_GRAPHICS)
			return true;

		// don't automatically update when in manual mode
		if(updateMode == UpdateMode.MANUAL)
			return false;

		long currentTime = System.currentTimeMillis();
		millisSinceUpdate += currentTime - lastUpdateTime;
		lastUpdateTime = currentTime;

		// It is only time to update if enough time has passed since last time we rendered/updated
		if(millisSinceUpdate >= MILLIS_PER_TICK) {
			millisSinceUpdate -= MILLIS_PER_TICK;
			return true;
		}
		return false;
	}

	private void clearScreen() {
		Gdx.gl.glClearColor(0, 0,0,0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}

	private void updateMenu() {
		sideMenu.update();
	}

	private void updateSimulation(){
		simulation.update();
	}

	private void renderMenu(){
		menuCamera.update();
		menuViewport.apply();
		sideMenu.render(menuCamera);
	}

	private void renderSimulation(){
		simulationCamera.update();
		simulationViewport.apply();
		simulation.render(simulationCamera);
	}

	public void switchUpdateMode(UpdateMode newMode){
		updateMode = newMode;
		millisSinceUpdate = 0L;
	}

}
