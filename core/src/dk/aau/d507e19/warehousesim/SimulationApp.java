package dk.aau.d507e19.warehousesim;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
	private static final int LOGIC_TICKRATE = 25;
	private static final long MS_PER_TICK = 1000 / LOGIC_TICKRATE; // todo Should this be nanoseconds for better accuracy
	private static final int FRAME_RATE = 25;

	private UpdateMode updateMode = UpdateMode.MANUAL;
	private long msSinceLastRender = 0L;
	private long lastUpdateTime = 0L;


	//
	private Simulation simulation = new Simulation();
	private SideMenu sideMenu = new SideMenu();

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

		// Render as fast as possible when in fast mode
		if(updateMode == UpdateMode.FAST_NO_GRAPHICS || updateMode == UpdateMode.FAST_FORWARD) {
			updateSimulation();
		}

		long currentTime = System.currentTimeMillis();
		msSinceLastRender += currentTime - lastUpdateTime;
		lastUpdateTime = currentTime;

		// Render/update if enough time has passed since last time we rendered/updated
		if(msSinceLastRender >= MS_PER_TICK){
			msSinceLastRender -= MS_PER_TICK;

			// If the simulation is running in real time then update once every frame
			if(updateMode == UpdateMode.REAL_TIME){
				updateSimulation();
			}

			renderSimulation();
			renderMenu();
			updateMenu();
		}

		renderSimulation();
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
		menuRenderer.setProjectionMatrix(menuCamera.combined);
	}

	private void renderSimulation(){
		simulationCamera.update();
		simulationViewport.apply();
		simulationRenderer.setProjectionMatrix(simulationCamera.combined);
		simulation.render(simulationCamera);
	}

	@Override
	public void dispose () {
		simulationRenderer.dispose();
		menuRenderer.dispose();
	}

	public void startRealTime(){
		updateMode = UpdateMode.REAL_TIME;
		msSinceLastRender = 0L;
	}

	public void pause(){
		updateMode = UpdateMode.MANUAL;
	}

	public void startFastForward(){
		updateMode = UpdateMode.FAST_FORWARD;
		msSinceLastRender = 0L;
	}

	public void simulateWithoutGrapihcs(){
		updateMode = UpdateMode.FAST_NO_GRAPHICS;
		msSinceLastRender = 0L;
	}

}
