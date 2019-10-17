package dk.aau.d507e19.warehousesim;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import dk.aau.d507e19.warehousesim.input.CameraMover;
import dk.aau.d507e19.warehousesim.ui.SideMenu;

import java.util.Random;

public class SimulationApp extends ApplicationAdapter {

	private static final long RANDOM_SEED = 123456789L;
	public static final Random random = new Random(RANDOM_SEED);

	private static final int MENU_WIDTH_IN_PIXELS = 300;
	// Size of a single square/tile in the grid
	private static final int DEFAULT_PIXELS_PER_TILE = 64;
	private static final int MAX_UPDATES_PER_FRAME = 30;

	private OrthographicCamera menuCamera = new OrthographicCamera();
	private OrthographicCamera simulationCamera = new OrthographicCamera();
	private OrthographicCamera simFontCamera = new OrthographicCamera();

	private ScreenViewport menuViewport;
	private ScreenViewport simulationViewport;

	// Variables for simulation loop logic
	public static final int TICKS_PER_SECOND = 30;
	public static final long MILLIS_PER_TICK = 1000 / TICKS_PER_SECOND;

	private UpdateMode updateMode = UpdateMode.MANUAL;
	private long millisSinceUpdate = 0L;
	private long lastUpdateTime = 0L;

	private static final Color simBGColor = new Color(244f/255f, 245f/255f,247f/255f, 1);

	private Simulation simulation;
	private SideMenu sideMenu;

	private CameraMover cameraMover;
    private InputMultiplexer inputMultiplexer;

	@Override
	public void create () {
        GraphicsManager.loadAssets();

        inputMultiplexer = new InputMultiplexer();
        simulationViewport = new ScreenViewport(simulationCamera);
        simulationViewport.setUnitsPerPixel(1f / (float) DEFAULT_PIXELS_PER_TILE);
		simulationViewport.setUnitsPerPixel(1f / (float) DEFAULT_PIXELS_PER_TILE);

		menuViewport = new ScreenViewport(menuCamera);

		centerCamera(simulationCamera);
		centerCamera(menuCamera);

		simulation = new Simulation(this);
		sideMenu = new SideMenu(menuViewport, this);

		Gdx.input.setInputProcessor(inputMultiplexer);
		cameraMover = new CameraMover(simulationCamera, simulationViewport);

		inputMultiplexer.addProcessor(cameraMover);
		inputMultiplexer.addProcessor(simulation.getInputProcessor());
        lastUpdateTime = System.currentTimeMillis();
	}

	private void centerCamera(OrthographicCamera camera) {
		camera.position.x = camera.viewportWidth / 2f;
		camera.position.y = camera.viewportHeight / 2f;
	}

	@Override
	// Called when the simulation window is resized; adjusts the screen height to fit the new aspect ratio
	public void resize(int width, int height) {
        simulationViewport.update(width - MENU_WIDTH_IN_PIXELS, height);
        menuViewport.update(MENU_WIDTH_IN_PIXELS, height);
	    menuViewport.setScreenX(width - MENU_WIDTH_IN_PIXELS);

		simFontCamera.viewportWidth = simulationViewport.getScreenWidth();
		simFontCamera.viewportHeight = simulationViewport.getScreenHeight();

		//updateSimulationScreenSize(width, height);
		//updateMenuScreenSize(width, height);
		centerCamera(menuCamera);
		centerCamera(simulationCamera); // TODO: 26/09/2019 Add more intelligent system for repositioning camera when resizing
		centerCamera(simFontCamera);
		simFontCamera.update();
	}

	@Override
	// Called repeatedly by the libgdx framework
	public void render () {
		cameraMover.update();
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
		Gdx.gl.glClearColor(simBGColor.r, simBGColor.g, simBGColor.b, simBGColor.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}

	private void updateMenu() {
		sideMenu.update();
	}

	private void updateSimulation(){
		simulation.update();
	}

	private void renderMenu(){
		//cameraMover.update();
		menuCamera.update();
		menuViewport.apply();
		sideMenu.render(menuCamera);
	}

	private void renderSimulation(){
		Gdx.gl.glEnable(GL30.GL_BLEND);
		Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
		simulationCamera.update();
		simFontCamera.update();
		simulationViewport.apply();
		simulation.render(simulationCamera, simFontCamera);
		Gdx.gl.glDisable(GL30.GL_BLEND);

	}

	private void switchUpdateMode(UpdateMode newMode){
		updateMode = newMode;
		millisSinceUpdate = 0L;
		lastUpdateTime = System.currentTimeMillis();
	}

	public void globalStepForward(){
		if(updateMode != UpdateMode.MANUAL)
			throw new IllegalStateException("Update mode must be manual to step forward manually; But current " +
					"update mode is : " + updateMode.name());
		simulation.update();
	}

	public void globalStepBackWard(){
		// TODO: 30/09/2019 Maybe exclude this feature?
	}

	public void pause(){
		switchUpdateMode(UpdateMode.MANUAL);
	}

	public void play(){
		switchUpdateMode(UpdateMode.REAL_TIME);
	}

	public void fastForward() {
		switchUpdateMode(UpdateMode.FAST_FORWARD);
	}

	@Override
	public void dispose() {
		simulation.dispose();
		GraphicsManager.disposeAssetManager();
	}

	public InputMultiplexer getInputMultiplexer() {
		return inputMultiplexer;
	}

	public Simulation getSimulation() {
		return simulation;
	}

	public void resetSimulation() {
		inputMultiplexer.removeProcessor(simulation.getInputProcessor());
		simulation.dispose();

		simulation = new Simulation(this);
		inputMultiplexer.addProcessor(simulation.getInputProcessor());
	}

	public OrthographicCamera getWorldCamera() {
		return simulationCamera;
	}

	public OrthographicCamera getFontCamera() {
		return simFontCamera;
	}

	public ScreenViewport getWorldViewport() {
		return simulationViewport;
	}
}
