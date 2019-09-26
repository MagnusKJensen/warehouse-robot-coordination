package dk.aau.d507e19.warehousesim.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import dk.aau.d507e19.warehousesim.SimulationApp;

public class DesktopLauncher {

	private int screenWidth = 1280, screenHeight = 720;

	public static void main (String[] arg) {
		DesktopLauncher  desktopLauncher = new DesktopLauncher();
		desktopLauncher.startSimulation();
	}

	private void startSimulation(){
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.height = screenHeight;
		config.width = screenWidth;
		LwjglApplication application = new LwjglApplication(new SimulationApp(), config);
	}

	public int getScreenWidth() {
		return screenWidth;
	}

	public int getScreenHeight() {
		return screenHeight;
	}

	public int getAspectRatio(){
		return screenWidth / screenHeight;
	}
}

